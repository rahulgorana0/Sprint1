package com.energy.analysis.repository;

import com.energy.analysis.model.ApiWeatherData; // Entity reference
import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPA support
import org.springframework.stereotype.Repository; // Bean annotation

import java.time.LocalDateTime; // Datetime support
import java.util.List; // Collection support
import java.util.Optional; // Safe null handling 

/**
 * Repository interface for managing weather data fetched from external APIs.
 * This class uses Spring Data JPA to automatically construct SQL queries based on method names.
 */
@Repository // Marks this interface as a Spring-managed Data Access Object (DAO)
public interface ApiWeatherDataRepository extends JpaRepository<ApiWeatherData, Long> {
    
    /**
     * Finds the single closest upcoming weather forecast after the provided time for a plant.
     * Ordered chronologically to ensure we just fetch the immediate next timestamp.
     * 
     * @param plantId ID of the solar plant
     * @param time The baseline time to search sequentially from
     * @return Optional containing the closest future prediction, or Empty if none exists.
     */
    Optional<ApiWeatherData> findFirstBySolarPlantIdAndDateTimeAfterOrderByDateTimeAsc(Long plantId, LocalDateTime time);

    /**
     * Finds the most recent weather data point logged for a particular plant.
     * Used for dashboard "current weather" displays.
     * 
     * @param plantId ID of the target solar plant
     * @return Optional containing the most recent log, or Empty if no logs exist.
     */
    Optional<ApiWeatherData> findTopBySolarPlantIdOrderByDateTimeDesc(Long plantId);

    /**
     * Fetches a continuous stream of weather records for a plant falling within a specific time window.
     * Essential for charting time-series arrays over X days.
     * 
     * @param plantId Targeting the specific facility
     * @param start The absolute beginning of the time window
     * @param end The absolute end of the time window
     * @return List of matching sequential chronologically ordered weather data entries.
     */
    List<ApiWeatherData> findBySolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(Long plantId, LocalDateTime start, LocalDateTime end);
}
