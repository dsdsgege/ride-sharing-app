package hu.ridesharing.service;

import hu.ridesharing.entity.Route;
import hu.ridesharing.entity.RouteId;
import hu.ridesharing.exception.BadRequestException;
import hu.ridesharing.repository.RouteRepository;
import hu.ridesharing.service.external.RapidApiCarService;
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
    private static final int GAS_PRICE_IN_HUF = 615;

    private final RouteService routeService;
    private final RouteRepository routeRepository;
    private final RapidApiCarService rapidApiCarService;

    @Autowired
    public PriceCalculatorService(RouteService routeService, RouteRepository routeRepository,
                                  RapidApiCarService rapidApiCarService) {

        this.routeService = routeService;
        this.routeRepository = routeRepository;
        this.rapidApiCarService = rapidApiCarService;
    }

    public int getPrice(String cityA, String cityB, double latitudeFrom, double longitudeFrom, double latitudeTo,
                        double longitudeTo, long trimId, int price, int givenSeats) {

        var trim = rapidApiCarService.fetchAndSaveTrimSpecs(trimId);
        System.out.println(trim.getNumberOfSeats());


        // If the API has no information about the seats, we are falling back to the user's input
        if (trim.getNumberOfSeats() == null || trim.getNumberOfSeats() == 0) {
            trim.setNumberOfSeats(givenSeats + 1);
        } else if (trim.getNumberOfSeats() - 1 < givenSeats) {
            throw new BadRequestException("Your car can not carry that many passengers.");
        }

        int from = Integer.parseInt(trim.getGeneration().getYearFrom());
        // If still manufactured (yearTo == null), calculate with current year
        int to = (trim.getGeneration().getYearTo() != null) ?
            Integer.parseInt(trim.getGeneration().getYearTo()) : 
            Year.now().getValue();

        int currentYear = Year.now().getValue();
        int estimatedManufactureYear = from + ((to - from) / 2);
        int carAge = currentYear - estimatedManufactureYear;

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
                    .getResponse(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);

            distanceInKm = response.getDistances()[0][1] / 1000;
            double duration = response.getDurations()[0][1];

            RouteId newRouteId = RouteId.normalizeId(cityA, cityB);
            Route newRoute = new Route();
            newRoute.setCityA(newRouteId.getCityA());
            newRoute.setCityB(newRouteId.getCityB());
            newRoute.setLatitudeFrom(latitudeFrom);
            newRoute.setLongitudeFrom(longitudeFrom);
            newRoute.setLatitudeTo(latitudeTo);
            newRoute.setLongitudeTo(longitudeTo);
            newRoute.setDistance(distanceInKm);
            newRoute.setDuration(duration);
            routeRepository.save(newRoute);
        }

        return (int) Math.round(
                (costPerKm * distanceInKm
                        + (distanceInKm / 100)
                        * trim.getMixedFuelConsumptionPer100KmL()
                        * GAS_PRICE_IN_HUF
                ) / trim.getNumberOfSeats()
        );
    }

    /**
     * Refer to "On the depreciation of automobiles: An international comparison"
     */
    private double getDeprecation(int age) {
        return 1 - Math.exp(-0.31 * age);
    }
}
