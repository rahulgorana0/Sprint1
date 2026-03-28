package com.energy.analysis.model;

import jakarta.persistence.*; // Maps to SQL equivalents
import java.time.LocalDateTime; // Standard chronological construct point 

/**
 * Entity representing power generation data for an inverter.
 * Samples momentary active generation output mapped to a timestamp.
 */
@Entity // Relational Object wrapper
@Table(name = "generation_data") // Data persisted to 'generation_data' structure
public class GenerationData {

    @Id // Denotes the primary ID column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates unique integer values
    private Long id; // The surrogate integer key

    @ManyToOne // Multiple generation logs originate from a single hardware inverter
    @JoinColumn(name = "inverter_id", nullable = false) // Mapped directly to solar_inverter schema 
    private SolarInverter solarInverter; // Link to the hardware device emitting this output reading

    @Column(name = "date_time", nullable = false) // Granular reading timestamp 
    private LocalDateTime dateTime; // Recorded generation instance down to the millisecond/second.

    @Column(name = "dc_power_kw", nullable = false) // Raw string generation measured pre-inversion
    private Double dcPowerKw; // Direct Current measurement emitted directly from the PV arrays

    @Column(name = "ac_power_kw", nullable = false) // The resulting utility-compatible frequency inverted power
    private Double acPowerKw; // Alternating Current output (after inversion loss mechanics) 

    /**
     * Needed for generic instantiation by backend persistence context.
     */
    public GenerationData() {
        // Leave empty for basic class generation 
    }

    // --- Property Management ---

    public Long getId() {
        return id; // Securely retrieve immutable ID
    }

    public void setId(Long id) {
        this.id = id; // Hard override configuration pointer (usually framework bound only)
    }

    public SolarInverter getSolarInverter() {
        return solarInverter; // Discover linked telemetry device module
    }

    public void setSolarInverter(SolarInverter solarInverter) {
        this.solarInverter = solarInverter; // Allocate device dependency context
    }

    public LocalDateTime getDateTime() {
        return dateTime; // Evaluate chronology point
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime; // Establish the generation timestamp origin
    }

    public Double getDcPowerKw() {
        return dcPowerKw; // Read pre-inversion energy scale
    }

    public void setDcPowerKw(Double dcPowerKw) {
        this.dcPowerKw = dcPowerKw; // Bind incoming array payload metric
    }

    public Double getAcPowerKw() {
        return acPowerKw; // Read useful outbound AC power metric
    }

    public void setAcPowerKw(Double acPowerKw) {
        this.acPowerKw = acPowerKw; // Bind inverter delivery metric
    }
}
