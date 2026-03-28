package com.energy.analysis.service; // Defines the logical grouping 

import com.energy.analysis.model.ApiWeatherData; // Object mapping
import com.energy.analysis.model.SolarPlant; // Plant node object
import com.energy.analysis.repository.ApiWeatherDataRepository; // Weather access
import com.energy.analysis.repository.SolarPlantRepository; // Plant access
import org.slf4j.Logger; // Logging interface 
import org.slf4j.LoggerFactory; // Logger instantiator 
import org.springframework.beans.factory.annotation.Autowired; // Spring DI  
import org.springframework.beans.factory.annotation.Value; // Config property ingestion
import org.springframework.core.ParameterizedTypeReference; // Complex generics
import org.springframework.http.HttpMethod; // REST Http methods
import org.springframework.http.ResponseEntity; // REST wrapper 
import org.springframework.stereotype.Service; // Spring Bean marking
import org.springframework.transaction.annotation.Transactional; // Safety bounds 
import org.springframework.web.client.RestTemplate; // Synchronous HTTP client
import org.springframework.web.util.UriComponentsBuilder; // Safe URL encoding 
import org.springframework.boot.context.event.ApplicationReadyEvent; // System startup hooks
import org.springframework.context.event.EventListener; // Marks event trigger methods 

import java.time.Instant; // Epoch mapping
import java.time.LocalDateTime; // Datetimes
import java.time.ZoneId; // Timzeones
import java.util.List; // Collections
import java.util.Map; // JSON dictionaries
import java.util.Optional; // Safe outputs

/**
 * Service to integrate with the Visual Crossing API for live weather forecasts.
 * Synchronizes external data down into local MySQL mappings automatically.
 */
@Service // Ingested as a singleton bean across Spring Framework
public class WeatherApiService {

    // Instantiates an SLF4J logging utility targeting just this class specifically
    private static final Logger log = LoggerFactory.getLogger(WeatherApiService.class);

    // Immutable service dependencies
    private final RestTemplate restTemplate; // Spring RestTemplate for firing HTTP Requests
    private final ApiWeatherDataRepository apiWeatherDataRepo; // Repository handling the saving of new weather points
    private final SolarPlantRepository plantRepo; // For locating the target plant inside the graph
    private final PredictionService predictionService; // Called after weather completes to update ML predictions
                                                       // instantly

    // Binds the property "weather.api.base-url" from application.properties
    @Value("${weather.api.base-url}")
    private String apiBaseUrl; // Target HTTP endpoint root

    // Binds the property "weather.api.key" safely from application.properties
    @Value("${weather.api.key}")
    private String apiKey; // Secret token used to authenticate

    /**
     * Initializes service, Autowiring all requisite managed sub-services
     * gracefully.
     */
    @Autowired // Auto-wires injection
    public WeatherApiService(RestTemplate restTemplate,
            ApiWeatherDataRepository apiWeatherDataRepo,
            SolarPlantRepository plantRepo,
            PredictionService predictionService) {
        this.restTemplate = restTemplate; // Binding client
        this.apiWeatherDataRepo = apiWeatherDataRepo; // Binding repo
        this.plantRepo = plantRepo; // Binding repo
        this.predictionService = predictionService; // Binding prediction trigger service
    }

    /**
     * Triggered automatically exactly once when the Spring Application completes
     * startup routing.
     * Starts the prediction pipeline proactively so the dashboard isn't empty upon
     * the first visitor's request.
     */
    @EventListener(ApplicationReadyEvent.class) // Listens explicitly for the Ready signal
    @Transactional // Executes synchronously inside a transaction
    public void onApplicationReady() {
        // Logs startup trigger
        log.info("--- APP STARTUP: Automatically triggering initial Weather API fetch and Prediction ---");
        // We comment out the literal weather fetch to save on bounded external API
        // rate-limits natively.
        fetchWeatherForecast();

        // But we DO trigger a brand new predictive sweep utilizing whatever weather
        // data remains in cache.
        predictionService.predictNext15Days();
    }

