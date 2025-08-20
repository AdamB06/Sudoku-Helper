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
            showAlert(Alert.AlertType.INFORMATION, "Sudoku Hint", null,
                    "More advanced solving techniques are required to solve this puzzle.");
            return;
        }
        switch (hint.type()) {
            case INCORRECT_INPUT -> {
                showAlert(Alert.AlertType.ERROR, "Incorrect Input", null,
                        "You have an incorrect input in the grid. The highlighted cell is incorrect.");
                cells[hint.row()][hint.col()].getStyleClass().add("hint-cell");
            }
            case ALREADY_SOLVED -> showAlert(Alert.AlertType.INFORMATION, "Sudoku Solved", null,
                    "The Sudoku puzzle is already solved!");
            case HIDDEN_SINGLE, NAKED_SINGLE, LAST_CANDIDATE -> {
                clearHintHighlights();
                SudokuCell cell = cells[hint.row()][hint.col()];
                cell.addHintStyle();
                showAlert(
                        hint.type() == Hint.HintType.HIDDEN_SINGLE || hint.type() == Hint.HintType.NAKED_SINGLE
                                ? Alert.AlertType.CONFIRMATION : Alert.AlertType.INFORMATION,
                        "Sudoku Hint",
                        "HINT: " + hint.type().toString().replaceAll("_", " "),
                        "Location: Row " + (hint.row() + 1) + ", Column " + (hint.col() + 1) +
                                "\n\nExplanation: " + hint.explanation()
                );
                cell.setValue(hint.value(), false);
                cell.setCandidates(new HashSet<>());
                // removes candidates that will be eliminated by this hint (if applicable)
                removeCandidatesFromBox(hint.row(), hint.col(), new int[]{hint.value()});
                removeCandidatesFromRow(hint.row(), new int[]{hint.value()});
                removeCandidatesFromColumn(hint.col(), new int[]{hint.value()});
            }
            case NAKED_PAIR, HIDDEN_PAIR, POINTING_PAIR -> {
                clearHintHighlights();
                CandidatesHint h = (CandidatesHint) hint;
                String scope = h.getScope();
                String pairType = hint.type().toString().replaceAll("_", " ");
                String candidatesStr = h.getCandidates().length == 2
                        ? h.getCandidates()[0] + " and " + h.getCandidates()[1]
                        : Arrays.toString(h.getCandidates());
                String explanation = "\n\nExplanation: " + hint.explanation();
                String header = "HINT: " + pairType;
                String content = pairContentTextHelper(h, scope, candidatesStr, pairType, explanation);
                for (int[] cellCoords : h.getCellCoordinates()) {
                    int row = cellCoords[0], col = cellCoords[1];
                    if (hint.type() == Hint.HintType.POINTING_PAIR) {
                        HashSet<Integer> candidatesSet = new HashSet<>(cells[row][col].getCandidates());
                        candidatesSet.add(h.getCandidates()[0]);
                        cells[row][col].setCandidates(candidatesSet);
                    } else {
                        cells[row][col].setCandidates(new HashSet<>(Arrays.asList(h.getCandidates()[0], h.getCandidates()[1])));
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Sudoku Hint", header, content);
            }
            case ALL_CANDIDATES -> {
                CandidatesHint h = (CandidatesHint) hint;
                showAlert(Alert.AlertType.INFORMATION, "Sudoku Hint",
                        "HINT: " + hint.type().toString().replaceAll("_", " "), h.explanation());
                Set<Integer>[][] allCandidates = h.getMultipleCandidates();
                for (int i = 0; i < allCandidates.length; i++) {
                    for (int j = 0; j < allCandidates[i].length; j++) {
                        if (allCandidates[i][j] != null) {
                            cells[i][j].setCandidates(new HashSet<>(allCandidates[i][j]));
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to generate the content text for pair hints.
     * @param h The CandidatesHint object containing the hint details.
     * @param scope The scope of the pair (row, column, or box).
     * @param candidatesStr The string representation of the candidates involved in the pair.
     * @param pairType The type of pair (e.g., "Naked Pair", "Hidden Pair", "Pointing Pair").
     * @param explanation The explanation text for the hint.
     * @return The formatted content text for the hint.
     */
    private String pairContentTextHelper(CandidatesHint h, String scope,  String candidatesStr, String pairType, String explanation) {
        String content;
        if ("row".equals(scope)) {
            content = (h.type() == Hint.HintType.POINTING_PAIR
                    ? "Row " + (h.getCellCoordinates()[0][0] + 1) + " has a pointing pair/triple with candidate: " + h.getCandidates()[0]
                    : "Row " + (h.getCellCoordinates()[0][0] + 1) + " has a " + pairType.toLowerCase() + " of candidates: " + candidatesStr)
                    + explanation;
            highlightRow(h.getCellCoordinates()[0][0]);
            if (h.type() != Hint.HintType.HIDDEN_PAIR)
                removeCandidatesFromRow(h.getCellCoordinates()[0][0], h.getCandidates());
        } else if ("column".equals(scope)) {
            content = (h.type() == Hint.HintType.POINTING_PAIR
                    ? "Column " + (h.getCellCoordinates()[0][1] + 1) + " has a pointing pair/triple with candidate: " + h.getCandidates()[0]
                    : "Column " + (h.getCellCoordinates()[0][1] + 1) + " has a " + pairType.toLowerCase() + " of candidates: " + candidatesStr)
                    + explanation;
            highlightColumn(h.getCellCoordinates()[0][1]);
            if (h.type() != Hint.HintType.HIDDEN_PAIR)
                removeCandidatesFromColumn(h.getCellCoordinates()[0][1], h.getCandidates());
        } else {
            content = "Box at Row " + (h.getCellCoordinates()[0][0] / 3 * 3 + 1) +
                    ", Column " + (h.getCellCoordinates()[0][1] / 3 * 3 + 1) +
                    " has a " + pairType.toLowerCase() + " of candidates: " + candidatesStr + explanation;
            highlightBox(h.getCellCoordinates()[0][0], h.getCellCoordinates()[0][1]);
            if (h.type() != Hint.HintType.HIDDEN_PAIR)
                removeCandidatesFromBox(h.getCellCoordinates()[0][0], h.getCellCoordinates()[0][1], h.getCandidates());
        }
        return content;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Highlights the box containing the cell at (row, col).
     * @param row The row index of the cell inside the box to highlight.
     * @param col The column index of the cell inside the box to highlight.
     */
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

    /**
     * Highlights the specified column in the Sudoku grid.
     * @param col The column index to highlight (0-8).
     */
    private void highlightColumn(int col) {
        // Highlight the column containing the cell at (row, col)
        for (int r = 0; r < 9; r++) {
            if(cells[r][col].getValueField().isDisabled()) continue; // Skip disabled cells
            cells[r][col].getStyleClass().add("hint-cell");
        }
    }

    /**
     * Highlights the specified row in the Sudoku grid.
     * @param row The row index to highlight (0-8).
     */
    private void highlightRow(int row) {
        // Highlight the row containing the cell at (row, col)
        for (int c = 0; c < 9; c++) {
            if(cells[row][c].getValueField().isDisabled()) continue; // Skip disabled cells
            cells[row][c].getStyleClass().add("hint-cell");
        }
    }


    /**
     * Removes the specified candidates from all cells in the given row.
     * @param row The row index from which to remove candidates (0-8).
     * @param candidates The array of candidates to remove from the cells in the specified row.
     */
    private void removeCandidatesFromRow(int row, int[] candidates) {
        for(int col = 0; col < 9; col++) {
            removeCandidatesHelper(candidates, row, col);
        }
    }

    /**
     * Removes the specified candidates from all cells in the given column.
     * @param col The column index from which to remove candidates (0-8).
     * @param candidates The array of candidates to remove from the cells in the specified column.
     */
    private void removeCandidatesFromColumn(int col, int[] candidates) {
        for(int row = 0; row < 9; row++) {
            removeCandidatesHelper(candidates, row, col);
        }
    }

    /**
     * Removes the specified candidates from all cells in the given box.
     * @param row The box row index from which to remove candidates (0-8).
     * @param col The box column index from which to remove candidates (0-8).
     * @param candidates The array of candidates to remove from the cells in the specified box.
     */
    private void removeCandidatesFromBox(int row, int col, int[] candidates) {
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                removeCandidatesHelper(candidates, r, c);
            }
        }
    }

    /**
     * Helper method to remove candidates from a specific cell.
     * @param candidates The array of candidates to remove from the specified cell.
     * @param r the row index of the cell to remove candidates from
     * @param c the column index of the cell to remove candidates from
     */
    private void removeCandidatesHelper(int[] candidates, int r, int c) {
        SudokuCell cell = cells[r][c];
        if (cell.getValueField().isDisabled() || cell.getValue() != 0) return;
        Set<Integer> currentCandidates = new HashSet<>(cell.getCandidates());
        for(int candidate : candidates) {
            currentCandidates.remove(candidate);
        }
        cell.setCandidates(currentCandidates);
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

    /**
     * Retrieves all candidates for each cell in the grid.
     * @return A 2D array of Sets containing all candidates for each cell in the grid.
     */
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
