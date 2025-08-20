package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.*;

public class HiddenPairHint implements  HintStrategy {

    /**
     * Constructor for HiddenPairHint.
     * @param board the current state of the Sudoku board
     * @param candidates the candidates for each cell in the Sudoku board
     * @param solution the solution to the Sudoku board
     * @param candidatesGiven indicates if candidates have been provided
     * @return a CandidatesHint indicating a hidden pair found in the Sudoku board, or null if no hidden pairs exist
     */
    @Override
    public CandidatesHint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        // Check rows
        for (int row = 0; row < 9; row++) {
            int[][] unit = new int[9][2];
            for (int col = 0; col < 9; col++) {
                unit[col] = new int[]{row, col};
            }
            CandidatesHint hint = findHiddenPairInUnit(candidates, unit, "row", "row " + (row + 1));
            if (hint != null) return hint;
        }

        // Check columns
        for (int col = 0; col < 9; col++) {
            int[][] unit = new int[9][2];
            for (int row = 0; row < 9; row++) {
                unit[row] = new int[]{row, col};
            }
            CandidatesHint hint = findHiddenPairInUnit(candidates, unit, "column", "column " + (col + 1));
            if (hint != null) return hint;
        }

        // Check boxes
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                int[][] unit = new int[9][2];
                int idx = 0;
                for (int r = boxRow * 3; r < (boxRow + 1) * 3; r++) {
                    for (int c = boxCol * 3; c < (boxCol + 1) * 3; c++) {
                        unit[idx++] = new int[]{r, c};
                    }
                }
                CandidatesHint hint = findHiddenPairInUnit(candidates, unit,"box",
                        "box (row: " + (3*boxRow + 1) + ", col: " + (3*boxCol + 1) + ")");
                if (hint != null) return hint;
            }
        }

        return null;
    }

    /**
     * Finds hidden pairs in a specific unit (row, column, or box) of the Sudoku board.
     * @param candidates the candidates for each cell in the Sudoku board
     * @param unitCells the cells in the unit to check for hidden pairs
     * @param scope the type of unit (row, column, or box)
     * @param unitLabel a label for the unit (e.g., "row 1", "column 2", etc.)
     * @return a CandidatesHint indicating a hidden pair found in the unit, or null if no hidden pairs exist
     */
    private CandidatesHint findHiddenPairInUnit(Set<Integer>[][] candidates,
                                                int[][] unitCells, String scope,
                                                String unitLabel) {
        Map<Integer, List<int[]>> candidateLocations = new HashMap<>();

        // Collect candidate locations in this unit
        for (int[] cell : unitCells) {
            int row = cell[0], col = cell[1];
            if (candidates[row][col].isEmpty()) continue;
            for (Integer cand : candidates[row][col]) {
                candidateLocations.computeIfAbsent(cand, k -> new ArrayList<>())
                        .add(new int[]{row, col});
            }
        }

        // Look for two candidates with identical 2-cell locations
        for (Map.Entry<Integer, List<int[]>> e1 : candidateLocations.entrySet()) {
            if (e1.getValue().size() == 2) {
                for (Map.Entry<Integer, List<int[]>> e2 : candidateLocations.entrySet()) {
                    if (e2.getKey() <= e1.getKey()) continue; // avoid duplicate pairs
                    if (e2.getValue().size() == 2 &&
                            sameLocations(e1.getValue(), e2.getValue())) {

                        int[] pair = new int[]{e1.getKey(), e2.getKey()};
                        int[][] cells = e1.getValue().toArray(new int[0][]);

                        // Filter trivial: check if these cells have extra candidates
                        boolean eliminates = false;
                        for (int[] cell : cells) {
                            if (candidates[cell[0]][cell[1]].size() > 2) {
                                eliminates = true;
                                break;
                            }
                        }
                        if (!eliminates) continue;

                        return new CandidatesHint(Hint.HintType.HIDDEN_PAIR,
                                cells, pair, null, scope,
                                "Hidden pair " + Arrays.toString(pair) +
                                        " found in " + unitLabel +". This means these two cells are the only ones that can contain these two candidates, and we can eliminate all other candidates from these two cells.");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if two lists of cell locations contain the same coordinates.
     * @param a the first list of cell locations
     * @param b the second list of cell locations
     * @return true if both lists contain the same cell locations, false otherwise
     */
    private boolean sameLocations(List<int[]> a, List<int[]> b) {
        return (a.size() == b.size()) &&
                a.stream().allMatch(cellA ->
                        b.stream().anyMatch(cellB ->
                                cellA[0] == cellB[0] && cellA[1] == cellB[1]));
    }
}
