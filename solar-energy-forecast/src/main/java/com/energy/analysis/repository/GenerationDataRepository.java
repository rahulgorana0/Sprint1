package com.energy.analysis.repository;

import com.energy.analysis.model.GenerationData; // Generation entity mapping
import org.springframework.data.jpa.repository.JpaRepository; // Automates standard database operations
import org.springframework.stereotype.Repository; // Configures runtime injection contexts
import java.time.LocalDateTime; // Time logic limits
import java.util.List; // Output sequential structures
import java.util.Optional; // Safe Null returns

/**
 * Repository interface targeting historic generation telemetry measured directly at invertors.
 */
@Repository // Handled entirely via proxy derivation using method naming conventions automatically
public interface GenerationDataRepository extends JpaRepository<GenerationData, Long> {
    
    /**
     * Retrieves chronological historic payload lists across a time-bounded window.
     * Specifically traverses the relationship: Inverter -> Plant to consolidate child data from all array outputs attached.
     * 
     * @param plantId Source facility root ID target
     * @param start Initial query bounding sequence 
     * @param end Final query boundary limitation
     * @return Ascending timeline of energy actual yields mapping out production history
     */
    List<GenerationData> findBySolarInverterSolarPlantIdAndDateTimeBetweenOrderByDateTimeAsc(Long plantId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Queries for the absolute newest timestamp log successfully recorded for a plant.
     * 
     * @param plantId Contextual map target facility 
     * @return The latest possible generation event recorded within the system attached to this specific plant 
     */
    Optional<GenerationData> findTopBySolarInverterSolarPlantIdOrderByDateTimeDesc(Long plantId);
}
