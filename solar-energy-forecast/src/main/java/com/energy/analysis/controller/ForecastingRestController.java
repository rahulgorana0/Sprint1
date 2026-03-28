package com.energy.analysis.controller; // Package mapping

import com.energy.analysis.model.*; // Local object maps
import com.energy.analysis.repository.*; // DB lookup logic
import com.energy.analysis.service.PredictionService; // ML Math engine 
import org.springframework.beans.factory.annotation.Autowired; // Wire mapping
import org.springframework.web.bind.annotation.GetMapping; // Generic HTTP GET route
import org.springframework.web.bind.annotation.RequestMapping; // HTTP base route
import org.springframework.web.bind.annotation.RestController; // Indicates pure JSON class

import java.time.LocalDateTime; // Datetime abstractions
import java.util.HashMap; // Dict Maps 
import java.util.List; // Array Collections 
import java.util.Map; // Generic maps
import java.util.stream.Collectors; // Stream utility functions

/**
 * REST Controller for providing predictive data and live weather purely to JSON consumers.
 */
@RestController // Defines behavior entirely as serializing API json logic bypassing HTML
@RequestMapping("/api") // Universal parent path prefix 
public class ForecastingRestController {

    // Final bindings mapping dependencies directly
    private final PredictionService predictionService; // Math generation
    private final ApiWeatherDataRepository weatherRepo; // API Weather cache 
    private final SolarPlantRepository plantRepo; // Plant lookup 

    /**
     * Constructor.
     */
    @Autowired // Signal DI
    public ForecastingRestController(PredictionService predictionService, 
                                 ApiWeatherDataRepository weatherRepo,
                                 SolarPlantRepository plantRepo) {
        this.predictionService = predictionService; // Init service
        this.weatherRepo = weatherRepo; // Init service
        this.plantRepo = plantRepo; // Init service 
    }

    /**
     * Outputs arrays mapping Time Strings strictly explicitly correctly representing power metrics explicitly cleanly
     */
    @GetMapping("/predictions") // End route mapping
    public List<Map<String, Object>> getPredictions() {
        // Fetch raw predictive entity mappings  
        List<ForecastData> predictions = predictionService.getLatestPredictions();
        
        // Execute Stream reduction flattening complex JPA objects massively down to lightweight dictionary blocks 
        return predictions.stream().map(p -> {
            Map<String, Object> map = new HashMap<>(); // Create block 
            map.put("time", p.getDateTime().toString()); // Unspool string formatting
            map.put("power", p.getPredictedPowerKw()); // Push numerical value natively 
            return map; // Provide output 
        }).collect(Collectors.toList()); // Yield assembled target arrays 
    }

    /**
     * Resolves currently active weather parameters parsing heavily specific DB constraints 
     */
    @GetMapping("/live") // End route mapping 
    public Map<String, Object> getLive() {
        // Query explicit facility natively completely securely 
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null);
        
        // Provide static boundary values preventing client-side UI failure paths explicitly 
        if (plant == null) return Map.of("weather", Map.of("temperature", 0.0, "solarRadiation", 0.0));

        // Evaluate DB natively seeking singular closest row temporally occurring physically AFTER exactly right now
        ApiWeatherData latest = weatherRepo.findFirstBySolarPlantIdAndDateTimeAfterOrderByDateTimeAsc(plant.getId(), LocalDateTime.now())
                .orElse(new ApiWeatherData()); // Deliver entirely empty object shielding from NullPointerExceptions natively smoothly 

        // Constructs a dynamic Map dictionary entirely in-line deploying functional initialization 
        return Map.of(
            "weather", Map.of( // Nest child dictionary directly inside 
                // Fetch internal temperature property defaulting zeroes smoothly cleanly completely 
                "temperature", latest.getTempC() != null ? latest.getTempC() : 0.0, 
                // Obtain Illumination properties exactly smoothly cleanly securely  
                "solarRadiation", latest.getIrradiationWm2() != null ? latest.getIrradiationWm2() : 0.0, 
                // Output physical boundary placeholder resolving legacy parameters completely cleanly smoothly  
                "windSpeed", 0.0 
            ) // Close Map nested array 
        ); // Close Master output Map
    }
}