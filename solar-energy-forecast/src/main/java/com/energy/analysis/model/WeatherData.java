package com.energy.analysis.model;

import jakarta.persistence.*; // Expose the JPA functionality
import java.time.LocalDateTime; // Datetime standard objects  

/**
 * Entity representing weather sensor physical readings directly captured at a solar plant.
 * As opposed to ApiWeatherData which represents external 3rd party web service data.
 */
@Entity // Treat as JPA persistable structure 
@Table(name = "weather_data") // Defines layout targeting the weather_data node structure
public class WeatherData {

    @Id // Establish core identifier field flag
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Instruct standard increment layout
    @Column(name = "weather_data_id")
    private Long id; // Unique data row numerical tracker

    @ManyToOne // Multiplexed records can exist for one single hardware sensor  
    @JoinColumn(name = "sensor_id", nullable = false) // Ensures data validity forcing strict coupling to one sensor device 
    private WeatherSensor weatherSensor; // Device actually capturing this raw metrics object

    @Column(name = "date_time", nullable = false) // Synchronizes physical observations with the time axis   
    private LocalDateTime dateTime; // Specific time metrics were physically reported 

    @Column(name = "ambient_temp_c", nullable = false) // Tracks temperature in surroundings
    private Double ambientTempC; // Environmental heat measure inside air envelope

    @Column(name = "module_temp_c", nullable = false) // Physical heat accumulation directly inside the solar arrays 
    private Double moduleTempC; // Extremely important as elevated module temp negatively degrades array output efficiencies 

    @Column(name = "irradiation_wm2", nullable = false) // The solar irradiance raw brightness capture
    private Double irradiationWm2; // Measure of sunlight photon energy hitting the surface metrics  

    /**
     * Requisite blank constructor.
     */
    public WeatherData() {
        // JPA standard dummy hook
    }

    // --- Core Entity Methods --- 
    
    public Long getId() {
        return id; // Access primary reference index
    }

    public void setId(Long id) {
        this.id = id; // Update root pointer ID 
    }

    public WeatherSensor getWeatherSensor() {
        return weatherSensor; // Query what hardware actually reported these conditions
    }

    public void setWeatherSensor(WeatherSensor weatherSensor) {
        this.weatherSensor = weatherSensor; // Re-assign ownership reference pointer 
    }

    public LocalDateTime getDateTime() {
        return dateTime; // Read physical capture timeline clock
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime; // Establish the strict timestamp requirement
    }

    public Double getAmbientTempC() {
        return ambientTempC; // Fetch external air measurement 
    }

    public void setAmbientTempC(Double ambientTempC) {
        this.ambientTempC = ambientTempC; // Mutate external air condition value 
    }

    public Double getModuleTempC() {
        return moduleTempC; // Discover the internal solar cell accumulated heat  
    }

    public void setModuleTempC(Double moduleTempC) {
        this.moduleTempC = moduleTempC; // Write heat metrics 
    }

    public Double getIrradiationWm2() {
        return irradiationWm2; // Fetch photon/sunlight measurement 
    }

    public void setIrradiationWm2(Double irradiationWm2) {
        this.irradiationWm2 = irradiationWm2; // Insert photon energy reading variables
    }
}
