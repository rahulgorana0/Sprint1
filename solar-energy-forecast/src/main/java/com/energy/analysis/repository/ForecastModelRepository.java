package com.energy.analysis.repository;

import com.energy.analysis.model.ForecastModel; // Machine Learning metadata entity object
import org.springframework.data.jpa.repository.JpaRepository; // Inherited persistence context
import org.springframework.stereotype.Repository; // Marker for component scanning
import java.util.Optional; // Encapsulates results avoiding NullPointerExceptions

/**
 * Repository dedicated to managing ML algorithm tracking instances for distinct generation facilities.
 */
@Repository // Promotes integration into Springs internal data orchestration cycle
public interface ForecastModelRepository extends JpaRepository<ForecastModel, Long> {
    
    /**
     * Attempts to resolve the singular current 'active' machine learning model dedicated to a specific plant node.
     * 
     * @param plantId ID of the parent facility
     * @return A safely wrapped Optional object containing the latest validated model configuration, or Empty.
     */
    Optional<ForecastModel> findBySolarPlantIdAndIsActiveTrue(Long plantId);
}
