package ru.shtamov.neural_cutting.domain.enums;

public enum AnalysisJobStatus {
    IN_PROGRESS("В процессе"),
    FINISHED("Закончена"),
    PAUSED("Остановлена"),
    WAITING_START("Ждет запуска"),
    CRUSHED("Сломана");

    AnalysisJobStatus(String title){}
}
