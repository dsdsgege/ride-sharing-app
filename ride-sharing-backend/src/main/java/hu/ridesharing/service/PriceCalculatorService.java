package hu.ridesharing.service;

import hu.ridesharing.entity.Route;
import hu.ridesharing.entity.RouteId;
import hu.ridesharing.repository.CarRepository;
import hu.ridesharing.repository.RouteRepository;
import hu.ridesharing.service.external.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

@Service
@Slf4j
public class PriceCalculatorService {
    private static final int AVG_KM_PER_YEAR = 10_800;
    private static final int GAS_PRICE_IN_HUF = 590;

    private final RouteService routeService;
    private final RouteRepository routeRepository;

    @Autowired
    public PriceCalculatorService(RouteService routeService, RouteRepository routeRepository) {

        this.routeService = routeService;
        this.routeRepository = routeRepository;
    }

    public int getPrice(String cityA, String cityB, double latitudeFrom, double longitudeFrom, double latitudeTo,
                        double longitudeTo, int seats, int consumption, int price, int make_year) {

        // the average km driven per year is 10800km in EU
        int carAge = Year.now().getValue() - make_year;

        double valueNow = price * (1 - getDeprecation(carAge));
        double valueNextYear = price * (1 - getDeprecation(carAge + 1));

        double loss = valueNow - valueNextYear;

        double costPerKm = loss / AVG_KM_PER_YEAR;

        RouteId routeId = RouteId.normalizeId(cityA, cityB);

        // Trying to cache the
        double distanceInKm;
        Optional<Route> route = routeRepository.findById(routeId);
        if (route.isPresent()) {
            distanceInKm = route.get().getDistance();
            log.debug("Found in cache: {}", route.get());
        } else {
            RouteService.ORSRespone response = routeService
                    .getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);

            distanceInKm = response.getDistances()[0][1] / 1000;
            double duration = response.getDurations()[0][1];

            RouteId newRouteId = RouteId.normalizeId(cityA, cityB);
            Route newRoute = new Route();
            newRoute.setCityA(newRouteId.getCityA());
            newRoute.setCityB(newRouteId.getCityB());
            newRoute.setDistance(distanceInKm);
            newRoute.setDuration(duration);
            routeRepository.save(newRoute);
        }

        return (int) Math.round(
                (costPerKm * distanceInKm + (distanceInKm / 100) * consumption * GAS_PRICE_IN_HUF) / seats
        );
    }

    /**
     * Initial Value	0%	$48,000
     * After 1 Month	10%	$43,200
     * After 1 Year	    20%	$38,400
     * After 2 Years	32%	$32,640
     * After 3 Years	42%	$27,744
     * After 4 Years	51%	$23,582
     * After 5 Years	60%	$19,200
     * @param age age of the car
     * @return
     */
    private double getDeprecation(int age) {
        return switch (age) {
            case 0 -> 0.1;
            case 1 -> 0.2;
            case 2 -> 0.32;
            case 3 -> 0.43;
            case 4 -> 0.51;
            default -> 0.6;
        };
    }
}
