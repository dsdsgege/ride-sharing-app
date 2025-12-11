package hu.ridesharing.service;

import hu.ridesharing.service.external.RouteService;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculatorService {
    private static final int GAS_PRICE_IN_HUF = 590;

    private final RouteService routeService;

    public PriceCalculatorService(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Calculates the price per passenger.
     *
     * @param longitudeFrom
     * @param latitudeFrom
     * @param longitudeTo
     * @param latitudeTo
     * @param seats
     * @param makeYear
     * @param consumption
     * @return
     */
    public int getPrice(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo, int seats,
                        int makeYear, int consumption) {

        double distance = getORSResponse(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo).getDistances()[0][1];

        return (int) distance / 100 * consumption * GAS_PRICE_IN_HUF;
    }
    // TODO: Save the every response to database, and only ask it again from the api if it is not yet stored in database
    private RouteService.ORSRespone getORSResponse(double longitudeFrom, double latitudeFrom, double longitudeTo,
                                                   double latitudeTo) {
        return routeService.getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);
    }
}
