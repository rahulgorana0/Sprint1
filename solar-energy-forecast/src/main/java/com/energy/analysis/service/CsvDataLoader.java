package com.energy.analysis.service; // Defines service package layout

import com.energy.analysis.model.*; // Object mappings imports
import com.energy.analysis.repository.*; // Data layers injected 
import com.opencsv.CSVReader; // Utility assisting in structured comma processing
import com.opencsv.exceptions.CsvValidationException; // CSV Validation checks 
import jakarta.annotation.PostConstruct; // Spring application start triggers
import org.slf4j.Logger; // Log routing protocol
import org.slf4j.LoggerFactory; // Class instantiator function
import org.springframework.beans.factory.annotation.Autowired; // Managed DI framework tags
import org.springframework.stereotype.Service; // Configures object lifecycle bounds
import org.springframework.transaction.annotation.Transactional; // Database protections against partial execution bugs

import java.io.File; // Abstract filesystem mapping bounds 
import java.io.FileReader; // Physical byte scanning 
import java.io.IOException; // Broad stream I/O errors handling
import java.time.LocalDateTime; // Datetime handling 
import java.time.format.DateTimeFormatter; // Datetime logic masking templates 
import java.util.ArrayList; // Collections wrapper
import java.util.HashMap; // Lookup structures 
import java.util.List; // Basic collection abstraction definition 
import java.util.Map; // Mapping representation requirements

/**
 * Service to handle the ingestion of Kaggle CSV datasets into the new schema.
 * Operates principally on system start-up.
 */
@Service // Ingests into Spring Dependency mappings natively
public class CsvDataLoader {

