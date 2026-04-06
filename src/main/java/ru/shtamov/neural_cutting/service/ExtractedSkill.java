package ru.shtamov.neural_cutting.service;

import ru.shtamov.neural_cutting.domain.enums.SkillCategory;

/**
 * Represents a skill extracted from text.
 *
 * @param name the canonical/normalized name of the skill
 * @param category the category of the skill
 * @param originalText the original text that was matched
 */
public record ExtractedSkill(
        String name,
        SkillCategory category,
        String originalText
) {
}
