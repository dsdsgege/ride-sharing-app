package hu.ridesharing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite key for the Route entity, so we can identify a routeId by the two cities it connects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteId implements Serializable {
    private String cityA;

    private String cityB;

    /**
     * We don't care if the city is the destination or the start point. We only care about the distance or duration
     * between the two cities.
     *
     * @param cityA
     * @param cityB
     * @return
     */
    public static RouteId normalizeId(String cityA, String cityB) {
        RouteId routeId = new RouteId();
        if (cityA.compareTo(cityB) <= 0) {
            routeId.setCityA(cityA);
            routeId.setCityB(cityB);
        } else {
            routeId.setCityA(cityB);
            routeId.setCityB(cityA);
        }
        return routeId;
    }
}
