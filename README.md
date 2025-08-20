# Sudoku Helper

A Sudoku helper tool that:

- Generates puzzles of varying difficulty
- Solves them step-by-step using human-style logic
- Provides interactive hints
- Features a modular solving and hint engine
- Includes a JavaFX GUI for an intuitive desktop experience

## Features

- Modular solving and hint engine 
  - Built in a way that allows for easy extension of hint strategies, so that the hint engine can solve more complex puzzles. (Currently able to solve roughly 75% of the puzzles generated from difficult level)
- The hint engine provides step-by-step hints with brief explanations to justify each step
- JavaFX GUI for playing, solving, and learning Sudoku
- Difficulty levels: generate either easy or difficult sudoku puzzles

## Setup

### Running the Application

This project uses JavaFX for the client UI.

### Prerequisites

- JDK 17 or 21 installed (JavaFX requires JDK 11+ but not newer than 21 for best compatibility)
- JavaFX SDK 23.0.1 downloaded from [Gluon](https://gluonhq.com/products/javafx/)

### Running in IntelliJ IDEA

1. Download and unzip the JavaFX SDK.
2. Open **Run > Edit Configurations...**.
3. Select the run configuration for `SudokuHelperClient`.
4. In **VM options**, add the following (replace with your JavaFX SDK path):
--module-path /path/to/javafx-sdk-23.0.1/lib --add-modules javafx.controls,javafx.fxml

### Run Locally

To launch the JavaFX client, use your IDE or run the appropriate main class with the JavaFX module path. Or: