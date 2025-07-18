package com.abezard.sudokuHelper.view;

import com.abezard.sudokuHelper.model.SudokuBoard;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class SudokuGridView extends GridPane {

    private final TextField[][] cells = new TextField[9][9];

    public SudokuGridView() {
        buildGrid();
    }

    private void buildGrid() {
        getColumnConstraints().clear();
        getRowConstraints().clear();
        getChildren().clear();
        getStyleClass().add("sudoku-grid");

        for (int i = 0; i < 9; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 9);
            col.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 9);
            row.setVgrow(Priority.ALWAYS);
            getRowConstraints().add(row);
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = new TextField();
                tf.getStyleClass().add("text-field");
                tf.getStyleClass().add("no-border");
                tf.setAlignment(Pos.CENTER);
                tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                tf.setPrefSize(50, 50);

                if (row == 2 || row == 5) tf.getStyleClass().add("top-row");
                if (row == 3 || row == 6) tf.getStyleClass().add("bottom-row");

                if (col == 2 || col == 5) tf.getStyleClass().add("left-col");
                if (col == 3 || col == 6) tf.getStyleClass().add("right-col");

                // Allow only 1-9 digits
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("[1-9]?")) {
                        tf.setText(oldVal);
                    }
                });

                add(tf, col, row);
                cells[row][col] = tf;
            }
        }
    }

    public void updateFromModel(SudokuBoard board) {
        int[][] values = board.getBoard();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int val = values[row][col];
                cells[row][col].setText(val == 0 ? "" : String.valueOf(val));
                // Optionally disable editing for preset clues (if you add that logic)
                // cells[row][col].setDisable(...);
            }
        }
    }

    public void clearAndDisableGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                cell.setText("");
                cell.setDisable(true);  // disable input and interaction
                // Optionally, if you want it grayed out visually:
                cell.setStyle("-fx-opacity: 0.6; -fx-background-color: #e0e0e0;"); // more visible feedback for disabled
            }
        }
    }

    public void enableGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                cell.setDisable(false);
                cell.setStyle(""); // reset style to default
            }
        }
    }

    public SudokuBoard getCurrentBoard() {
        SudokuBoard board = new SudokuBoard();
        int[][] values = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText();
                values[row][col] = (text == null || text.isEmpty()) ? 0 : Integer.parseInt(text);
            }
        }
        board.setBoard(values);
        return board;
    }
}