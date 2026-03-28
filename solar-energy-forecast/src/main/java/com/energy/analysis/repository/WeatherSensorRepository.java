package com.energy.analysis.repository;

import com.energy.analysis.model.WeatherSensor; // Identifies the environment parsing IoT node
import org.springframework.data.jpa.repository.JpaRepository; // Pull basic SQL actions
import org.springframework.stereotype.Repository; // Component definition  
import java.util.Optional; // Safe packaging 
import java.util.List; // Response grouping arrays

/**
 * Repository interface abstracting direct database manipulations targeting weather sensor node configurations.
 */
@Repository // Forces scanning detection into service layers 
public interface WeatherSensorRepository extends JpaRepository<WeatherSensor, Long> {
    
    /**
     * Correlates an application entity object tightly utilizing external physical tracking identifiers. 
     * Used predominantly when new data pushes hit the API tagged only via hardware identification strings.
     * 
     * @param sourceKey The universally unique external identifier of the object node
     * @return Safely encapsulated WeatherSensor matching reality, or Empty  
     */
    Optional<WeatherSensor> findBySourceKey(String sourceKey);
    
    /**
     * Evaluates total hardware sensor inventory attached to a specified location node.
     * 
     * @param plantId The root ID managing arrays of distributed sensors
     * @return Complete list of mapped active hardware units 
     */
    List<WeatherSensor> findBySolarPlantId(Long plantId);
}
