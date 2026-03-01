package ru.shtamov.neural_cutting.domain.enums;


public enum RecommendationType {
    REWRITE("Переписать"),
    ADD("Добавить"),
    REMOVE("Удалить"),
    REORDER("Переставить"),
    QUANTIFY("Добавить количественные показатели"),
    TAILOR("Адаптировать под вакансию"),
    FIX("Исправить");

    RecommendationType(String name) {
    }
}
