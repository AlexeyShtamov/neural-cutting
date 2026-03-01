package ru.shtamov.neural_cutting.domain.enums;

import org.apache.kafka.common.protocol.types.Field;

public enum ProblemSection {
    HEADER("Заголовок"),
    SUMMARY("О себе"),
    EXPERIENCE("Опыт работы"),
    SKILLS("Навыки"),
    EDUCATION("Образование"),
    PROJECTS("Проекты"),
    OTHER("Другое");

    ProblemSection(String name) {
    }
}
