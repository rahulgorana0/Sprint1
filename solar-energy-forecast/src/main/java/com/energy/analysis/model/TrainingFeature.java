package com.energy.analysis.model;

import jakarta.persistence.*; // Support for JPA annotations
import java.time.LocalDateTime; // Datetime structure

/**
 * Entity representing unified features for ML model training.
 * Aggregates various sensor telemetry into canonical training rows.
 */
@Entity // Treat as JPA entity table
@Table(name = "training_features") // Table name
public class TrainingFeature {

    @Id // Auto-assigned identity column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Strategy defaults to identity
    @Column(name = "training_features_id")
    private Long id; // Data row surrogate key

    @ManyToOne // Can be multiple rows for a single plant
    @JoinColumn(name = "plant_id", nullable = false) // Constrain relation locally
    private SolarPlant solarPlant; // Master node metadata tracking

    @Column(name = "date_time", nullable = false) // Time vector constraint
    private LocalDateTime dateTime; // Independent time variable 

    @Column(name = "irradiation_wm2", nullable = false) // Feature 1: solar irradiation
    private Double irradiationWm2; // Power density in Watts per Square Meter

    @Column(name = "ambient_temp_c", nullable = false) // Feature 2: outside temperature
    private Double ambientTempC; // External ambient temperature

    @Column(name = "sum_ac_power_kw", nullable = false) // The target / dependent variable 'Y' during training
    private Double sumAcPowerKw; // Truth label for generation yield used during back-prop/fitting

    /**
     * Default constructor for ORM requirement.
     */
    public TrainingFeature() {
        // Requisite empty constructor
    }

    // --- Accessors & Mutators ---

    public Long getId() {
        return id; // Fetch identity
    }

    public void setId(Long id) {
        this.id = id; // Modify identity
    }

    public SolarPlant getSolarPlant() {
        return solarPlant; // Reference plant
    }

    public void setSolarPlant(SolarPlant solarPlant) {
        this.solarPlant = solarPlant; // Establish plant attachment
    }

    public LocalDateTime getDateTime() {
        return dateTime; // Time index output
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime; // Establish time constraints
    }

    public Double getIrradiationWm2() {
        return irradiationWm2; // Fetch training feature value
    }

    public void setIrradiationWm2(Double irradiationWm2) {
        this.irradiationWm2 = irradiationWm2; // Provide training feature observation
    }

    public Double getAmbientTempC() {
        return ambientTempC; // Fetch temp condition
    }

    public void setAmbientTempC(Double ambientTempC) {
        this.ambientTempC = ambientTempC; // Update temp condition
    }

    public Double getSumAcPowerKw() {
        return sumAcPowerKw; // Expose regression truth variable
    }

    public void setSumAcPowerKw(Double sumAcPowerKw) {
        this.sumAcPowerKw = sumAcPowerKw; // Mutate truth value
    }
}
