package ru.shtamov.neural_cutting.integration.analysis;

public interface AnalysisClient {

    ExternalAnalysisResponse analyze(ExternalAnalysisRequest request);
}
