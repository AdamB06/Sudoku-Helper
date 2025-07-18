package com.abezard.sudokuHelper.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SudokuController implements Initializable {

    @FXML
    public Button newEasyButton;
    @FXML
    public Button newHardButton;
    @FXML
    private GridPane sudokuGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sudokuGrid.getColumnConstraints().clear();
        sudokuGrid.getRowConstraints().clear();
        sudokuGrid.getChildren().clear();
        sudokuGrid.getStyleClass().add("sudoku-grid");

        for (int i = 0; i < 9; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 9);
            col.setHgrow(Priority.ALWAYS);
            sudokuGrid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 9);
            row.setVgrow(Priority.ALWAYS);
            sudokuGrid.getRowConstraints().add(row);
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = new TextField();
                tf.getStyleClass().add("text-field");
                tf.getStyleClass().add("no-border");
                tf.setAlignment(Pos.CENTER);
                tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                tf.setPrefSize(50, 50);

                if(row == 2 || row == 5) tf.getStyleClass().add("top-row");
                if(row == 3 || row == 6) tf.getStyleClass().add("bottom-row");

                if(col == 2 || col == 5) tf.getStyleClass().add("left-col");
                if(col == 3 || col == 6) tf.getStyleClass().add("right-col");

                // Allow only 1-9 digits
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("[1-9]?")) {
                        tf.setText(oldVal);
                    }
                });

                sudokuGrid.add(tf, col, row);
            }
        }
    }
}