    /**
     * Core functional loop to execute an API call upstream, parse JSON, and store
     * rows.
     */
    public void fetchWeatherForecast() {
        // Logging context
        log.info("Manually fetching 15-day forecasts from Visual Crossing API");

        try {
            // Using a static location hook based on sample data
            String location = "Gandhinagar";

            // Looks up the plant. If the database is somehow totally wiped, this acts as a
            // self-healing fallback creating the plant node to stick data against
            SolarPlant plant = plantRepo.findByPlantId("4135001").orElseGet(() -> {
                SolarPlant newPlant = new SolarPlant(); // Initialize
                newPlant.setPlantId("4135001"); // Define literal schema requirement
                newPlant.setCapacityMw(100.0); // Sample capacity max
                newPlant.setLocation(location); // Attach string map point
                return plantRepo.save(newPlant); // Persist fallback
            });

            // Constructs a heavily configured safe URI string integrating all query
            // parameters elegantly avoiding string-concatenation errors
            String url = UriComponentsBuilder.fromUriString(apiBaseUrl + "/" + location)
                    .queryParam("unitGroup", "metric") // Requires celsius and standard metric units
                    .queryParam("key", apiKey) // Authenticates the transaction
                    .queryParam("contentType", "json") // Explicitly dictates JSON response expectation
                    .queryParam("include", "hours") // Ensures we receive granular hour-by-hour arrays, not just daily
                                                    // rollups
                    .queryParam("elements", "datetimeEpoch,temp,solarradiation") // Optimizes response payload sizing
                                                                                 // explicitly
                    .toUriString(); // Bakes it down into a finalized URL

            // Dbg check outputting exactly what URL string we formulated
            log.debug("Calling API URL: {}", url);

            // Execute the GET call natively mapped directly into a complex nested Java
            // Map<String, Object> using ParameterizedTypeReference
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, // Endpoint
                    HttpMethod.GET, // GET operation
                    null, // Blank body
                    new ParameterizedTypeReference<Map<String, Object>>() { // Generic parsing definition
                    });

            // Verify both the HTTP response and the internal property layout guarantees
            // exist
            if (response.getBody() != null && response.getBody().containsKey("days")) {

                // Safe-cast the child "days" property array root
                List<Map<String, Object>> days = (List<Map<String, Object>>) response.getBody().get("days");

                // Benchmark 'now' to correctly identify what's historic vs what's predicted
                LocalDateTime now = LocalDateTime.now();
                int count = 0; // Incrementing insert counter

                // Traverse the days nested array chunk
                for (Map<String, Object> day : days) {

                    // Verify the daily chunk actually holds hour structures
                    if (day.containsKey("hours")) {

                        // Descend into the hour-by-hour objects
                        List<Map<String, Object>> hours = (List<Map<String, Object>>) day.get("hours");
                        for (Map<String, Object> hour : hours) {

                            // Identify the long integer tracking the unix epoch stamp
                            long epoch = ((Number) hour.get("datetimeEpoch")).longValue();

                            // Transform raw epoch integer into fully-featured standard Java LocalDateTime
                            // object via system timezone map
                            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch),
                                    ZoneId.systemDefault());

                            // We query bounded +/- 1 minute simply to see if a DB record for this literal
                            // hour already exists
                            List<ApiWeatherData> existing = apiWeatherDataRepo
                                    .findBySolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(
                                            plant.getId(), dateTime.minusMinutes(1), dateTime.plusMinutes(1));

                            ApiWeatherData record;
                            // Check if a row already existed so we mutate it, keeping the primary key clean
                            // (an upsert procedure)
                            if (!existing.isEmpty()) {
                                record = existing.get(0); // Mutate existing
                            } else {
                                record = new ApiWeatherData(); // Generate totally new
                                record.setSolarPlant(plant); // Link relations
                                record.setDateTime(dateTime); // Register new time constraint
                            }

                            // Maps temperatures, handling null properties by injecting exactly 0.0 zeroes
                            // gracefully
                            record.setTempC(hour.get("temp") != null ? ((Number) hour.get("temp")).doubleValue() : 0.0);

                            // Maps radiation levels gracefully
                            record.setIrradiationWm2(hour.get("solarradiation") != null
                                    ? ((Number) hour.get("solarradiation")).doubleValue()
                                    : 0.0);

                            // Set boolean truth by simply comparing the row time against the current
                            // execution time bounds
                            record.setForecast(dateTime.isAfter(now));

                            // Queue the final assembled item for an explicit save operation
                            apiWeatherDataRepo.save(record);
                            count++; // Step up inserted record tracking output
                        }
                    }
                }

                // Print a friendly success report summarizing processing block
                log.info("Successfully fetched and stored {} weather data points", count);

                log.info("Auto-trigger ML predictions given new weather conditions...");
                // Having updated the source Weather array, forcefully demand an ML Prediction
                // update iteration ensuring UI views reflect the new future
                predictionService.predictNext15Days();
                log.info("ML Predictions updated for the next 15 days.");
            }
        } catch (Exception e) {
            // General catch bounds throwing an explicit error string accompanied by stack
            // trace log
            log.error("Error fetching weather forecast from API.", e);
        }
    }
}
