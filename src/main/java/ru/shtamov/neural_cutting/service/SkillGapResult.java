package ru.shtamov.neural_cutting.service;

import java.util.Set;

/**
 * Result of a skill gap analysis comparing resume skills against vacancy requirements.
 *
 * @param matchPercent percentage of vacancy skills present in resume (0-100)
 * @param matchedSkills skills present in both resume and vacancy
 * @param missingSkills skills required by vacancy but not present in resume
 * @param resumeSkills all skills found in resume
 * @param vacancySkills all skills found in vacancy
 */
public record SkillGapResult(
        int matchPercent,
        Set<ExtractedSkill> matchedSkills,
        Set<ExtractedSkill> missingSkills,
        Set<ExtractedSkill> resumeSkills,
        Set<ExtractedSkill> vacancySkills
) {

    public static SkillGapResult empty() {
        return new SkillGapResult(0, Set.of(), Set.of(), Set.of(), Set.of());
    }
}
