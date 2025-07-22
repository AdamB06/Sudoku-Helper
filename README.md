# Sudoku Helper

A Sudoku helper tool that:
- Generates puzzles of varying difficulty
- Solves them step-by-step using human-style logic
- Provides interactive hints

## Features
- REST API with Spring Boot
- Modular solving and hint engine
- JavaFX GUI

## Setup
### Running the Application

This project uses JavaFX for the client UI and Spring Boot for the backend server.

### Prerequisites

- JDK 17 or 21 installed (JavaFX requires JDK 11+ but not newer than 21 for best compatibility)
- JavaFX SDK 23.0.1 downloaded from [Gluon](https://gluonhq.com/products/javafx/)

### Running in IntelliJ IDEA

1. Download and unzip the JavaFX SDK.
2. Open **Run > Edit Configurations...**.
3. Select the run configuration for `SudokuHelperClient`.
4. In **VM options**, add the following (replace with your JavaFX SDK path):

## Run Locally
```bash
./mvnw spring-boot:run
