package hu.ridesharing.service;

import hu.ridesharing.entity.Car;
import hu.ridesharing.entity.CarId;
import hu.ridesharing.entity.Route;
import hu.ridesharing.repository.CarRepository;
import hu.ridesharing.repository.RouteRepository;
import hu.ridesharing.service.external.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

@Service
public class PriceCalculatorService {
    private static final int AVG_KM_PER_YEAR = 10_800;
    private static final int GAS_PRICE_IN_HUF = 590;

    private final RouteService routeService;
    private final CarRepository carRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public PriceCalculatorService(RouteService routeService, CarRepository carRepository,
                                  RouteRepository routeRepository) {

        this.routeService = routeService;
        this.carRepository = carRepository;
        this.routeRepository = routeRepository;
    }

    public int getPrice(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo, int seats,
                        String make, String model, int makeYear, int consumption) {
        // the average km driven per year is 10800km in EU

        CarId id = new CarId();
        id.setMake(make);
        id.setModel(model);
        id.setYear(makeYear);
        Car car = carRepository.findById(id).orElseThrow();

        double valueNow = car.getPrice() * (1 - getDeprecation(car.getYear()));
        double valueNextYear = car.getPrice() * (1 - getDeprecation(car.getYear() - 1));

        double loss = valueNow - valueNextYear;

        double costPerKm = loss / AVG_KM_PER_YEAR;

        double distanceInKm = getORSResponse(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo).getDistances()[0][1]
                / 1000;

        return (int) Math.round((costPerKm * distanceInKm + (distanceInKm / 100) * consumption * GAS_PRICE_IN_HUF) / seats);
    }
    private RouteService.ORSRespone getORSResponse(double longitudeFrom, double latitudeFrom, double longitudeTo,
                                                   double latitudeTo) {

        // if we have this coordinate saved in the db, just return the saved data
        Optional<Route> route = routeRepository.findByCoordinate(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);
        if (route.isPresent()) {
            RouteService.ORSRespone respone = new RouteService.ORSRespone();
            respone.setDistances(new double[][]{{0, route.get().getDistance()}});
            respone.setDurations(new double[][]{{0, route.get().getDuration()}});
            return respone;
        }

        // otherwise we make the api call and save the data
        Route routeToSave = new Route();
        routeToSave.setLatitudeFrom(latitudeFrom);
        routeToSave.setLongitudeFrom(longitudeFrom);
        routeToSave.setLatitudeTo(latitudeTo);
        routeToSave.setLongitudeTo(longitudeTo);

        RouteService.ORSRespone response = routeService.getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);
        routeToSave.setDistance(response.getDistances()[0][1]);
        routeToSave.setDuration(response.getDurations()[0][1]);
        routeRepository.save(routeToSave);
        return response;
    }

    /**
     * Initial Value	0%	$48,000
     * After 1 Month	10%	$43,200
     * After 1 Year	20%	$38,400
     * After 2 Years	32%	$32,640
     * After 3 Years	42%	$27,744
     * After 4 Years	51%	$23,582
     * After 5 Years	60%	$19,200
     * @param year
     * @return
     */
    private double getDeprecation(int year) {
        int thisYear = Year.now().getValue();
        return switch (thisYear-year) {
            case 0 -> 0.1;
            case 1 -> 0.2;
            case 2 -> 0.32;
            case 3 -> 0.43;
            case 4 -> 0.51;
            default -> 0.6;
        };
    }
}
