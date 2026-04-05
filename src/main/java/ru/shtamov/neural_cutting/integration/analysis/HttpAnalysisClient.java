package ru.shtamov.neural_cutting.integration.analysis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.shtamov.neural_cutting.exception.ExternalServiceException;

import java.net.http.HttpClient;

@Component
@ConditionalOnProperty(prefix = "app.analysis", name = "mode", havingValue = "http")
public class HttpAnalysisClient implements AnalysisClient {

    private final RestClient restClient;
    private final AnalysisClientProperties properties;

    public HttpAnalysisClient(RestClient.Builder restClientBuilder, AnalysisClientProperties properties) {
        this.properties = properties;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public ExternalAnalysisResponse analyze(ExternalAnalysisRequest request) {
        try {
            ExternalAnalysisResponse response = restClient.post()
                    .uri(properties.endpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ExternalAnalysisResponse.class);

            if (response == null) {
                throw new ExternalServiceException("Analysis service returned an empty response");
            }
            return response;
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Analysis service request failed", exception);
        }
    }
}
