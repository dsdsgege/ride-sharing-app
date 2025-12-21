package hu.ridesharing.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class RouteService {

    private final RestClient restClient;

    private final String API_KEY;

    public RouteService(@Value("${open.route.api.key}") String apiKey) {
        this.restClient = RestClient.builder().build();
        this.API_KEY = apiKey;
    }

    public ORSRespone getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
        ORSRequest request = new ORSRequest();
        request.locations = new double[][]{{ longitudeFrom, latitudeFrom }, { longitudeTo, latitudeTo }};
        request.metrics = List.of("duration", "distance");
        log.debug("Getting distance: latFrom: {}, lonFrom: {}, latTo: {}, lonTo: {}", latitudeFrom, longitudeFrom,
                latitudeTo, longitudeTo );

        ORSRespone result = restClient.post().
                uri("https://api.openrouteservice.org/v2/matrix/driving-car")
                .header(HttpHeaders.AUTHORIZATION, API_KEY)
                .body(request)
                .retrieve()
                .toEntity(ORSRespone.class)
                .getBody();

        return result;
    }

    /**
     * OpenRouteService Respone object.
     */
    @Getter
    @Setter
    public static class ORSRespone {
        @JsonProperty("distances")
        double[][] distances;

        @JsonProperty("durations")
        double[][] durations;
    }

    /**
     * OpenRouteService Request object.
     */
    @Setter
    @Getter
    static class ORSRequest {
        double[][] locations;
        List<String> metrics;
    }
}
