package com.energy.analysis.model;

import jakarta.persistence.*; // Annotation schema mapping rules
import java.util.List; // Enable list references 

/**
 * Super-Entity representing an entire integrated Solar Power Plant.
 * Sits at the root of the hierarchy spanning Inverters, Sensors, Forecasts, and Weather logs.
 */
@Entity // Tells Hibernate this acts as an actual table
@Table(name = "solar_plant") // Specifies logical schema naming schema 
public class SolarPlant {

    @Id // Identifies sequence primary node row 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID is auto-derived by MySQL index increments
    private Long id; // Surrogate root identifier

    @Column(name = "plant_id", nullable = false, unique = true) // Explicit plant identifier string often seen in CSV telemetry headers
    private String plantId; // The functional string hash code distinguishing different physical locations

    @Column(name = "location") // Generic map/string coordinates or human-readable address
    private String location; // Physical geographic location string 

    @Column(name = "capacity_mw", nullable = false) // Defines expected overall max threshold for entire structure
    private Double capacityMw; // Megawatt rated maximal capability of combined connected PV arrays

    // The root owns the cascade life-cycles of its diverse child devices & datasets
    @OneToMany(mappedBy = "solarPlant", cascade = CascadeType.ALL) // Inverse map of the relation to inverters
    private List<SolarInverter> inverters; // A plant can contain multiple disjoint inverters reporting energy stats

    @OneToMany(mappedBy = "solarPlant", cascade = CascadeType.ALL) // Cascade deleting plant deletes linked sensors
    private List<WeatherSensor> sensors; // Hardware environmental sensors mapped directly to the facility zone

    @OneToMany(mappedBy = "solarPlant", cascade = CascadeType.ALL) // Forecast Models trained to predict *this* local plant specifically
    private List<ForecastModel> forecastModels; // Holds the references to WEKA models representing this entity 

    @OneToMany(mappedBy = "solarPlant", cascade = CascadeType.ALL) // Derived training feature rows scoped strictly to this site
    private List<TrainingFeature> trainingFeatures; // Time-series aggregated training variables required for Machine Learning

    @OneToMany(mappedBy = "solarPlant", cascade = CascadeType.ALL) // Stores 3rd-party fetched weather telemetry predicting incoming scenarios
    private List<ApiWeatherData> apiWeatherData; // Weather parameters strictly confined geographically to this area

    /**
     * Default constructor to prevent initialization proxy blocks
     */
    public SolarPlant() {
        // Requires no logic inside
    }

    // --- Accessor Interfaces ---
    
    public Long getId() {
        return id; // Access primary constraint value
    }

    public void setId(Long id) {
        this.id = id; // Provide DB primary key mechanism configuration 
    }

    public String getPlantId() {
        return plantId; // Get explicit logical hash value 
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId; // Force a strict external telemetry source ID reference
    }

    public String getLocation() {
        return location; // Read string-typed geo context
    }

    public void setLocation(String location) {
        this.location = location; // Give human label for system display 
    }

    public Double getCapacityMw() {
        return capacityMw; // Observe total installed power parameters
    }

    public void setCapacityMw(Double capacityMw) {
        this.capacityMw = capacityMw; // Register the total potential capacity of the equipment 
    }

    public List<SolarInverter> getInverters() {
        return inverters; // Query list of subordinate generation devices 
    }

    public void setInverters(List<SolarInverter> inverters) {
        this.inverters = inverters; // Tie child subordinate generation objects here
    }

    public List<WeatherSensor> getSensors() {
        return sensors; // View hardware environmental probes active on site
    }

    public void setSensors(List<WeatherSensor> sensors) {
        this.sensors = sensors; // Tie environmental IoT points 
    }

    public List<ForecastModel> getForecastModels() {
        return forecastModels; // Access the suite of locally tailored ML algorithms
    }

    public void setForecastModels(List<ForecastModel> forecastModels) {
        this.forecastModels = forecastModels; // Push updated ML algorithms array
    }

    public List<TrainingFeature> getTrainingFeatures() {
        return trainingFeatures; // View collated feature blocks  
    }

    public void setTrainingFeatures(List<TrainingFeature> trainingFeatures) {
        this.trainingFeatures = trainingFeatures; // Assign compiled feature arrays ready for WEKA 
    }

    public List<ApiWeatherData> getApiWeatherData() {
        return apiWeatherData; // See what third-party services claim is happening
    }

    public void setApiWeatherData(List<ApiWeatherData> apiWeatherData) {
        this.apiWeatherData = apiWeatherData; // Dump third-party claims into local entity map  
    }
}
