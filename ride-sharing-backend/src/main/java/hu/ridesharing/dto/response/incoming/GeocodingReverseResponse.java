package hu.ridesharing.dto.response.incoming;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GeocodingReverseResponse {
    private String name;
    private Map<String, String> local_names;
    private String country;
    private String state;
}
