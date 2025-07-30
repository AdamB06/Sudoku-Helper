package com.abezard.sudokuHelper.view;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;

import java.util.Optional;

public class SudokuGridView extends GridPane {

    private final SudokuCell[][] cells = new SudokuCell[9][9];

    /**
     * Constructor for SudokuGridView.
     * Initializes the grid layout and populates it with SudokuCell instances.
     */
    public SudokuGridView() {
        buildGrid();
    }

    /**
     * Rebuilds the grid layout, clearing any existing cells and constraints.
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

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = new SudokuCell();
                cell.getStyleClass().add("inner-cell");
                if (r == 2 || r == 5) cell.getStyleClass().add("top-row");
                if (r == 3 || r == 6) cell.getStyleClass().add("bottom-row");
                if (c == 2 || c == 5) cell.getStyleClass().add("left-col");
                if (c == 3 || c == 6) cell.getStyleClass().add("right-col");
                add(cell, c, r);
                cells[r][c] = cell;
            }
        }
    }

    /**
     * Updates the grid view from the provided SudokuBoard model.
     * @param board The SudokuBoard model to update the view from.
     */
    public void updateFromModel(SudokuBoard board) {
        clearAllStyles();
        for (Node node : getChildren()) {
            if (node instanceof SudokuCell cell) {
                Integer row = GridPane.getRowIndex(cell);
                Integer col = GridPane.getColumnIndex(cell);
                row = (row == null) ? 0 : row;
                col = (col == null) ? 0 : col;

                int value = board.getCell(row, col);
                cell.setValue(value, value != 0);
                cell.setCandidates(board.getCandidates(row, col));
            }
        }
    }

    /**
     * Clears the grid and disables all cells.
     * Sets all cell values to 0 and adds a "disabled-cell" style class.
     */
    public void clearAndDisableGrid() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = cells[r][c];
                cell.setValue(0, true);
                cell.setCandidates(null);
                cell.getStyleClass().add("disabled-cell");
            }
        }
    }

    /**
     * Enables the grid by removing the "disabled-cell" style class and allowing value input.
     */
    public void enableGrid() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = cells[r][c];
                cell.valueField().setDisable(false);
                cell.getStyleClass().remove("disabled-cell");
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
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                values[r][c] = cells[r][c].getValue();
            }
        }
        board.setBoard(values);
        return board;
    }

    /**
     * Checks the solution of the Sudoku board against the provided resultBoard.
     * Highlights incorrect cells and disables value input for correct cells.
     * Displays an alert if the solution is correct.
     * @param resultBoard The SudokuBoard containing the correct solution.
     */
    public void checkSolution(SudokuBoard resultBoard) {
        boolean allCorrect = true;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                SudokuCell cell = cells[r][c];
                int checkValue = resultBoard.getCell(r, c);
                if (checkValue == -1) {
                    cell.getStyleClass().add("incorrect-cell");
                    cell.valueField().getStyleClass().add("incorrect-cell");
                    allCorrect = false;
                } else {
                    cell.valueField().setDisable(true);
                    cell.getStyleClass().add("correct-cell");
                    cell.getValueField().getStyleClass().add("correct-cell");
                }
            }
        }
        if (allCorrect) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku");
            alert.setHeaderText(null);
            alert.setContentText("Congratulations! Your solution is correct!");
            alert.showAndWait();
        }
    }

    /**
     * Reveals the solution of the Sudoku puzzle by updating the grid view with the provided solution.
     * @param solution The SudokuBoard containing the solution to reveal.
     */
    public void revealSolution(SudokuBoard solution) {
        if (solution == null) {
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
     * Highlights the cell with the hint and shows an alert with the hint details.
     * If the hint is null or indicates that the puzzle is already solved, appropriate messages are shown.
     * @param hint The Hint object containing the details of the hint to display.
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

        clearHintHighlights();

        SudokuCell cell = cells[hint.row()][hint.col()];
        cell.addHintStyle();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sudoku Hint");
        alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
        alert.setContentText(
                "Location: Row " + (hint.row() + 1) + ", Column " + (hint.col() + 1) +
                        "\n\nExplanation: " + hint.explanation() +
                        "\n\nWould you like to reveal its number?"
        );
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            cell.setValue(hint.value(), false);
            cell.setCandidates(null);
        }
    }

    /**
     * Clears the highlight styles from all cells in the grid.
     * This method is used to remove any previous hint highlights before displaying a new hint.
     */
    private void clearHintHighlights() {
        for (Node node : getChildren()) {
            if (node instanceof SudokuCell cell) {
                cell.getStyleClass().remove("hint-cell");
                cell.getValueField().getStyleClass().remove("hint-cell");
            }
        }
    }

    /**
     * Toggles the candidate mode for all cells in the grid.
     * @param enabled true to enable candidate mode, false to disable it.
     */
    public void setCandidateMode(boolean enabled) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setCandidateMode(enabled);
            }
        }
    }

    /**
     * Clears all styles from the Sudoku grid, including hints, incorrect, and correct styles.
     * This method is typically used to reset the grid before starting a new game or after checking a solution.
     */
    public void clearAllStyles() {
        for (Node node : getChildren()) {
            if (node instanceof SudokuCell cell) {
                cell.getStyleClass().remove("hint-cell");
                cell.getValueField().getStyleClass().remove("hint-cell");
                cell.getStyleClass().remove("incorrect-cell");
                cell.getValueField().getStyleClass().remove("incorrect-cell");
                cell.getStyleClass().remove("correct-cell");
                cell.getValueField().getStyleClass().remove("correct-cell");
            }
        }
    }
}
