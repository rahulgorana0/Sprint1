package com.energy.analysis.repository;

import com.energy.analysis.model.DailyMetrics; // Daily stat tracking entity
import org.springframework.data.jpa.repository.JpaRepository; // JPA abstractions
import org.springframework.stereotype.Repository; // Spring framework DAO marker
import java.time.LocalDateTime; // Standard time structure
import java.util.List; // For sequential query results

/**
 * Repository Data Access Object for historical daily yield trackers.
 * Responsible for querying large-scale rolled-up metrics.
 */
@Repository // Instructs Spring Boot to load this as an instanced Bean for Dependency Injection
public interface DailyMetricsRepository extends JpaRepository<DailyMetrics, Long> {
    
    /**
     * Finds time-series rolled-up metric logs traversing across a designated solar plant for a window in time.
     * 
     * @param plantId Contextual facility root 
     * @param start Initial chronological bounding constraint
     * @param end Final chronological bounding constraint
     * @return Ordered continuous array of historical day-to-day metrics ready for reporting. 
     */
    List<DailyMetrics> findBySolarInverterSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(Long plantId, LocalDateTime start, LocalDateTime end);
}
