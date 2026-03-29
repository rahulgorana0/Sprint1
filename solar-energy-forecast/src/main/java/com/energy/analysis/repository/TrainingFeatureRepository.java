package com.energy.analysis.repository;

import com.energy.analysis.model.TrainingFeature; // Data representation used primarily for WEKA Training 
import org.springframework.data.jpa.repository.JpaRepository; // Automated operations
import org.springframework.data.jpa.repository.Modifying; // Denotes DB side effects 
import org.springframework.data.jpa.repository.Query; // Arbitrary SQL support plugin  
import org.springframework.stereotype.Repository; // Tells builder to auto-wire the implementation
import org.springframework.transaction.annotation.Transactional; // Manages execution rollback protections 

import java.util.stream.Stream; // Extremely fast DB-Cursor output representation avoiding OOM errors

/**
 * Repository interface administering read/write configurations against TrainingFeature tables.
 * Used fundamentally to bridge raw logs over into pre-structured ML row sets. 
 */
@Repository // Auto-injects instances where Autowired
public interface TrainingFeatureRepository extends JpaRepository<TrainingFeature, Long> {
    
    /**
     * Streams literally all available historical training features sequenced chronologically.
     * Employs streaming architecture specifically to prevent running application out of heap memory.
     * 
     * @return Forward-only sequential processing pipe
     */
    @Transactional(readOnly = true) // Disables commit checks internally speeding execution 
    Stream<TrainingFeature> findAllByOrderByDateTimeAsc();

    /**
     * Queries exclusively the training data attached directly to one particular facility location.
     * 
     * @param plantId ID of specific plant logic targeting  
     * @return A stream configured to process data arrays efficiently
     */
    @Transactional(readOnly = true) // Ensures safe memory footprint extraction without dirty-reads  
    Stream<TrainingFeature> findAllBySolarPlantIdOrderByDateTimeAsc(Long plantId);

    /**
     * Crucial custom SQL query responsible for collapsing disparate data sources into a unified feature set natively.
     * The database executes this vastly faster than iterating manually inside Java memory.
     */
    @Modifying // Specifies this interacts & mutates tables 
    @Transactional // Commits changes as a single transaction block 
    @Query(value = "INSERT INTO training_features (plant_id, date_time, irradiation_wm2, ambient_temp_c, sum_ac_power_kw) " +
                   "SELECT p.solar_plant_id, g.date_time, w.irradiation_wm2, w.ambient_temp_c, SUM(g.ac_power_kw) " +
                   "FROM solar_plant p " +
                   "JOIN solar_inverter i ON p.solar_plant_id = i.plant_id " +
                   "JOIN generation_data g ON i.solar_inverter_id = g.inverter_id " +
                   "JOIN weather_sensor s ON p.solar_plant_id = s.plant_id " +
                   "JOIN weather_data w ON s.weather_sensor_id = w.sensor_id AND g.date_time = w.date_time " +
                   "GROUP BY p.solar_plant_id, g.date_time, w.irradiation_wm2, w.ambient_temp_c", 
           nativeQuery = true) // Force JPA to ignore entity translation and feed specific native SQL 
    void populateFromJoins();
}
