package ru.shtamov.neural_cutting.integration.vacancy;

import java.util.Optional;

/**
 * Interface for the Adapter pattern to fetch vacancy data from external providers.
 */
public interface VacancyProvider {

    /**
     * Fetch a vacancy by its ID.
     *
     * @param vacancyId the external vacancy identifier
     * @return the vacancy data, or empty if not found
     */
    Optional<HhRuVacancy> findById(String vacancyId);

    /**
     * Search for vacancies matching the given criteria.
     *
     * @param query the search query
     * @param areaId the geographic area ID (optional)
     * @param limit maximum number of results
     * @return search response containing matching vacancies
     */
    HhRuSearchResponse search(String query, String areaId, int limit);
}
