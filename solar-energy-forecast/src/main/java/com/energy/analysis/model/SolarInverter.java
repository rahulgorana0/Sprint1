package com.energy.analysis.model;

import jakarta.persistence.*; // Pull JPA features
import java.util.List; // Enable list arrays mapping

/**
 * Entity representing a hardware Solar Inverter.
 * Serves as the child grouping boundary in a Solar Plant topography containing multiple arrays.
 */
@Entity // Represents a database row
@Table(name = "solar_inverter") // Points to solar_inverter SQL structure
public class SolarInverter {

    @Id // Dictates DB primary 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Delegate integer sequence to DB
    @Column(name = "solar_inverter_id")
    private Long id; // Surrogate row key

    @Column(name = "source_key", nullable = false, unique = true) // Device UUID tracking (usually from telemetry equipment)
    private String sourceKey; // Uniquely identifies the original manufacturer hardware identifier 

    @ManyToOne // Represents a subset/child under a generalized Plant node
    @JoinColumn(name = "plant_id", nullable = false) // Foreign key relationship pointing to root SolarPlant
    private SolarPlant solarPlant; // The actual plant holding this equipment item

    @OneToMany(mappedBy = "solarInverter", cascade = CascadeType.ALL) // Link collection for 1:N relations
    private List<GenerationData> generationData; // Houses the continuous generation power output logs sequentially

    @OneToMany(mappedBy = "solarInverter", cascade = CascadeType.ALL) // Defines 24-hr rolled metrics relationship
    private List<DailyMetrics> dailyMetrics; // Groups the static day-to-day odometer metrics

    /**
     * Mandatory uninitialized state constructor.
     */
    public SolarInverter() {
        // Retained for ORM standard proxy configurations
    }

    // --- Access Control / Setters ---

    public Long getId() {
        return id; // Pull ID index 
    }

    public void setId(Long id) {
        this.id = id; // Provide ID binding override
    }

    public String getSourceKey() {
        return sourceKey; // Check logical hardware ID key
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey; // Map external hardware ID for data tracing
    }

    public SolarPlant getSolarPlant() {
        return solarPlant; // Review parent plant location
    }

    public void setSolarPlant(SolarPlant solarPlant) {
        this.solarPlant = solarPlant; // Designate physical asset location parent
    }

    public List<GenerationData> getGenerationData() {
        return generationData; // Retrieve the full stream of historic measurements
    }

    public void setGenerationData(List<GenerationData> generationData) {
        this.generationData = generationData; // Configure generation list directly (usually managed bi-directionally) 
    }

    public List<DailyMetrics> getDailyMetrics() {
        return dailyMetrics; // Access daily roll-up statistics records
    }

    public void setDailyMetrics(List<DailyMetrics> dailyMetrics) {
        this.dailyMetrics = dailyMetrics; // Save rolled stats 
    }
}
