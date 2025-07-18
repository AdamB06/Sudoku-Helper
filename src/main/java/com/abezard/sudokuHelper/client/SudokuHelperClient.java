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

    @Override
    public void init() throws Exception {
        // Start Spring Boot context before UI
        springContext = SpringApplication.run(SudokuHelperApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPageTemplate.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/sudoku.css").toExternalForm());
        System.out.println(getClass().getResource("/css/sudoku.css"));
        stage.setScene(scene);
        stage.setTitle("Sudoku Helper");
        stage.show();
    }

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