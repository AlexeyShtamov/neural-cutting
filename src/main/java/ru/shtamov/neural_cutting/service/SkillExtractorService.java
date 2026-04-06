package ru.shtamov.neural_cutting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shtamov.neural_cutting.domain.Skill;
import ru.shtamov.neural_cutting.domain.SkillAlias;
import ru.shtamov.neural_cutting.domain.enums.SkillCategory;
import ru.shtamov.neural_cutting.repository.SkillAliasRepository;
import ru.shtamov.neural_cutting.repository.SkillRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for extracting skills from text using dictionary matching, context patterns,
 * and normalization.
 */
@Service
@Slf4j
public class SkillExtractorService {

    private static final int MIN_SKILL_LENGTH = 2;
    private static final int MAX_SKILL_LENGTH = 50;

    // Context patterns that indicate a skill is being mentioned
    private static final Set<Pattern> CONTEXT_PATTERNS = Set.of(
            // Russian patterns
            Pattern.compile("опыт\\s+(работы\\s+с|использования|владения)\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("знание\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("знания\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("навыки\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("стек\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("технологии\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("технологии[\\s\\w]*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("работа\\s+с\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("работал\\s+с\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            // English patterns
            Pattern.compile("experience\\s+(with|in|using)\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("knowledge\\s+(of|in)\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("skills\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("stack\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("technologies\\s*:?\\s*([\\w\\s\\-\\.,]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("proficient\\s+(in|with)\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("familiar\\s+(with)\\s+([\\w\\s\\-\\.]+)", Pattern.CASE_INSENSITIVE)
    );

    // Stop words to filter out from extracted text
    private static final Set<String> STOP_WORDS = Set.of(
            "and", "the", "for", "with", "that", "this", "have", "from", "your", "about",
            "into", "will", "more", "than", "such", "some", "them", "they", "which",
            "и", "в", "на", "с", "по", "для", "от", "к", "об", "за", "из", "не", "или",
            "как", "так", "все", "это", "тот", "его", "ее", "их", "мы", "вы", "он", "она",
            "a", "an", "is", "are", "was", "were", "be", "been", "being", "has",
            "had", "do", "does", "did", "would", "could", "should", "may", "might",
            "must", "shall", "can", "need", "dare", "ought", "used", "to", "of", "in"
    );

    private final SkillRepository skillRepository;
    private final SkillAliasRepository skillAliasRepository;

    // In-memory cache for faster lookups
    private Map<String, Skill> skillCache;
    private Map<String, Skill> aliasCache;

    public SkillExtractorService(SkillRepository skillRepository, SkillAliasRepository skillAliasRepository) {
        this.skillRepository = skillRepository;
        this.skillAliasRepository = skillAliasRepository;
    }

    /**
     * Extract skills from the given text.
     *
     * @param text the text to analyze
     * @return set of extracted skills
     */
    @Transactional(readOnly = true)
    public Set<ExtractedSkill> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        // Initialize cache on first use
        initializeCacheIfNeeded();

        Set<ExtractedSkill> skills = new LinkedHashSet<>();

        // Step 1: Context-aware extraction
        extractWithContextPatterns(text, skills);

        // Step 2: Direct dictionary matching
        extractWithDictionaryMatching(text, skills);

        log.debug("Extracted {} skills from text of length {}", skills.size(), text.length());
        return skills;
    }

    /**
     * Perform skill gap analysis between resume and vacancy.
     *
     * @param resumeText the resume text
     * @param vacancyText the vacancy text
     * @return the gap analysis result
     */
    public SkillGapResult analyzeGap(String resumeText, String vacancyText) {
        Set<ExtractedSkill> resumeSkills = extractSkills(resumeText);
        Set<ExtractedSkill> vacancySkills = extractSkills(vacancyText);

        if (vacancySkills.isEmpty()) {
            return new SkillGapResult(100, resumeSkills, Set.of(), resumeSkills, vacancySkills);
        }

        // Find matched skills (present in both)
        Set<String> vacancySkillNames = vacancySkills.stream()
                .map(ExtractedSkill::name)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<ExtractedSkill> matchedSkills = resumeSkills.stream()
                .filter(skill -> vacancySkillNames.contains(skill.name().toLowerCase()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Find missing skills (in vacancy but not in resume)
        Set<String> resumeSkillNames = resumeSkills.stream()
                .map(ExtractedSkill::name)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<ExtractedSkill> missingSkills = vacancySkills.stream()
                .filter(skill -> !resumeSkillNames.contains(skill.name().toLowerCase()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Calculate match percentage
        int matchPercent = (int) ((matchedSkills.size() * 100.0) / vacancySkills.size());

        log.info("Skill gap analysis: {}% match, {} matched, {} missing",
                matchPercent, matchedSkills.size(), missingSkills.size());

        return new SkillGapResult(matchPercent, matchedSkills, missingSkills, resumeSkills, vacancySkills);
    }

    private void initializeCacheIfNeeded() {
        if (skillCache == null) {
            skillCache = skillRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            Skill::getNormalizedName,
                            skill -> skill,
                            (existing, replacement) -> existing
                    ));

            aliasCache = skillAliasRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            SkillAlias::getNormalizedAlias,
                            SkillAlias::getSkill,
                            (existing, replacement) -> existing
                    ));

            log.debug("Initialized skill cache: {} skills, {} aliases", skillCache.size(), aliasCache.size());
        }
    }

    private void extractWithContextPatterns(String text, Set<ExtractedSkill> skills) {
        for (Pattern pattern : CONTEXT_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                // Get the last group (the skill part)
                String skillText = matcher.groupCount() >= 2 ? matcher.group(matcher.groupCount()) : matcher.group(1);
                processSkillText(skillText, skills);
            }
        }
    }

    private void extractWithDictionaryMatching(String text, Set<ExtractedSkill> skills) {
        // Split text into tokens and check each against dictionary
        String[] tokens = text.split("[\\s,;\\-–—/\\\\()\\[\\]{}\"']+");

        for (String token : tokens) {
            if (token.length() < MIN_SKILL_LENGTH || token.length() > MAX_SKILL_LENGTH) {
                continue;
            }

            String normalized = normalizeToken(token);
            if (normalized.isEmpty() || STOP_WORDS.contains(normalized)) {
                continue;
            }

            findSkillInDictionary(token, normalized).ifPresent(skill -> {
                skills.add(new ExtractedSkill(
                        skill.getName(),
                        skill.getCategory(),
                        token
                ));
            });
        }

        // Also check for multi-word skills
        extractMultiWordSkills(text, skills);
    }

    private void extractMultiWordSkills(String text, Set<ExtractedSkill> skills) {
        // Check for common multi-word skill patterns
        String[] sentences = text.split("[.!?\\n]");
        for (String sentence : sentences) {
            for (String multiWordSkill : skillCache.keySet()) {
                if (multiWordSkill.contains(" ")) {
                    // Case-insensitive search for multi-word skills
                    Pattern skillPattern = Pattern.compile(
                            Pattern.quote(multiWordSkill),
                            Pattern.CASE_INSENSITIVE
                    );
                    Matcher matcher = skillPattern.matcher(sentence);
                    if (matcher.find()) {
                        Skill skill = skillCache.get(multiWordSkill);
                        skills.add(new ExtractedSkill(
                                skill.getName(),
                                skill.getCategory(),
                                matcher.group()
                        ));
                    }
                }
            }
        }
    }

    private Optional<Skill> findSkillInDictionary(String originalToken, String normalizedToken) {
        // Check exact match in skills
        Skill skill = skillCache.get(normalizedToken);
        if (skill != null) {
            return Optional.of(skill);
        }

        // Check alias match
        skill = aliasCache.get(normalizedToken);
        if (skill != null) {
            return Optional.of(skill);
        }

        return Optional.empty();
    }

    private void processSkillText(String skillText, Set<ExtractedSkill> skills) {
        // Split by common delimiters
        String[] potentialSkills = skillText.split("[,;\\-–—/\\\\&]+");

        for (String potential : potentialSkills) {
            String trimmed = potential.trim();
            if (trimmed.length() < MIN_SKILL_LENGTH || trimmed.length() > MAX_SKILL_LENGTH) {
                continue;
            }

            String normalized = normalizeToken(trimmed);
            if (normalized.isEmpty() || STOP_WORDS.contains(normalized)) {
                continue;
            }

            findSkillInDictionary(trimmed, normalized).ifPresent(skill -> {
                skills.add(new ExtractedSkill(
                        skill.getName(),
                        skill.getCategory(),
                        trimmed
                ));
            });
        }
    }

    private String normalizeToken(String token) {
        return token.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Clear the internal cache (useful for testing or after data updates).
     */
    public void clearCache() {
        skillCache = null;
        aliasCache = null;
    }
}
