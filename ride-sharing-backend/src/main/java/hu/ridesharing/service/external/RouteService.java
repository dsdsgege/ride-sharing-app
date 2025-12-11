package hu.ridesharing.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RouteService {
    private final RestClient restClient;

    private final String api_key;

    @Autowired
    public RouteService(@Value("${openrouteservice.api.key}") String apiKey) {
        this.restClient = RestClient.builder().build();
        this.api_key = apiKey;
    }

    public ORSRespone getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
        ORSRequest request = new ORSRequest();
        request.locations = new double[][]{{longitudeFrom, latitudeFrom}, {longitudeTo, latitudeTo}};
        request.metrics = List.of("duration", "distance");

        ORSRespone result = restClient.post().
                uri("https://api.openrouteservice.org/v2/matrix/driving-car")
                .header(HttpHeaders.AUTHORIZATION, api_key)
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
