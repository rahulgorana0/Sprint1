package com.energy.analysis.model;

import jakarta.persistence.*; // JPA ORM tools
import java.time.LocalDateTime; // Datetime handling handling
import java.util.List; // Collection support

/**
 * Entity representing a Forecast Model trained for a specific Solar Plant.
 * Tracks ML model metadata, evaluation metrics, and references specific saved model files.
 */
@Entity // Indicates a persistent model entity
@Table(name = "forecast_model") // Table name mapping
public class ForecastModel {

    @Id // Primary key modifier
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment behavior
    private Long id; // Unique model identifier

    @ManyToOne // Multiple models can be generated per solar plant
    @JoinColumn(name = "plant_id", nullable = false) // Foreign Key relationship to plant table
    private SolarPlant solarPlant; // The plant this model caters to

    @Column(name = "model_name", nullable = false) // Model file name or internal ID name
    private String modelName; // Used to identify the .model file on the disk

    @Column(name = "accuracy_mae") // Mean Absolute Error metric for evaluating the regression model
    private Double accuracyMae; // Represents absolute average variance from actual values

    @Column(name = "accuracy_rmse") // Root Mean Square Error metric
    private Double accuracyRmse; // Imposes heavier penalties on larger prediction outliers

    @Column(name = "training_date") // Timestamps when the model learned from historical data
    private LocalDateTime trainingDate; // Crucial for defining model drift/staleness

    @Column(name = "is_active") // Soft-deletion / current usage flag
    private boolean isActive; // Denotes if the system uses this model currently for ongoing forecasts

    @OneToMany(mappedBy = "forecastModel", cascade = CascadeType.ALL) // Inverse mapping for predictions generated
    private List<ForecastData> forecastData; // All active predictions spawned by this specific model instance

    /**
     * Default constructor for Hibernate mapping.
     */
    public ForecastModel() {
        // Keeps JPA happy
    }

    // --- Standard Getters / Setters ---
    
    public Long getId() {
        return id; // Obtain DB primary key
    }

    public void setId(Long id) {
        this.id = id; // Set DB primary key
    }

    public SolarPlant getSolarPlant() {
        return solarPlant; // Get associated generation facility
    }

    public void setSolarPlant(SolarPlant solarPlant) {
        this.solarPlant = solarPlant; // Bind model to generation facility
    }

    public String getModelName() {
        return modelName; // Retrieve disk filename
    }

    public void setModelName(String modelName) {
        this.modelName = modelName; // Store disk filename
    }

    public Double getAccuracyMae() {
        return accuracyMae; // Retrieve MAE
    }

    public void setAccuracyMae(Double accuracyMae) {
        this.accuracyMae = accuracyMae; // Submit evaluated MAE
    }

    public Double getAccuracyRmse() {
        return accuracyRmse; // Retrieve RMSE
    }

    public void setAccuracyRmse(Double accuracyRmse) {
        this.accuracyRmse = accuracyRmse; // Submit evaluated RMSE
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate; // Get training execution time
    }

    public void setTrainingDate(LocalDateTime trainingDate) {
        this.trainingDate = trainingDate; // Set training execution time
    }

    public boolean isActive() {
        return isActive; // Check if model serves active traffic
    }

    public void setActive(boolean active) {
        isActive = active; // Update active traffic mapping flag
    }

    public List<ForecastData> getForecastData() {
        return forecastData; // Review historical outputs
    }

    public void setForecastData(List<ForecastData> forecastData) {
        this.forecastData = forecastData; // Define historical outputs
    }
}
