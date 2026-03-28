# Solar Energy Forecast

## 1. Project Overview

The **Solar Energy Forecast** (`solar-energy-forecast`) is a production-ready, highly optimized Spring Boot application designed to provide accurate predictions of solar power generation. By integrating historical generation telemetry with real-time weather forecasts from the Visual Crossing API, the system utilizes a **Weka-based Linear Regression model** to estimate energy output for the next 15 days.

A major feature of this codebase is its **comprehensive inline documentation**. Every single class, HTML template, CSS block, and JS function features meticulous line-by-line comments explaining exactly *what* the code does and *why* it is structured that way, making it an exceptional educational and maintainable reference architecture.

The system features a premium, responsive dashboard (Light/Dark mode) that visualizes current performance metrics, a 48-hour detailed forecast curve, and long-term daily yield projections.

---

## 2. Technologies Used

* **Backend Framework:** Java 25, Spring Boot 4.0.3
* **Web Layer:** Spring Web MVC, Thymeleaf
* **Data Access & Persistence:** Spring Data JPA, Hibernate (MySQL 10-table schema)
* **Machine Learning:** Weka (Linear Regression algorithm)
* **External API:** Visual Crossing Weather API
* **Data Ingestion:** OpenCSV (Kaggle Dataset ETL)
* **Frontend:** HTML5, Vanilla CSS3 (CSS Variables for Theming), Bootstrap 5, Chart.js

---

## 3. Project Structure & Architecture

The project features a highly normalized 10-table SQL schema separating hierarchy (Plants -> Inverters) from Telemetry and ML tracking.

```text
solar-energy-forecast/
│
├── src/main/java/com/energy/analysis/
│   ├── SolarEnergyForecastApplication.java       # Main application entry point
│   │
│   ├── model/                                # 10-Table JPA Entity Architecture
│   │   ├── SolarPlant.java                   # Root facility node
│   │   ├── SolarInverter.java                # Child generation nodes
│   │   ├── WeatherSensor.java                # Child environment nodes
│   │   ├── GenerationData.java               # Historical power telemetry
│   │   ├── WeatherData.java                  # Historical physical conditions
│   │   ├── DailyMetrics.java                 # 24-hr aggregated rollups
│   │   ├── ApiWeatherData.java               # 3rd-party API weather forecasts
│   │   ├── TrainingFeature.java              # Aggregated dataset for ML training
│   │   ├── ForecastModel.java                # ML model metadata and accuracy metrics
│   │   └── ForecastData.java                 # ML predicted generation outputs
│   │
│   ├── repository/                           # Data Access Layer
│   │   ├── (10 matching JPA interfaces including complex Native SQL joins)
│   │
│   ├── service/                              # Business Logic
│   │   ├── DashboardService.java             # UI telemetry aggregation
│   │   ├── PredictionService.java            # Weka ML training and inference engine
│   │   ├── WeatherApiService.java            # External Visual Crossing API integration
│   │   └── CsvDataLoader.java                # On-boot Kaggle CSV parsing and ETL
│   │
│   └── controller/                           # Web & REST API Handlers
│       ├── DashboardController.java          # Main MVC UI controller (Thymeleaf)
│       ├── ForecastingRestController.java    # JSON API predicting power arrays
│       └── SolarDataController.java          # JSON API charting historical metrics
│
├── src/main/resources/
│   ├── application.yml                       # Core server, JPA, and Database configuration
│   ├── data/                                 # Source Kaggle CSV datasets
│   └── templates/                            # Thymeleaf layouts
│       └── dashboard.html                    # Premium UI (HTML/JS/CSS)
│
├── pom.xml                                   # Dependency and build configuration
└── mvnw / mvnw.cmd                           # Maven wrapper executables
```

---

## 4. Key Features

### **4.1 Predictive Analytics (Machine Learning)**
- **15-Day Forecast:** Automated generation of power estimates based on upcoming weather patterns.
- **Weka Engine:** Predicts kW generation using a Linear Regression mathematical model trained on combined database telemetry.
- **Auto-Retraining:** Automatically parses and evaluates its own mathematical schema boundaries.

### **4.2 Premium Responsive UI**
- **Dual-Axis Visualization:** Correlation charts superimposing solar radiation and predicted power output via Chart.js.
- **Dynamic Theming:** Instantaneous client-side swapping between sleek Dark and Light modes.
- **Live Status:** Current AC power output and peak daily performance metrics.

### **4.3 Code Quality & Maintainability**
- **Line-By-Line Comments:** Everything from standard POJO getters to complex SQL join logic and external API routing is thoroughly explained. 
- **Optimized SQL Batching:** High performance ETL parsing capable of mapping tens of thousands of Kaggle CSV rows natively without memory crashes.

---

## 5. Getting Started

### Prerequisites
1. **JDK 25**
2. **MySQL 8.0+**
3. **Maven** (included via wrapper)

### Setup
1. Create the target schema in MySQL: 
   ```sql
   CREATE DATABASE energy_db;
   ```
2. Configure credentials in `src/main/resources/application.yml` (update the `password` field).
3. The application will automatically construct its own 10 tables on first boot and digest the local CSV files in `src/main/resources/data`.
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Dashboard Access
The primary UI interface is available locally at: **http://localhost:8080/dashboard**
