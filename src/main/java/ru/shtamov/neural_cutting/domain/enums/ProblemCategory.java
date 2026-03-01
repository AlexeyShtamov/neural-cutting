package ru.shtamov.neural_cutting.domain.enums;

public enum ProblemCategory {
    STRUCTURE("Структура"),
    IMPACT("Влияние"),
    CLARITY("Ясность"),
    SKILLS("Навыки"),
    GRAMMAR("Грамматика"),
    ATS("Соответствие ATS"),
    TRUTHFULNESS("Достоверность");


    ProblemCategory(String label) {
    }

}
