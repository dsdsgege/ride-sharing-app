package hu.ridesharing.service.external;

import hu.ridesharing.entity.car.Car;
import hu.ridesharing.entity.car.CarGeneration;
import hu.ridesharing.entity.car.CarModel;
import hu.ridesharing.entity.car.CarTrim;
import hu.ridesharing.repository.car.CarGenerationRepository;
import hu.ridesharing.repository.car.CarModelRepository;
import hu.ridesharing.repository.car.CarRepository;
import hu.ridesharing.repository.car.CarTrimRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RapidApiCarService {

    private final String baseUrl;

    private final String apiKey;

    private final String apiHost = "car-specs.p.rapidapi.com";

    private static final String API_KEY_HEADER = "x-rapidapi-key";

    private static final String API_HOST_HEADER = "x-rapidapi-host";

    private final CarRepository carRepository;

    private final CarModelRepository carModelRepository;

    private final CarGenerationRepository carGenerationRepository;

    private final CarTrimRepository carTrimRepository;

    private final RestClient restClient = RestClient.builder().build();

    @Autowired
    public RapidApiCarService(@Value("${rapid.api.url}") String baseUrl, @Value("${rapid.api.key}") String apiKey,
                              CarRepository carRepository, CarModelRepository carModelRepository,
                              CarGenerationRepository carGenerationRepository, CarTrimRepository carTrimRepository) {

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.carGenerationRepository = carGenerationRepository;
        this.carTrimRepository = carTrimRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchAndSaveCarMakes() {
        if (carRepository.count() > 0) {
            return;
        }

        log.info("Car makes is empty... Fetching car makes from RapidAPI");
        List<Car> cars = restClient.get()
                .uri(baseUrl + "/makes")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(API_KEY_HEADER, apiKey)
                .header(API_HOST_HEADER, apiHost)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Car>>() {});

        if (cars != null && !cars.isEmpty()) {
            carRepository.saveAll(cars);
        }
    }

    public void fetchAndSaveModels(Car parentCar) {
        log.info("Fetching models for make ID: {}", parentCar.getId());
        List<CarModel> models = restClient.get()
                .uri(baseUrl + "/makes/" + parentCar.getId() + "/models")
                .header(API_KEY_HEADER, apiKey)
                .header(API_HOST_HEADER, apiHost)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CarModel>>() {});

        if (models != null && !models.isEmpty()) {
            // Link back to parent before saving
            models.forEach(model -> model.setCar(parentCar));
            carModelRepository.saveAll(models);
        }
    }

    public void fetchAndSaveGenerations(CarModel parentModel) {
        log.info("Fetching generations for model ID: {}", parentModel.getId());
        List<CarGeneration> generations = restClient.get()
                .uri(baseUrl + "/models/" + parentModel.getId() + "/generations")
                .header(API_KEY_HEADER, apiKey)
                .header(API_HOST_HEADER, apiHost)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CarGeneration>>() {});

        if (generations != null && !generations.isEmpty()) {
            generations.forEach(gen -> gen.setModel(parentModel));
            carGenerationRepository.saveAll(generations);
        }
    }

    public void fetchAndSaveTrims(CarGeneration parentGeneration) {
        log.info("Fetching trims for generation ID: {}", parentGeneration.getId());
        List<CarTrim> trims = restClient.get()
                .uri(baseUrl + "/generations/" + parentGeneration.getId() + "/trims")
                .header(API_KEY_HEADER, apiKey)
                .header(API_HOST_HEADER, apiHost)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CarTrim>>() {});

        if (trims != null && !trims.isEmpty()) {
            trims.forEach(trim -> trim.setGeneration(parentGeneration));
            carTrimRepository.saveAll(trims);
        }
    }

    /**
     * When calculating the consumption refer to:
     * <a href=https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX%3A52024DC0122>this link.</a>
     *
     * @param trimId
     * @return
     */
    // We ONLY need to call this when we are calculating the price for a specific journey
    public CarTrim fetchAndSaveTrimSpecs(Long trimId) {
        // fetch the REAL object from DB so we don't lose the Generation link
        CarTrim trim = carTrimRepository.findById(trimId)
                .orElseThrow(() -> new IllegalArgumentException("Trim not found for ID: " + trimId));

        if (trim.getNumberOfSeats() != null && trim.getMixedFuelConsumptionPer100KmL() != null
                && trim.getEngineType() != null) {

            return trim; // Already cached
        }

        log.info("Fetching specs for trim ID: {}", trim.getId());
        Map<String, String> specs = restClient.get()
                .uri(baseUrl + "/trims/" + trim.getId())
                .header(API_KEY_HEADER, apiKey)
                .header(API_HOST_HEADER, apiHost)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});

        if (specs != null) {
            if (specs.containsKey("numberOfSeats")) {
                trim.setNumberOfSeats(Integer.parseInt(specs.get("numberOfSeats")));
            } else {
                log.error("No number of seats found for trim ID: {}", trim.getId());
            }
            if (specs.containsKey("engineType")) {
                trim.setEngineType(specs.get("engineType"));
            } else {
                log.error("No engine type found for trim ID: {}", trim.getId());
            }
            if (specs.containsKey("mixedFuelConsumptionPer100KmL")) {
                trim.setMixedFuelConsumptionPer100KmL(Double.parseDouble(
                        specs.get("mixedFuelConsumptionPer100KmL").split("l")[0].trim().replace(",", ".")
                ));
            } else {
                log.error("No mixed fuel consumption found for trim ID: {}...\n Falling back to average",
                        trim.getId()
                );

                // (7.89 + 6.88 + 7.44 + 5.97 + 5.83 + 5.94) / 6
                double defaultConsumption = 6.6583;
                if (trim.getEngineType() != null) {
                    defaultConsumption = switch (trim.getEngineType()) {
                        case "Petrol" -> 7.89;
                        case "Diesel" -> 6.88;
                        default -> defaultConsumption;
                    };
                }
                trim.setMixedFuelConsumptionPer100KmL(defaultConsumption);
            }

            return carTrimRepository.save(trim);
        }
        return trim;
    }
}