package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PointingPairHint implements HintStrategy {

    /**
     * Finds pointing pairs in the Sudoku board.
     * Pointing pairs are candidates that can be eliminated from other cells in the same unit (row, column, or box).
     *
     * @param board the current state of the Sudoku board
     * @param candidates the candidates for each cell in the Sudoku board
     * @param solution the solution to the Sudoku board
     * @param candidatesGiven indicates if candidates have been provided
     * @return a CandidatesHint indicating a pointing pair found in the Sudoku board, or null if no pointing pairs exist
     */
    @Override
    public CandidatesHint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        // Iterate over each 3x3 box
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                int startRow = boxRow * 3;
                int startCol = boxCol * 3;

                // Check each candidate digit 1-9
                for (int digit = 1; digit <= 9; digit++) {
                    List<int[]> locations = new ArrayList<>();

                    // Collect candidate positions inside the box
                    for (int r = startRow; r < startRow + 3; r++) {
                        for (int c = startCol; c < startCol + 3; c++) {
                            if (candidates[r][c].contains(digit)) {
                                locations.add(new int[]{r, c});
                            }
                        }
                    }

                    if (locations.size() < 2) continue; // Need at least 2 candidates

                    // Check same row
                    boolean sameRow = locations.stream().allMatch(pos -> pos[0] == locations.get(0)[0]);
                    if (sameRow) {
                        int row = locations.get(0)[0];
                        for (int c = 0; c < 9; c++) {
                            // Skip cells in this box
                            if (c >= startCol && c < startCol + 3) continue;
                            if (candidates[row][c].contains(digit)) {
                                int[][] cells = locations.size() == 2 ?
                                        new int[][]{new int[]{row, locations.get(0)[1]},
                                                new int[]{row, locations.get(1)[1]}} :
                                        new int[][]{new int[]{row, locations.get(0)[1]},
                                                new int[]{row, locations.get(1)[1]}, new int[]{row, locations.get(2)[1]}};
                                return new CandidatesHint(
                                        Hint.HintType.POINTING_PAIR,
                                        cells,
                                        new int[]{digit}, null,
                                        "row",
                                        "Pointing pair: digit " + digit + " in row " + (row+1) +
                                                " confined to box (row: " + (3*boxRow+1) + ", col: " + (3*boxCol+1)
                                                + "). This means this digit can only appear in this row of this box, so we can eliminate it from other cells outside this box in this row."
                                );
                            }
                        }
                    }

                    // Check same column
                    boolean sameCol = locations.stream().allMatch(pos -> pos[1] == locations.get(0)[1]);
                    if (sameCol) {
                        int col = locations.get(0)[1];
                        for (int r = 0; r < 9; r++) {
                            // Skip cells in this box
                            if (r >= startRow && r < startRow + 3) continue;
                            if (candidates[r][col].contains(digit)) {
                                int[][] cells = locations.size() == 2 ?
                                        new int[][]{new int[]{locations.get(0)[0], col},
                                                new int[]{locations.get(1)[0], col}} :
                                        new int[][]{new int[]{locations.get(0)[0], col},
                                                new int[]{locations.get(1)[0], col}, new int[]{locations.get(2)[0], col}};
                                return new CandidatesHint(
                                        Hint.HintType.POINTING_PAIR,
                                        cells, new int[]{digit}, null,
                                        "column",
                                        "Pointing pair: digit " + digit + " in column " + (col+1) +
                                                " confined to box (row: " + (3*boxRow+1) + ", col: " + (3*boxCol+1)
                                                + "). This means this digit can only appear in this column of this box, so we can eliminate it from other cells outside this box in this column."
                                );
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
