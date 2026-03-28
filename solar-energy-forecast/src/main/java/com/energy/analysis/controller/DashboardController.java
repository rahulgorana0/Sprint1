package com.energy.analysis.controller; // Controller package grouping

import com.energy.analysis.model.GenerationData; // Generation entity
import com.energy.analysis.repository.GenerationDataRepository; // DB access layer
import com.energy.analysis.service.DashboardService; // UI logic service
import com.energy.analysis.service.PredictionService; // ML logic service
import org.springframework.beans.factory.annotation.Autowired; // Dependency injection tag
import org.springframework.stereotype.Controller; // MVC web controller tag
import org.springframework.ui.Model; // UI context binding
import org.springframework.web.bind.annotation.GetMapping; // HTTP GET mapper
import org.springframework.web.bind.annotation.ResponseBody; // JSON response mapper

import java.util.HashMap; // Dictionary mapping
import java.util.List; // Array list handling
import java.util.Map; // Generic Map logic

/**
 * DashboardController handles the main UI views for the solar energy forecasting system.
 * Employs Spring MVC architecture dynamically rendering Thymeleaf HTML templates.
 */
@Controller // Registers bean as explicit web endpoint target
public class DashboardController {

    // Repositories mapped as final rejecting mutability
    private final GenerationDataRepository generationRepo; // Access physical power history
    
    // Services insulating controller from underlying messy DB/ML logic 
    private final DashboardService dashboardService; // UI telemetry aggregator
    private final PredictionService predictionService; // Weka math coordinator

    /**
     * Dependency injected instantiation. 
     */
    @Autowired // Auto-wire parameters securely
    public DashboardController(GenerationDataRepository generationRepo,
                               DashboardService dashboardService,
                               PredictionService predictionService) {
        this.generationRepo = generationRepo; // Link DB repo
        this.dashboardService = dashboardService; // Link telemetry tools
        this.predictionService = predictionService; // Link ML tools
    }

    /**
     * Captures visitors hitting the absolute root of the website seamlessly redirecting them 
     * exactly where they need to go.
     */
    @GetMapping("/") // Intercept root HTTP requests
    public String index() {
        return "redirect:/dashboard"; // Issue HTTP 302 pointing to /dashboard directly
    }

    /**
     * Dispatches the main HTML dashboard user interface. 
     * Injects highly dynamic payload bindings straight into the Thymeleaf template engine.
     */
    @GetMapping("/dashboard") // Main UI HTTP route
    public String dashboard(Model model) {
        // Prepare Weather Status Bar logic pushing object completely directly into view
        model.addAttribute("currentWeather", dashboardService.getLatestWeather()); 
        
        // Formats the live wattage output trimming wild float decimals strictly down to 2 precision points
        model.addAttribute("currentPower", String.format("%.2f", dashboardService.getLatestPower())); 
        
        // Pull max ceiling power rating applying 2 precision float bounds 
        model.addAttribute("peakPower", String.format("%.2f", dashboardService.getPeakPowerToday())); 
        
        // Execute heavy rolling total query generating UI metric 
        model.addAttribute("totalYieldKpi", String.format("%.2f", dashboardService.getTotalDailyYield())); 
        
        // Ask if system actually contains data records currently 
        model.addAttribute("systemStatus", dashboardService.getSystemStatus()); 
        
        // Inject massive List map arrays into UI for Chart.js graphing handling! 
        model.addAttribute("forecast48h", predictionService.getCombinedForecast(48)); // Passes Array containing 48 hours out
        model.addAttribute("dailyTotals15d", predictionService.getDailyTotals15d()); // Passes Array projecting yields spanning 15 calendar days

        return "dashboard"; // Direct Spring MVC to resolve and return `dashboard.html` 
    }

    /**
     * Maintains legacy compatibility querying explicitly all historical logged telemetry payloads natively
     */
    @GetMapping("/api/historical") // Legacy API route 
    @ResponseBody // Tells Spring this endpoint explicitly returns raw JSON, not an HTML view name!
    public Map<String, Object> getHistoricalData() {
        Map<String, Object> response = new HashMap<>(); // Create return boundary definition 
        
        // Execute brutal massive extraction fetching literal ALL generations logged (Caution: OOM risk scaling long timeframe) 
        List<GenerationData> data = generationRepo.findAll(); 
        
        // Key payload wrapping format cleanly 
        response.put("generation", data); 
        return response; // Export fully constructed block 
    }

    /**
     * API bridging UI frontend natively requesting JSON arrays representing purely future ML predictions cleanly  
     */
    @GetMapping("/api/dashboard/predictions") // API access path
    @ResponseBody // Returns explicit JSON list arrays  
    public List<?> getPredictions() {
        // Kick prediction request logic hitting WEKA algorithms directly producing array responses  
        return predictionService.predictNext15Days(); 
    }
}
