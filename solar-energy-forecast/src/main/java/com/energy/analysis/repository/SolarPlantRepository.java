package com.energy.analysis.repository;

import com.energy.analysis.model.SolarPlant; // Maps the primary root solar facility
import org.springframework.data.jpa.repository.JpaRepository; // Default JPA configurations
import org.springframework.stereotype.Repository; // Registers into applications context 
import java.util.Optional; // Safe response type handling null boundaries

/**
 * Interface configuring persistence controls strictly for the root-level SolarPlant entity.
 */
@Repository // Configures entity to participate in Spring Dependency Injection automatically 
public interface SolarPlantRepository extends JpaRepository<SolarPlant, Long> {
    
    /**
     * Resolves a distinct SolarPlant entity wrapper given a deterministic physical `plantId` string key.
     * Frequently used to securely bootstrap system operations tracking back to local entity data.
     * 
     * @param plantId Originating hash or explicit string identity describing physical location 
     * @return Optional wrapping the existing Database entry.
     */
    Optional<SolarPlant> findByPlantId(String plantId);
}
