package ru.shtamov.neural_cutting.integration.vacancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.shtamov.neural_cutting.exception.ExternalServiceException;

import java.net.http.HttpClient;
import java.util.Optional;

/**
 * Adapter implementation for HH.ru API.
 * Provides caching for both individual vacancy lookups and search results.
 */
@Component
@ConditionalOnProperty(prefix = "app.hh-ru", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HhRuVacancyProvider implements VacancyProvider {

    private static final String VACANCIES_PATH = "/vacancies";
    private static final String CACHE_VACANCIES = "hh-ru-vacancies";
    private static final String CACHE_SEARCH = "hh-ru-search";

    private final RestClient restClient;
    private final HhRuProperties properties;

    public HhRuVacancyProvider(RestClient.Builder restClientBuilder, HhRuProperties properties) {
        this.properties = properties;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .defaultHeader("User-Agent", properties.userAgent())
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    @Cacheable(value = CACHE_VACANCIES, key = "#vacancyId")
    public Optional<HhRuVacancy> findById(String vacancyId) {
        try {
            HhRuVacancy vacancy = restClient.get()
                    .uri(VACANCIES_PATH + "/{id}", vacancyId)
                    .retrieve()
                    .body(HhRuVacancy.class);
            return Optional.ofNullable(vacancy);
        } catch (RestClientException exception) {
            if (isNotFoundError(exception)) {
                return Optional.empty();
            }
            throw new HhRuApiException("Failed to fetch vacancy from HH.ru: " + vacancyId, exception);
        }
    }

    @Override
    @Cacheable(value = CACHE_SEARCH, key = "#query + '-' + #areaId + '-' + #limit")
    public HhRuSearchResponse search(String query, String areaId, int limit) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(VACANCIES_PATH)
                                .queryParam("text", query)
                                .queryParam("per_page", limit);
                        if (areaId != null && !areaId.isBlank()) {
                            uriBuilder.queryParam("area", areaId);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(HhRuSearchResponse.class);
        } catch (RestClientException exception) {
            if (isRateLimited(exception)) {
                throw new HhRuApiException("HH.ru API rate limit exceeded", exception, 429);
            }
            throw new HhRuApiException("Failed to search vacancies on HH.ru", exception);
        }
    }

    private boolean isNotFoundError(RestClientException exception) {
        return exception.getMessage() != null && exception.getMessage().contains("404");
    }

    private boolean isRateLimited(RestClientException exception) {
        return exception.getMessage() != null && exception.getMessage().contains("429");
    }
}
