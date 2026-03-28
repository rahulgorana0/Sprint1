package com.energy.analysis.service; // Declares the package for all service layer classes

import com.energy.analysis.model.*; // Imports all model entities (ApiWeatherData, SolarPlant, etc.)
import com.energy.analysis.repository.*; // Imports all repositories to access the database
import org.springframework.beans.factory.annotation.Autowired; // Imports Autowired for Spring Dependency Injection
import org.springframework.stereotype.Service; // Imports Service to mark this class as a Spring Bean

import java.time.LocalDateTime; // Imports LocalDateTime for managing dates and times
import java.util.List; // Imports List for handling collections of data

/**
 * Service providing real-time metrics and weather context for the solar energy forecast dashboard.
 * Acts as the business logic layer between the controllers and the database for UI statistics.
 */
@Service // Marks DashboardService as a valid Spring Service component
public class DashboardService {

    // Repositories injected to allow querying the underlying SQL database mappings
    private final SolarPlantRepository plantRepo; // Accesses SolarPlant records
    private final GenerationDataRepository generationRepo; // Accesses historic generation logs
    private final DailyMetricsRepository dailyMetricsRepo; // Accesses 24-hr rolled up summaries
    private final ApiWeatherDataRepository apiWeatherDataRepo; // Accesses 3rd party API weather pulls

    /**
     * Constructor-based dependency injection.
     * Preferred over field injection as it enforces required dependencies during instantiation and aids testing.
     * 
     * @param plantRepo The plant repository to inject
     * @param generationRepo The generation repository to inject
     * @param dailyMetricsRepo The daily metrics repository to inject
     * @param apiWeatherDataRepo The api weather repository to inject
     */
    @Autowired // instructs Spring to automatically inject the required repository beans into this constructor
    public DashboardService(SolarPlantRepository plantRepo,
                            GenerationDataRepository generationRepo,
                            DailyMetricsRepository dailyMetricsRepo,
                            ApiWeatherDataRepository apiWeatherDataRepo) {
        this.plantRepo = plantRepo; // Binds the injected plantRepo to the final local field
        this.generationRepo = generationRepo; // Binds the injected generationRepo to the final local field
        this.dailyMetricsRepo = dailyMetricsRepo; // Binds the injected dailyMetricsRepo to the local field
        this.apiWeatherDataRepo = apiWeatherDataRepo; // Binds the injected apiWeatherRepo to the local field
    }

    /**
     * Finds the absolute peak AC power generated at any point in the current local day.
     * 
     * @return Peak AC power as a double. Returns 0.0 if not found.
     */
    public double getPeakPowerToday() {
        // Attempts to find the hardcoded plant ID '4135001', or defaults to null if absent 
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null); 
        
        // If the plant is entirely missing from the DB, return an immediate 0.0 baseline
        if (plant == null) return 0.0; 

        // Constructs a boundary timestamp for the exact start of today (midnight)
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        // Constructs a boundary timestamp for exactly right now
        LocalDateTime now = LocalDateTime.now();
        
        // Queries the generation repository for all entries for this plant between midnight and now
        List<GenerationData> todayData = generationRepo.findBySolarInverterSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(plant.getId(), startOfDay, now);
        
        // Opens a java stream over the fetched array of records
        return todayData.stream()
                // Maps the objects down to just a primitive double stream of their AC Power values, defaulting nulls to 0.0 
                .mapToDouble(d -> d.getAcPowerKw() != null ? d.getAcPowerKw() : 0.0)
                // Finds the maximum value in that resulting stream 
                .max()
                // If the stream was completely empty, provides a fallback of 0.0
                .orElse(0.0);
    }

    /**
     * Obtains the single absolute newest AC power reading reported by the system.
     * 
     * @return Latest AC power reading or 0.0 if none exists.
     */
    public double getLatestPower() {
        // Look up the master plant record via the hardcoded string plant ID 
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null);
        
        // Return 0.0 immediately if the database is unpopulated
        if (plant == null) return 0.0;

        // Uses a highly optimized SQL TOP 1 ordering query to fetch literally just the most recent single chronological row
        return generationRepo.findTopBySolarInverterSolarPlantIdOrderByDateTimeDesc(plant.getId())
                // Transforms that optional row into an optional Double representing the AC power
                .map(d -> d.getAcPowerKw() != null ? d.getAcPowerKw() : 0.0)
                // Defaults to 0.0 if there was no row found at all 
                .orElse(0.0);
    }

    /**
     * Evaluates the health status of the platform by checking if any plants are registered.
     * 
     * @return String "Healthy" or "Offline"
     */
    public String getSystemStatus() {
        // Performs a rapid database COUNT() on the plant table. Greater than 0 means Healthy.
        return plantRepo.count() > 0 ? "Healthy" : "Offline";
    }

    /**
     * Retrieves the single most currently relevant weather record.
     * Primarily prefers actual upcoming forecast data over historic stale logs.
     * 
     * @return ApiWeatherData entity object fully populated, or a total blank object if unresolvable.
     */
    public ApiWeatherData getLatestWeather() {
        // Fetch the reference to our primary plant 
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null);
        
        // If the plant is missing, we must return a safe (but empty) weather object to prevent UI null pointers
        if (plant == null) return new ApiWeatherData();

        // Capture the chronological 'now'
        LocalDateTime now = LocalDateTime.now();
        
        // Strategy: First attempts to find the VERY NEXT upcoming forecast logically positioned after 'now'
        return apiWeatherDataRepo.findFirstBySolarPlantIdAndDateTimeAfterOrderByDateTimeAsc(plant.getId(), now)
                // If there are no future forecasts, fallback to the absolute most recent historic reading available
                .or(() -> apiWeatherDataRepo.findTopBySolarPlantIdOrderByDateTimeDesc(plant.getId()))
                // If neither exist (completely blank table), safely yield an empty object 
                .orElse(new ApiWeatherData());
    }

    /**
     * Calculates the absolute peak daily total yield measured at any point in the last 24 sliding hours.
     * 
     * @return Daily yield in kWh
     */
    public double getTotalDailyYield() {
        // Attempt to fetch the plant
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null);
        
        // Catch uninitialized setups
        if (plant == null) return 0.0;

        // Establish chronological boundary: Now
        LocalDateTime now = LocalDateTime.now();
        
        // Establish chronologic boundary: Exactly a sliding 24-hours prior to Now
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        
        // Fetch all daily metric rolling counters that updated within sliding past 24-hr period 
        List<DailyMetrics> recentMetrics = dailyMetricsRepo.findBySolarInverterSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(plant.getId(), twentyFourHoursAgo, now);
        
        // Stream the objects to find the absolute maximum yielding metric recorded within this recent window
        return recentMetrics.stream()
                // Safely convert each metric object into its yield double, avoiding nulls
                .mapToDouble(d -> d.getDailyYieldKwh() != null ? d.getDailyYieldKwh() : 0.0)
                // Ask stream for maximum 
                .max()
                // Or fallback to 0.0 
                .orElse(0.0);
    }
}
