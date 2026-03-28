package com.energy.analysis.repository;

import com.energy.analysis.model.WeatherData; // Weather parameters tracked physically 
import org.springframework.data.jpa.repository.JpaRepository; // Default functions 
import org.springframework.stereotype.Repository; // Configures runtime injections
import java.time.LocalDateTime; // Datetime handling 
import java.util.List; // Supports array queries

/**
 * Accessor component managing the persistable 'weather_data' table logs.
 */
@Repository // Configured into dependency mappings
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    
    /**
     * Scans telemetry metrics originated purely from a specific sensor across a mapped duration.
     * 
     * @param sensorId Origin hardware identifier 
     * @param start Initial chronological bounding frame
     * @param end Termination bounding limitation
     * @return Array capturing local site weather progressions 
     */
    List<WeatherData> findByWeatherSensorIdAndDateTimeBetween(Long sensorId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Combines metrics from all sensors attached globally across the scope of a plant inside a timeframe.
     * Ordering ensures chronological stability during plotting/aggregation later.
     * 
     * @param plantId Master site logical node 
     * @param start Initial timestamp definition 
     * @param end Terminal timestamp requirement 
     * @return Sequentially sorted arrays covering broader aggregate zones
     */
    List<WeatherData> findByWeatherSensorSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(Long plantId, LocalDateTime start, LocalDateTime end);
}