    // Configures dedicated log channel directly identifying actions spawned directly here 
    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);
    
    // Hardcoded permanent repository references. Best practice protecting concurrent modification flaws
    private final SolarPlantRepository plantRepo; // Primary mapping tracker
    private final SolarInverterRepository inverterRepo; // Generation nodes tracker 
    private final WeatherSensorRepository sensorRepo; // Environment sensor tracking points
    private final GenerationDataRepository generationRepo; // Generation row metrics 
    private final DailyMetricsRepository metricsRepo; // Day rollups database arrays
    private final WeatherDataRepository weatherRepo; // Physical dataset conditions 
    private final TrainingFeatureRepository trainingRepo; // Unified training table operations 

    // Reusable formatter instance targeting native Kaggle strings perfectly (Faster than instantiating per-row!)
    private static final DateTimeFormatter FORMATTER_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Primary sequence
    private static final DateTimeFormatter FORMATTER_2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"); // Alternative logic 

    /**
     * Requisite Constructor. Spring explicitly manages parsing these interfaces resolving implementations natively
     */
    @Autowired // Signal instruction config to spring  
    public CsvDataLoader(SolarPlantRepository plantRepo,
                         SolarInverterRepository inverterRepo,
                         WeatherSensorRepository sensorRepo,
                         GenerationDataRepository generationRepo,
                         DailyMetricsRepository metricsRepo,
                         WeatherDataRepository weatherRepo,
                         TrainingFeatureRepository trainingRepo) {
        this.plantRepo = plantRepo; // Plant access logic assigned 
        this.inverterRepo = inverterRepo; // Generation layer logic bounded
        this.sensorRepo = sensorRepo; // Sensors structure bound 
        this.generationRepo = generationRepo; // Telemetry logic connected
        this.metricsRepo = metricsRepo; // Rollups connected 
        this.weatherRepo = weatherRepo; // Environment connected 
        this.trainingRepo = trainingRepo; // Feature DB connected
    }

    /**
     * Executes entirely automatically exactly once after Spring configures this complete module layer.
     */
    @PostConstruct // Directs Framework automatic execution rules. Replaces Init scripts! 
    public void loadCsvData() {
        String dataDir = "src/main/resources/data"; // Directory tracking variable
        new File(dataDir).mkdirs(); // Failsafe filesystem command assuring the structure actually manifests 

        // Generates java abstraction configurations targeting expected dataset formats 
        File generationFile = new File(dataDir + "/Plant_1_Generation_Data.csv"); // Locate sample gen string 
        File weatherFile = new File(dataDir + "/Plant_1_Weather_Sensor_Data.csv"); // Locate sample env string 

        // Hard checks if files are completely missing halting program execution
        if (!generationFile.exists() || !weatherFile.exists()) {
            // Logs out warning directly preventing total crash scenarios
            log.warn("Kaggle CSV files do not exist in {} directory.", dataDir);
            return; // Stops initialization loop silently 
        }

        // We check row quantity on Generation repo. If exactly 0, proceed with heavy IO string digestion  
        if (generationRepo.count() == 0) {
            log.info("Ingesting Plant Generation CSV data..."); // Report initiation metrics
            ingestGenerationData(generationFile); // Pass file IO bound string targeting internal digestion protocol
        }

        // We check Weather row quantity. 0 flags uninitialized configuration bounds. 
        if (weatherRepo.count() == 0) {
            log.info("Ingesting Weather Sensor CSV data..."); // Notify startup bounds
            ingestWeatherData(weatherFile); // Hand over parsing configuration directly
        }

        // Verifies Generation and Weather arrays populated totally but the aggregated Training dataset sits unformed 
        if (trainingRepo.count() == 0 && generationRepo.count() > 0 && weatherRepo.count() > 0) {
            log.info("Populating Training Features..."); // Signal complex join executions 
            populateTrainingFeatures(); // Orchestrate table aggregations directly through DB internal mechanics 
        }

        log.info("ETL CSV processing complete."); // Report total initialization success scenario bounds. 
    }

    /**
     * Try-catch parser logic safely evaluating diverse external date structures returning precise internal Localdate variables 
     */
    private LocalDateTime parseDate(String dateStr) {
        try {
            // Parse attempting standard format configuration rules  
            return LocalDateTime.parse(dateStr, FORMATTER_1); 
        } catch (Exception e) {
            // Drop back executing slower alternative format upon explicit string matching errors 
            return LocalDateTime.parse(dateStr, FORMATTER_2); 
        }
    }

    /**
     * Scans physical filesystem line-by-line generating database rows mapping to generation telemetry attributes
     */
    private void ingestGenerationData(File file) {
        // Open IO StreamReader tied to File Object executing safe-closure try blocks 
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line; // Buffer configuration variable 
            reader.readNext(); // Consume array zero deliberately skipping text headers!
            
            // In-memory cache dictionaries avoiding punishing database query calls per every line evaluation loop bounds 
            Map<String, SolarPlant> plantCache = new HashMap<>(); // Tracks parsed parents natively locally
            Map<String, SolarInverter> inverterCache = new HashMap<>(); // Native inverted configuration tracking array map bounds
            Map<String, Double> plantMaxAc = new HashMap<>(); // Records running totals computing massive dataset bounds seamlessly 
            
            // Buffer queues explicitly allowing us to orchestrate bulk SQL inserts. Inserting row by row takes astronomically longer!
            List<GenerationData> genBatch = new ArrayList<>(); // Power generation rows
            List<DailyMetrics> metricsBatch = new ArrayList<>(); // Metrics tracking queues
            
            // Execute loop mapping until IO Reader natively issues a NULL terminating string definition 
            while ((line = reader.readNext()) != null) {
                // Safeguard rejecting malformed partial lines skipping instantly 
                if (line.length < 7) continue; 
                
                String plantIdStr = line[1]; // Column 2 targeting string ID mapping
                String sourceKey = line[2]; // Column 3 specifying UUID strings 
                
                // Map plant objects leveraging Cache dictionaries ensuring objects spawn entirely once saving thousands of queries 
                SolarPlant plant = plantCache.computeIfAbsent(plantIdStr, id -> 
                    // Database fallback querying 
                    plantRepo.findByPlantId(id).orElseGet(() -> {
                        SolarPlant newPlant = new SolarPlant(); // Map generation layout
                        newPlant.setPlantId(id); // Write configuration target ID
                        newPlant.setCapacityMw(0.0); // Configure temporary marker holding tracking array (Updated later!)
                        newPlant.setLocation("Gandhinagar"); // Define hard reference default tracking parameter
                        return plantRepo.save(newPlant); // Persist configuration explicitly to root node mapping structures
                    })
                );
                
                // Construct inverter representations caching outputs checking UUID mappings intelligently
                SolarInverter inverter = inverterCache.computeIfAbsent(sourceKey, key -> 
                    inverterRepo.findBySourceKey(key).orElseGet(() -> {
                        SolarInverter newInverter = new SolarInverter(); // Create mapping nodes
                        newInverter.setSourceKey(key); // Tie explicit string identities 
                        newInverter.setSolarPlant(plant); // Link hierarchical bindings mapping directly cleanly
                        return inverterRepo.save(newInverter); // Persist node representations natively into bounds 
                    })
                );
                
                // Translate text time object executing function tracking limits  
                LocalDateTime dateTime = parseDate(line[0]); 
                
                // Initialize clean generation row representations 
                GenerationData genData = new GenerationData(); // Blank node
                genData.setSolarInverter(inverter); // Establish DB pointer tracking targets locally
                genData.setDateTime(dateTime); // Push time index references natively 
                genData.setDcPowerKw(Double.parseDouble(line[3])); // Safe string primitive casting resolving DC tracking metrics 
                double acPower = Double.parseDouble(line[4]); // Save explicit AC primitive extraction bounds directly 
                genData.setAcPowerKw(acPower); // Implement AC property binding layouts actively
                genBatch.add(genData); // Deposit assembled node configuration mapping representations directly onto bulk stack list  
                
                // Execute highly optimized rolling max-computation logic updating Cache dictionaries concurrently 
                plantMaxAc.compute(plantIdStr, (k, currentMax) -> currentMax == null ? acPower : Math.max(currentMax, acPower));
                
                // Deploy daily tracking metric mappings simultaneously 
                DailyMetrics metrics = new DailyMetrics(); // Object layout
                metrics.setSolarInverter(inverter); // Push configuration targeting explicit references 
                metrics.setDateTime(dateTime); // Inject sequence timeframe markers
                metrics.setDailyYieldKwh(Double.parseDouble(line[5])); // Rollup mapping primitive bindings 
                metrics.setTotalYieldKwh(Double.parseDouble(line[6])); // Assign odometer tally configurations
                metricsBatch.add(metrics); // Enqueue payload layout targets completely successfully 
                
                // Orchestrate mass SQL flush executing queries across explicit bounds  
                if (genBatch.size() >= 10000) { // Block limitation
                    generationRepo.saveAll(genBatch); // Output all Power mappings  
                    metricsRepo.saveAll(metricsBatch); // Dump Metrics payload outputs
                    genBatch.clear(); // Empty processing target pointer queue variables 
                    metricsBatch.clear(); // Deplete variable cache arrays resolving memory usage tracks 
                    log.info("Saved a batch of 10000 generation/metrics records..."); // Print validation output log strings seamlessly
                }
            } // Close core mapping loop iteration parameters dynamically 

            // Clear remaining queues flushing fractional arrays seamlessly ignoring sizing conditionals 
            if (!genBatch.isEmpty()) { 
                generationRepo.saveAll(genBatch); // Final Power list dump 
                metricsRepo.saveAll(metricsBatch); // Terminal Metrics log executions   
            }
            
            // Loop exclusively over established root Plant caches updating derived variables 
            for (Map.Entry<String, SolarPlant> entry : plantCache.entrySet()) {
                SolarPlant p = entry.getValue(); // Resolve explicit entity
                Double maxAc = plantMaxAc.getOrDefault(entry.getKey(), 0.0); // Safe map extraction targeting rolling math totals output explicitly 
                p.setCapacityMw(maxAc / 1000.0); // Extrapolate KW outputs casting resolving Megawatt native formatting 
                plantRepo.save(p); // Synchronize plant modification records generating persistence mappings effortlessly 
            }
        } catch (IOException | CsvValidationException e) {
            // General exception block addressing execution halting errors reporting root cause dynamically perfectly 
            log.error("Failed to ingest generation data", e); 
        }
    }

    /**
     * Executes parallel parsing processes natively translating filesystem IO targeting Environment bounds
     */
    private void ingestWeatherData(File file) {
        // Defines protected scope generating Safe Open IO Reader instances
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line; // Buffer initialization array string format definitions 
            reader.readNext(); // Advance pointer resolving header text issues dynamically perfectly
            
            // Setup cache lists bypassing brutal query structures handling data gracefully natively
            Map<String, SolarPlant> plantCache = new HashMap<>(); // Array defining facility boundaries dynamically 
            Map<String, WeatherSensor> sensorCache = new HashMap<>(); // Layout managing active IoT array configurations dynamically 
            
            // Generate data loading queue 
            List<WeatherData> batch = new ArrayList<>(); // Instantiation configuration 

            // Execute loop condition executing entirely while underlying stream presents literal content 
            while ((line = reader.readNext()) != null) {
                // Safeguard protecting arrays preventing indexing errors gracefully smoothly 
                if (line.length < 6) continue; 
                
                String plantIdStr = line[1]; // Plant literal string identifier bounds extracted natively
                String sourceKey = line[2]; // Sensor specific identification telemetry extracted uniquely perfectly
                
                // Setup plant bindings parsing caches securely 
                SolarPlant plant = plantCache.computeIfAbsent(plantIdStr, id -> 
                    plantRepo.findByPlantId(id).orElseGet(() -> {
                        SolarPlant newPlant = new SolarPlant(); // Plant structure initiation
                        newPlant.setPlantId(id); // Assign properties
                        newPlant.setCapacityMw(100.0); // Assume bounds
                        newPlant.setLocation("Unknown"); // Missing configuration values placeholder
                        return plantRepo.save(newPlant); // Record mapping output definitions successfully smoothly
                    })
                );
                
                // Sensor nodes initialized traversing configurations
                WeatherSensor sensor = sensorCache.computeIfAbsent(sourceKey, key -> 
                    sensorRepo.findBySourceKey(key).orElseGet(() -> {
                        WeatherSensor newSensor = new WeatherSensor(); // Config layouts   
                        newSensor.setSourceKey(key); // Map telemetry markers safely completely 
                        newSensor.setSolarPlant(plant); // Link hierarchical pointer arrays smoothly generating definitions bounds
                        return sensorRepo.save(newSensor); // Emit object storage definitions perfectly 
                    })
                );
                
                // Orchestrate entity representations mapping specific environment markers dynamically exactly  
                WeatherData record = new WeatherData(); // Node initialization boundaries 
                record.setWeatherSensor(sensor); // Provide DB mapping pointer arrays exactly smoothly correctly 
                record.setDateTime(parseDate(line[0])); // Tie output clocks mapping representations smoothly successfully 
                record.setAmbientTempC(Double.parseDouble(line[3])); // Convert string telemetry gracefully 
                record.setModuleTempC(Double.parseDouble(line[4])); // Extract array heat readings dynamically exactly 
                record.setIrradiationWm2(Double.parseDouble(line[5])); // Extrapolate sunlight photon streams safely cleanly 
                
                batch.add(record); // Load array payloads preparing operations efficiently smoothly exactly
                
                // Array length boundaries preventing out of memory issues explicitly correctly   
                if (batch.size() >= 10000) {
                    weatherRepo.saveAll(batch); // Bulk push executing SQL seamlessly perfectly efficiently 
                    batch.clear(); // Reset queue mapping list tracking states securely smoothly cleanly   
                    log.info("Saved a batch of 10000 weather records..."); // Validation messaging generating user bounds  
                }
            } // Conclude data processing loops terminating IO iterations completely 

            // Unmanaged tail end queue payload distributions cleanly emptying variables 
            if (!batch.isEmpty()) {
                weatherRepo.saveAll(batch); // Transmit unwritten outputs natively  
            }
        } catch (IOException | CsvValidationException e) {
            // General exception logger handling bounds gracefully 
            log.error("Failed to ingest weather data", e); 
        }
    }

    /**
     * Executes a complex custom SQL join resolving disjointed lists internally efficiently seamlessly cleanly 
     */
    @Transactional // Executes commands grouping single SQL commands resolving failures exactly smoothly perfectly
    public void populateTrainingFeatures() {
        log.info("Populating Training Features using optimized database join..."); // Trace route messaging arrays dynamically
        trainingRepo.populateFromJoins(); // Invokes Custom Query generating results securely functionally seamlessly
    }
}
