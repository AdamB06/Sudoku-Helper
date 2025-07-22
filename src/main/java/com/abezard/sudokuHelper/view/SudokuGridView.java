package com.abezard.sudokuHelper.view;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.Optional;

public class SudokuGridView extends GridPane {

    private final TextField[][] cells = new TextField[9][9];

    /**
     * Constructor for SudokuGridView.
     * Initializes the grid layout and sets up the text fields for Sudoku cells.
     */
    public SudokuGridView() {
        buildGrid();
    }

    /**
     * Rebuilds the grid layout and initializes the text fields.
     */
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

    /**
     * Updates the grid view from the provided SudokuBoard model.
     * Clears existing text fields and populates them with values from the board.
     * @param board The SudokuBoard model to update the view from.
     */
    public void updateFromModel(SudokuBoard board) {
        for (Node node : this.getChildren()) {
            if (node instanceof TextField tf) {
                Integer row = GridPane.getRowIndex(tf);
                Integer col = GridPane.getColumnIndex(tf);
                row = row == null ? 0 : row;
                col = col == null ? 0 : col;
                int value = board.getCell(row, col);
                if (value == 0) {
                    tf.setText("");
                    tf.setDisable(false);
                } else {
                    tf.setText(String.valueOf(value));
                    tf.setDisable(true);
                }
                tf.getStyleClass().remove("hint-cell");
            }
        }
    }

    /**
     * Clears the grid and disables all cells.
     */
    public void clearAndDisableGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                cell.setText("");
                cell.setDisable(true);
                cell.setStyle("-fx-opacity: 0.6; -fx-background-color: #e0e0e0;");
            }
        }
    }

    /**
     * Enables all cells in the grid and resets their styles.
     * This method is typically used to allow user input after a puzzle has been cleared or reset.
     */
    public void enableGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                cell.setDisable(false);
                cell.setStyle(""); // reset style to default
            }
        }
    }

    /**
     * Retrieves the current state of the Sudoku board from the grid view.
     * @return A SudokuBoard object representing the current state of the grid.
     */
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

    /**
     * Checks the solution of the Sudoku board against the provided model.
     * Highlights incorrect cells and disables correct ones.
     * @param sudokuBoard The SudokuBoard model to check against.
     */
    public void checkSolution(SudokuBoard sudokuBoard) {
        boolean allCorrect = true;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                int value = sudokuBoard.getCell(row, col);
                if (value == -1) {
                    cell.setStyle("-fx-background-color: #f8d7da;"); // Incorrect value
                    allCorrect = false;
                } else {
                    cell.setDisable(true); // Disable cell if correct
                    cell.setStyle("-fx-background-color: #d4edda;"); // Correct value
                }
            }
        }

        if (allCorrect) {
            javafx.scene.control.Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku");
            alert.setHeaderText(null);
            alert.setContentText("Congratulations! Your solution is correct!");
            alert.showAndWait();
        }
    }

    /**
     * Reveals the solution of the Sudoku puzzle by updating the grid view.
     * @param solution The SudokuBoard containing the solution to reveal.
     */
    public void revealSolution(SudokuBoard solution) {
        if(solution == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sudoku");
            alert.setHeaderText(null);
            alert.setContentText("No solution available to reveal.");
            alert.showAndWait();
            return;
        }
        updateFromModel(solution);
    }

    /**
     * Displays a hint for the Sudoku puzzle.
     * Highlights the cell and shows an alert with the hint details.
     * @param hint The Hint object containing the hint information.
     */
    public void showHint(Hint hint) {
        if (hint == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText(null);
            alert.setContentText("No hints available at this time.");
            alert.showAndWait();
            return;
        }

        if (hint.type() == Hint.HintType.ALREADY_SOLVED) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText(null);
            alert.setContentText("The Sudoku puzzle is already solved!");
            alert.showAndWait();
            return;
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField cell = cells[row][col];
                cell.getStyleClass().remove("hint-cell");
            }
        }

        TextField cell = cells[hint.row()][hint.col()];
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sudoku Hint");
        alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
        alert.setContentText(
                "Location: Row " + (hint.row() + 1) + ", Column " + (hint.col() + 1) +
                        "\n\nExplanation: " + hint.explanation() +
                        "\n\nWould you like to reveal its number?"
        );
        Optional<ButtonType> result = alert.showAndWait();
        cell.getStyleClass().add("hint-cell");

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            cell.setText(String.valueOf(hint.value()));
        }
    }

}