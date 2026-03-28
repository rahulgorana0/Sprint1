package com.energy.analysis.model;

import jakarta.persistence.*; // Necessary Java Persistence utilities
import java.util.List; // For grouping weather observations inside arrays 

/**
 * Entity representing a physical Weather Sensor positioned at a Solar Plant location.
 * Provides the hardware binding context for 'WeatherData' entries.
 */
@Entity // Specifies to the engine this represents a database table model
@Table(name = "weather_sensor") // Ties the pojo mapping directly to 'weather_sensor' table in SQL
public class WeatherSensor {

    @Id // Flag for primary index constraint identity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ask the Database auto-increment to track creation 
    private Long id; // Tracks primary sequential internal database index identifier

    @Column(name = "source_key", nullable = false, unique = true) // The hardware manufacturer's physical key/UUID marker 
    private String sourceKey; // Retains uniqueness representing the physical device ID telemetry 

    @ManyToOne // Connects hierarchically to a parent 'plant' object context
    @JoinColumn(name = "plant_id", nullable = false) // Maps structurally to the table plant identifier node natively 
    private SolarPlant solarPlant; // Maintains object reference to the facility managing this sensor array

    @OneToMany(mappedBy = "weatherSensor", cascade = CascadeType.ALL) // Link collection structure for associated weather streams
    private List<WeatherData> weatherData; // Accumulates historical readings fetched natively from this very hardware stream 

    /**
     * Base initialization routine. Required by reflection tools like Hibernate. 
     */
    public WeatherSensor() {
        // Does absolutely nothing natively, simply fulfills the proxy expectation protocol 
    }

    // --- Getter and Setter Configuration Operations --- 

    public Long getId() {
        return id; // Pull the internal tracking identity sequence 
    }

    public void setId(Long id) {
        this.id = id; // Replace internal tracking identity sequence 
    }

    public String getSourceKey() {
        return sourceKey; // Retrieve the unique hardware ID serial 
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey; // Re-configure hardware ID target string identifier  
    }

    public SolarPlant getSolarPlant() {
        return solarPlant; // Discover the facility location controlling this probe
    }

    public void setSolarPlant(SolarPlant solarPlant) {
        this.solarPlant = solarPlant; // Overwrite the location controller hierarchy assignment 
    }

    public List<WeatherData> getWeatherData() {
        return weatherData; // Dump out the associated metric collection arrays linked to the device node
    }

    public void setWeatherData(List<WeatherData> weatherData) {
        this.weatherData = weatherData; // Bind an array of physical data attributes to this sensor object
    }
}
