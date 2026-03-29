package com.energy.analysis.model;

import jakarta.persistence.*; // Persistance mapping
import java.time.LocalDateTime; // Datetime tracking

/**
 * Entity representing daily yield metrics for an inverter.
 * Captures historical performance per individual inverter over unified 24-hr windows.
 */
@Entity // Database mappable Pojo
@Table(name = "daily_metrics") // Specifies explicit table label
public class DailyMetrics {

    @Id // Surrogate primary identity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Relies on database sequencing or auto-increment
    @Column(name = "daily_metrics_id")
    private Long id; // Individual Metric row sequence

    @ManyToOne // One inverter produces multiple daily metrics naturally over time
    @JoinColumn(name = "inverter_id", nullable = false) // Foreign key relationship pointing to solar_inverter
    private SolarInverter solarInverter; // Link to the specific AC-DC Inverter device being measured

    @Column(name = "date_time", nullable = false) // The 24-hour window slice representation
    private LocalDateTime dateTime; // Tracks the day of capture (usually midnight or local rollover)

    @Column(name = "daily_yield_kwh", nullable = false) // Cumulative energy yielded for specifically this single day
    private Double dailyYieldKwh; // Measured in Kilowatt-hours

    @Column(name = "total_yield_kwh", nullable = false) // Absolute odometer rating (lifetime accumulated yield)
    private Double totalYieldKwh; // Measured in Kilowatt-hours, useful for computing deltas independently if daily is lost

    /**
     * Standard parameterless constructor.
     */
    public DailyMetrics() {
        // JPA standard proxy initialization requirement
    }

    // --- Accessor Property Methods ---

    public Long getId() {
        return id; // Accessrow pointer ID
    }

    public void setId(Long id) {
        this.id = id; // Mutate ID wrapper
    }

    public SolarInverter getSolarInverter() {
        return solarInverter; // Extract source hardware config mapping
    }

    public void setSolarInverter(SolarInverter solarInverter) {
        this.solarInverter = solarInverter; // Bind hardware configuration node
    }

    public LocalDateTime getDateTime() {
        return dateTime; // Grab contextual timeline stamp
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime; // Provide date index for window
    }

    public Double getDailyYieldKwh() {
        return dailyYieldKwh; // Review snapshot day yield
    }

    public void setDailyYieldKwh(Double dailyYieldKwh) {
        this.dailyYieldKwh = dailyYieldKwh; // Setup daily snapshot value
    }

    public Double getTotalYieldKwh() {
        return totalYieldKwh; // Fetch odometer tally
    }

    public void setTotalYieldKwh(Double totalYieldKwh) {
        this.totalYieldKwh = totalYieldKwh; // Persist updated odometer limit
    }
}
