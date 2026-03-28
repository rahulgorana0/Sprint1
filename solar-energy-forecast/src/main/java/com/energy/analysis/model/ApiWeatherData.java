package com.energy.analysis.model;

import jakarta.persistence.*; // Import JPA annotations for ORM mapping
import java.time.LocalDateTime; // Import LocalDateTime for date-time fields

/**
 * Entity to store weather data fetched from external APIs.
 * This class maps to the 'api_weather_data' table in the database.
 * We use this to persist third-party weather information necessary for solar forecasting.
 */
@Entity // Specifies that the class is an entity and is mapped to a database table
@Table(name = "api_weather_data") // Specifies the name of the database table to be used for mapping
public class ApiWeatherData {

    @Id // Specifies the primary key of an entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Provides for the specification of generation strategies for the values of primary keys
    private Long id; // Unique identifier for each API weather data record

    @ManyToOne // Defines a many-to-one relationship with SolarPlant; many weather data records can belong to one plant
    @JoinColumn(name = "plant_id", nullable = false) // Specifies the foreign key column 'plant_id' which cannot be null
    private SolarPlant solarPlant; // Reference to the associated SolarPlant entity

    @Column(name = "date_time", nullable = false) // Maps the field to the 'date_time' column, enforcing it to be non-null
    private LocalDateTime dateTime; // Timestamp for when this weather data is applicable

    @Column(name = "temp_c", nullable = false) // Maps the field to 'temp_c' (temperature in Celsius)
    private Double tempC; // Ambient temperature in degrees Celsius at the reported time

    @Column(name = "irradiation_wm2", nullable = false) // Maps the field to 'irradiation_wm2'
    private Double irradiationWm2; // Solar irradiation measured in Watts per square meter

    @Column(name = "is_forecast", nullable = false) // Maps the field 'is_forecast' to denote if this is predicted weather
    private boolean isForecast; // Flag indicating if this record is a forecast (true) or historical observation (false)

    @OneToOne(mappedBy = "apiWeatherData", cascade = CascadeType.ALL) // One-to-one relationship with ForecastData, cascading all operations
    private ForecastData forecastData; // The corresponding prediction/forecast data linked to this weather record

    /**
     * Default constructor required by JPA.
     */
    public ApiWeatherData() {
        // Empty constructor needed for Hibernate to instantiate the object via reflection
    }

    // --- Getters and Setters section ---
    // These methods allow encapsulation, letting us securely read/write entity properties.

    public Long getId() {
        return id; // Return the primary key ID
    }

    public void setId(Long id) {
        this.id = id; // Set the primary key ID
    }

    public SolarPlant getSolarPlant() {
        return solarPlant; // Return the associated Solar Plant
    }

    public void setSolarPlant(SolarPlant solarPlant) {
        this.solarPlant = solarPlant; // Set the associated Solar Plant
    }

    public LocalDateTime getDateTime() {
        return dateTime; // Return the timestamp of the weather data
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime; // Set the timestamp of the weather data
    }

    public Double getTempC() {
        return tempC; // Return the temperature in Celsius
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC; // Set the temperature in Celsius
    }

    public Double getIrradiationWm2() {
        return irradiationWm2; // Return the solar irradiation
    }

    public void setIrradiationWm2(Double irradiationWm2) {
        this.irradiationWm2 = irradiationWm2; // Set the solar irradiation
    }

    public boolean isForecast() {
        return isForecast; // Check if the data is a forecast
    }

    public void setForecast(boolean forecast) {
        isForecast = forecast; // Update the forecast status flag
    }

    public ForecastData getForecastData() {
        return forecastData; // Get linked forecast data
    }

    public void setForecastData(ForecastData forecastData) {
        this.forecastData = forecastData; // Update linked forecast data
    }
}
