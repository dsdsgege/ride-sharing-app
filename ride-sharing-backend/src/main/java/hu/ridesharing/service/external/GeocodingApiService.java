package hu.ridesharing.service.external;

import hu.ridesharing.dto.response.incoming.GeocodingReverseResponse;
import hu.ridesharing.entity.GeocodingResponse;
import hu.ridesharing.repository.GeocodingResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
public class GeocodingApiService {

    private final RestClient restClient;
    private final GeocodingResponseRepository geocodingResponseRepository;

    private final String API_KEY;

    public GeocodingApiService(@Value("${geocoding.api.key}") String apiKey, GeocodingResponseRepository geocodingResponseRepository) {
        this.API_KEY = apiKey;
        restClient = RestClient.builder().build();
        this.geocodingResponseRepository = geocodingResponseRepository;
    }

    public GeocodingReverseResponse[] getReverse(double latitude, double longitude) {
        URI uri = UriComponentsBuilder.fromUriString("http://api.openweathermap.org/geo/1.0/" +
                "reverse?lat={lat}&lon={lon}&limit=1&appid={appid}")
                .build(latitude, longitude, API_KEY);
        log.debug("URI: {}", uri);

        return restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(response -> {
                    log.debug("Response: {}\n {}\n {}", response.getStatusCode(), response.getStatusText(),
                            response.getHeaders());
                    return true;
                })
                .body(GeocodingReverseResponse[].class);
    }

    public GeocodingResponse[] getGeocoding(String city) {
        Optional<GeocodingResponse> response = geocodingResponseRepository.findById(city);
        if (response.isPresent()) {
            log.debug("Found in cache: {}", response.get());
            return new GeocodingResponse[]{ response.get() };
        }

        URI uri = UriComponentsBuilder.fromUriString("http://api.openweathermap.org/geo/1.0/direct?q={q}&appid={appid}")
                .build(city, API_KEY);
        log.debug("URI: {}", uri);

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(GeocodingResponse[].class);
    }
}
