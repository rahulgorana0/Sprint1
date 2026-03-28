package com.energy.analysis.repository;

import com.energy.analysis.model.SolarInverter; // Targeting inverter Entity Type
import org.springframework.data.jpa.repository.JpaRepository; // Built in ORM repository functions
import org.springframework.stereotype.Repository; // Configures it as a component candidate 
import java.util.Optional; // Safe NULL-handling
import java.util.List; // Handling lists

/**
 * Access interface managing operations for Solar Inverter database entities.
 */
@Repository // Auto-implements all routine methods allowing save, delete, and retrieval out of the box
public interface SolarInverterRepository extends JpaRepository<SolarInverter, Long> {
    
    /**
     * Resolves an internal application 'SolarInverter' object utilizing an external physical hardware ID key.
     * Useful for mapping incoming JSON/CSV telemetry where only a hardware ID is provided.
     * 
     * @param sourceKey The hardware identity of the object module payload 
     * @return The matched Inverter object wrapper 
     */
    Optional<SolarInverter> findBySourceKey(String sourceKey);
    
    /**
     * Obtains the comprehensive array of every inverter operating under one specified root facility. 
     * 
     * @param plantId ID pointing to the root plant
     * @return Fully populated object collection representing current plant topology 
     */
    List<SolarInverter> findBySolarPlantId(Long plantId);
}
