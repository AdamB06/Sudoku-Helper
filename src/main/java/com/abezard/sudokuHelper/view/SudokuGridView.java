package com.abezard.sudokuHelper.view;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
                cell.setCandidates(new HashSet<>());
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
            alert.setContentText("More advanced solving techniques are required to solve this puzzle.");
            alert.showAndWait();
            return;
        }

        if(hint.type() == Hint.HintType.INCORRECT_INPUT){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText(null);
            alert.setContentText("You have an incorrect input in the grid. The highlighted cell is incorrect.");
            alert.showAndWait();
            SudokuCell cell = cells[hint.row()][hint.col()];
            cell.getStyleClass().add("hint-cell");
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

        if(hint.type() == Hint.HintType.HIDDEN_SINGLE || hint.type() == Hint.HintType.NAKED_SINGLE) {
            SudokuCell cell = cells[hint.row()][hint.col()];
            cell.addHintStyle();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            alert.setContentText(
                    "Location: Row " + (hint.row() + 1) + ", Column " + (hint.col() + 1) +
                            "\n\nExplanation: " + hint.explanation()
            );
            alert.showAndWait();
            cell.setValue(hint.value(), false);
            cell.setCandidates(new HashSet<>());
            return;
        }

        if(hint.type() == Hint.HintType.LAST_CANDIDATE) {
            SudokuCell cell = cells[hint.row()][hint.col()];
            cell.addHintStyle();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            alert.setContentText(
                    "Location: Row " + (hint.row() + 1) + ", Column " + (hint.col() + 1) +
                            "\n\nExplanation: " + hint.explanation()
            );
            alert.showAndWait();
            cell.setValue(hint.value(), false);
            cell.setCandidates(new HashSet<>());
            return;
        }

        if(hint.type() == Hint.HintType.NAKED_PAIR) {
            CandidatesHint h = (CandidatesHint) hint;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            if(h.getScope().equals("row")) {
                alert.setContentText(
                        "Row " + (h.getCellCoordinates()[0][0] + 1) + " has a naked pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightRow(h.getCellCoordinates()[0][0]);
                removeCandidatesFromRow(h.getCellCoordinates()[0][0], h.getCandidates());
            } else if(h.getScope().equals("column")) {
                alert.setContentText(
                        "Column " + (h.getCellCoordinates()[0][1] + 1) + " has a naked pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightColumn(h.getCellCoordinates()[0][1]);
                removeCandidatesFromColumn(h.getCellCoordinates()[0][1], h.getCandidates());
            } else {
                alert.setContentText(
                        "Box at Row " + (h.getCellCoordinates()[0][0] / 3 * 3 + 1) +
                                ", Column " + (h.getCellCoordinates()[0][1] / 3 * 3 + 1) +
                                " has a naked pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightBox(h.getCellCoordinates()[0][0], h.getCellCoordinates()[0][1]);
                removeCandidatesFromBox(h.getCellCoordinates()[0][0], h.getCellCoordinates()[0][1], h.getCandidates());
            }
            for(int[] cellCoords : h.getCellCoordinates()) {
                int row = cellCoords[0];
                int col = cellCoords[1];
                cells[row][col].setCandidates(new HashSet<>(Arrays.asList(h.getCandidates()[0], h.getCandidates()[1])));
            }
            alert.showAndWait();
        }

        if(hint.type() == Hint.HintType.HIDDEN_PAIR) {
            CandidatesHint h = (CandidatesHint) hint;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            if(h.getScope().equals("row")) {
                alert.setContentText(
                        "Row " + (h.getCellCoordinates()[0][0] + 1) + " has a hidden pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightRow(h.getCellCoordinates()[0][0]);
            } else if(h.getScope().equals("column")) {
                alert.setContentText(
                        "Column " + (h.getCellCoordinates()[0][1] + 1) + " has a hidden pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightColumn(h.getCellCoordinates()[0][1]);
            } else {
                alert.setContentText(
                        "Box at Row " + (h.getCellCoordinates()[0][0] / 3 * 3 + 1) +
                                ", Column " + (h.getCellCoordinates()[0][1] / 3 * 3 + 1) +
                                " has a hidden pair of candidates: " +
                                h.getCandidates()[0] + " and " + h.getCandidates()[1] +
                                "\n\nExplanation: " + hint.explanation()
                );
                highlightBox(h.getCellCoordinates()[0][0], h.getCellCoordinates()[0][1]);
            }
            for(int[] cellCoords : h.getCellCoordinates()) {
                int row = cellCoords[0];
                int col = cellCoords[1];
                cells[row][col].setCandidates(new HashSet<>(Arrays.asList(h.getCandidates()[0], h.getCandidates()[1])));
            }
            alert.showAndWait();
        }

        if(hint.type() == Hint.HintType.POINTING_PAIR) {
            CandidatesHint h = (CandidatesHint) hint;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            if(h.getScope().equals("row")) {
                alert.setContentText(
                        "Row " + (h.getCellCoordinates()[0][0] + 1) + " has a pointing pair/triple with candidate: " +
                                h.getCandidates()[0]+ "\n\nExplanation: " + hint.explanation());
                highlightRow(h.getCellCoordinates()[0][0]);
                removeCandidatesFromRow(h.getCellCoordinates()[0][0], h.getCandidates());
            } else if(h.getScope().equals("column")) {
                alert.setContentText(
                        "Column " + (h.getCellCoordinates()[0][1] + 1) + " has a pointing pair/triple with candidate: " +
                                h.getCandidates()[0] + "\n\nExplanation: " + hint.explanation());
                highlightColumn(h.getCellCoordinates()[0][1]);
                removeCandidatesFromColumn(h.getCellCoordinates()[0][1], h.getCandidates());
            }
            // Add the candidates back to the cells in the box they were confined to
            for(int[] cellCoords : h.getCellCoordinates()) {
                int row = cellCoords[0];
                int col = cellCoords[1];
                HashSet<Integer> candidatesSet = new HashSet<>(cells[row][col].getCandidates());
                candidatesSet.add(h.getCandidates()[0]);
                cells[row][col].setCandidates(candidatesSet);
            }
            alert.showAndWait();
        }

        if(hint.type() == Hint.HintType.ALL_CANDIDATES) {
            CandidatesHint h = (CandidatesHint) hint;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Sudoku Hint");
            alert.setHeaderText("HINT: " + hint.type().toString().replaceAll("_", " "));
            alert.setContentText(h.explanation());
            Set<Integer>[][] allCandidates = h.getMultipleCandidates();
            for (int i = 0; i < allCandidates.length; i++) {
                for (int j = 0; j < allCandidates[i].length; j++) {
                    if (allCandidates[i][j] != null) {
                        cells[i][j].setCandidates(new HashSet<>(allCandidates[i][j]));
                    }
                }
            }
            alert.showAndWait();
        }
    }

    private void highlightBox(int row, int col) {
        // Highlight the box containing the cell at (row, col)
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if(cells[r][c].getValueField().isDisabled()) continue; // Skip disabled cells
                cells[r][c].getStyleClass().add("hint-cell");
            }
        }
    }

    private void highlightColumn(int col) {
        // Highlight the column containing the cell at (row, col)
        for (int r = 0; r < 9; r++) {
            if(cells[r][col].getValueField().isDisabled()) continue; // Skip disabled cells
            cells[r][col].getStyleClass().add("hint-cell");
        }
    }

    private void highlightRow(int row) {
        // Highlight the row containing the cell at (row, col)
        for (int c = 0; c < 9; c++) {
            cells[row][c].getStyleClass().add("hint-cell");
        }
    }

    private void removeCandidatesFromRow(int row, int[] candidates) {
        for(int col = 0; col < 9; col++) {
            SudokuCell cell = cells[row][col];
            if(cell.getValueField().isDisabled() || cell.getValue() != 0) continue; // Skip disabled cells
            Set<Integer> currentCandidates = new HashSet<>(cell.getCandidates());
            for(int candidate : candidates) {
                currentCandidates.remove(candidate);
            }
            cell.setCandidates(currentCandidates);
        }
    }

    private void removeCandidatesFromColumn(int col, int[] candidates) {
        for(int row = 0; row < 9; row++) {
            SudokuCell cell = cells[row][col];
            if(cell.getValueField().isDisabled() || cell.getValue() != 0) continue; // Skip disabled cells
            Set<Integer> currentCandidates = new HashSet<>(cell.getCandidates());
            for(int candidate : candidates) {
                currentCandidates.remove(candidate);
            }
            cell.setCandidates(currentCandidates);
        }
    }

    private void removeCandidatesFromBox(int row, int col, int[] candidates) {
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                SudokuCell cell = cells[r][c];
                if (cell.getValueField().isDisabled() || cell.getValue() != 0) continue; // Skip disabled cells
                Set<Integer> currentCandidates = new HashSet<>(cell.getCandidates());
                for(int candidate : candidates) {
                    currentCandidates.remove(candidate);
                }
                cell.setCandidates(currentCandidates);
            }
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
                cell.getStyleClass().remove("incorrect-cell");
                cell.getStyleClass().remove("correct-cell");
                cell.getValueField().getStyleClass().remove("correct-cell");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Set<Integer>[][] getAllCandidates(){
        Set<Integer>[][] candidates = new HashSet[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                candidates[r][c] = cells[r][c].getCandidates();
            }
        }
        return candidates;
    }
}
