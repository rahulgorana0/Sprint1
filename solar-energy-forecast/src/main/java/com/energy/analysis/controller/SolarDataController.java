package com.energy.analysis.controller; // Maps namespace

import com.energy.analysis.model.*; // Local Data structures
import com.energy.analysis.repository.*; // Data retrieval 
import com.energy.analysis.service.WeatherApiService; // Weather operations
import org.springframework.beans.factory.annotation.Autowired; // Automapper 
import org.springframework.web.bind.annotation.GetMapping; // HTTP GET mapper
import org.springframework.web.bind.annotation.PostMapping; // HTTP POST mapper
import org.springframework.web.bind.annotation.RequestParam; // Query Parser string tag 
import org.springframework.web.bind.annotation.RestController; // Indicates REST component behavior 

import java.time.LocalDateTime; // Range constraints limits
import java.util.HashMap; // Object layout structuring 
import java.util.List; // Output sequential structures 
import java.util.Map; // Return structures 

/**
 * Controller providing REST API endpoints charting generation telemetry 
 * over various aggregate time intervals smoothly securely.
 */
@RestController // Generates JSON implicitly naturally 
public class SolarDataController {

    // Repositories mapped completely directly bypassing service intermediaries 
    private final GenerationDataRepository generationRepo; // Telemetry
    private final WeatherDataRepository weatherRepo; // Physical conditions 
    private final SolarPlantRepository plantRepo; // Plant index 
    private final WeatherApiService weatherApiService; // Trigger APIs externally

    /**
     * Dependency injected configuration layouts smoothly  
     */
    @Autowired // Auto-wires all elements correctly cleanly exactly smoothly 
    public SolarDataController(GenerationDataRepository generationRepo,
                               WeatherDataRepository weatherRepo,
                               SolarPlantRepository plantRepo,
                               WeatherApiService weatherApiService) {
        this.generationRepo = generationRepo; // Link references
        this.weatherRepo = weatherRepo; // Link references
        this.plantRepo = plantRepo; // Link references
        this.weatherApiService = weatherApiService; // Link reference instances 
    }

    /**
     * Executable POST trigger forcing immediate external fetch cycle cleanly explicitly natively
     */
    @PostMapping("/api/weather/refresh") // Map explicit POST target completely exactly securely 
    public Map<String, String> refreshWeather() {
        // Halt application thread executing external REST API HTTP request synchronously exactly
        weatherApiService.fetchWeatherForecast(); 
        
        // Define simple dictionary message boundaries securely natively smoothly 
        Map<String, String> response = new HashMap<>(); // Instantiate new hash 
        response.put("status", "success"); // Add output tracking indicator metrics  
        response.put("message", "Weather forecast updated successfully"); // User readable notification output cleanly 
        return response; // Export dict 
    }

    /**
     * Endpoint targeting Chart queries requesting windowed sequences representing historical operations explicitly correctly 
     */
    @GetMapping("/api/solar-data") // Endpoint definition path exactly smoothly cleanly  
    public Map<String, Object> getSolarData(@RequestParam(defaultValue = "day") String type) { // Param matches URL query values parsing intelligently dynamically  
        Map<String, Object> response = new HashMap<>(); // Core root response JSON target maps 
        
        // Tie requests explicitly natively cleanly resolving explicit site layouts structurally 
        SolarPlant plant = plantRepo.findByPlantId("4135001").orElse(null); 
        if (plant == null) return response; // Graceful exit guarding errors 

        // Current time clock anchor metric definitions  
        LocalDateTime end = LocalDateTime.now(); 
        // Instantiates uninitialized variable holding backwards bounds offsets  
        LocalDateTime start; 

        // Evaluates String variable tracking requests dynamically securely cleanly smoothly  
        switch (type.toLowerCase()) { // Safely converts string case avoiding mismatch crashes 
            case "month": // Chart requested 30 trailing day view
                start = end.minusMonths(1); // Modifies clock bounds  
                break; // Limit operations
            case "year": // Chart requested roughly 365 days explicitly securely 
                start = end.minusYears(1); // Modifies timeline limits explicitly securely broadly
                break; // Break fallthrough natively gracefully 
            case "day": // Common default parameter view 
            default: // Handle garbage string configurations natively routing broadly fallback limits cleanly 
                start = end.minusDays(1); // Modify layout constraint limit boundaries cleanly smoothly exactly  
                break; // Halt evaluations efficiently  
        }

        // Executes targeted repository operations requesting only data points falling linearly inside mapped temporal boundaries natively securely smoothly  
        List<GenerationData> generation = generationRepo.findBySolarInverterSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(plant.getId(), start, end);
        
        // Duplicate chronological filtering procedure applied independently across separate environmental data arrays exactly smoothly completely  
        List<WeatherData> weather = weatherRepo.findByWeatherSensorSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(plant.getId(), start, end);

        // Bundle individual resulting queries tightly inside single JSON Master Dictionary successfully 
        response.put("type", type); // Emits reference tag natively cleanly dynamically resolving context bounds completely 
        response.put("generation", generation); // Attach list explicitly precisely functionally easily smoothly safely  
        response.put("weather", weather); // Assemble array cleanly gracefully natively effectively confidently cleanly  

        return response; // End sequence emitting completely wrapped string payload objects seamlessly  
    }
}
