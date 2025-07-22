package com.abezard.sudokuHelper.client;

import com.abezard.sudokuHelper.SudokuHelperApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SudokuHelperClient extends Application {

    private ConfigurableApplicationContext springContext;

    /**
     * Initializes the JavaFX application and starts the Spring Boot context.
     * This method is called before the JavaFX application starts.
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
        // Start Spring Boot context before UI
        springContext = SpringApplication.run(SudokuHelperApplication.class);
    }

    /**
     * Starts the JavaFX application.
     * @param stage the primary stage of the app.
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageTemplate.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/sudoku.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Sudoku Helper");
        stage.show();
    }

    /**
     * Stops the JavaFX application and closes the Spring context.
     * @throws Exception if an error occurs during shutdown.
     */
    @Override
    public void stop() throws Exception {
        // Shutdown Spring context when JavaFX closes
        if (springContext != null) {
            springContext.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}