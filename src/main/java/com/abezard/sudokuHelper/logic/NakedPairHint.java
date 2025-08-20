package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NakedPairHint implements HintStrategy {

    /**
     * Finds naked pairs in the Sudoku board candidates.
     *
     * @param board           the current state of the Sudoku board
     * @param candidates      the candidates for each cell in the Sudoku board
     * @param solution        the solution Sudoku board (not used in this strategy)
     * @param candidatesGiven indicates if candidates have been provided
     * @return a CandidatesHint indicating the naked pair found, or null if no naked pairs exist
     */
    @Override
    public CandidatesHint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        CandidatesHint rowHint = findNakedPairInRows(candidates);
        if (rowHint != null) {
            return rowHint;
        }
        CandidatesHint colHint = findNakedPairInColumns(candidates);
        if (colHint != null) {
            return colHint;
        }
        return findNakedPairInBoxes(candidates);
    }

    /**
     * Finds naked pairs in the rows of the Sudoku board candidates.
     * @param candidates the candidates for each cell in the Sudoku board
     * @return a CandidatesHint indicating the naked pair found in rows, or null if no naked pairs exist
     */
    private CandidatesHint findNakedPairInRows(Set<Integer>[][] candidates) {
        // Check rows for naked pairs
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (candidates[row][col].isEmpty()) {
                    continue; // Skip cells with no candidates (i.e., already filled cells)
                }
                // check the row
                if (candidates[row][col].size() == 2) {
                    int[] pairCandidates = candidates[row][col].stream().mapToInt(Integer::intValue).sorted().toArray();
                    for (int otherCol = col + 1; otherCol < 9; otherCol++) {
                        if (Arrays.equals(pairCandidates, candidates[row][otherCol].stream().mapToInt(Integer::intValue).sorted().toArray())) {
                            // Found a naked pair
                            // check that this naked pair eliminates candidates in other cells in the row
                            for (int otherCellCol = 0; otherCellCol < 9; otherCellCol++) {
                                if (otherCellCol != col && otherCellCol != otherCol) {
                                    if (candidates[row][otherCellCol].contains(pairCandidates[0]) || candidates[row][otherCellCol].contains(pairCandidates[1])) {
                                        return new CandidatesHint(Hint.HintType.NAKED_PAIR, new int[][]{{row, col}, {row, otherCol}}, pairCandidates, null, "row",
                                                "Found a naked pair in row " + (row + 1) + " with candidates " + Arrays.toString(pairCandidates)
                                                        + ". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this row.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds naked pairs in the columns of the Sudoku board candidates.
     * @param candidates the candidates for each cell in the Sudoku board
     * @return a CandidatesHint indicating the naked pair found in columns, or null if no naked pairs exist
     */
    private CandidatesHint findNakedPairInColumns(Set<Integer>[][] candidates) {
        // Check columns for naked pairs
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                if (candidates[row][col].isEmpty()) {
                    continue; // Skip cells with no candidates (i.e., already filled cells)
                }
                // check the column
                if (candidates[row][col].size() == 2) {
                    int[] pairCandidates = candidates[row][col].stream().mapToInt(Integer::intValue).sorted().toArray();
                    for (int otherRow = row + 1; otherRow < 9; otherRow++) {
                        if (Arrays.equals(pairCandidates, candidates[otherRow][col].stream().mapToInt(Integer::intValue).sorted().toArray())) {
                            // Found a naked pair
                            // check that this naked pair eliminates candidates in other cells in the column
                            for (int otherCellRow = 0; otherCellRow < 9; otherCellRow++) {
                                if (otherCellRow != row && otherCellRow != otherRow) {
                                    if (candidates[otherCellRow][col].contains(pairCandidates[0]) || candidates[otherCellRow][col].contains(pairCandidates[1])) {
                                        return new CandidatesHint(Hint.HintType.NAKED_PAIR,
                                                new int[][]{{row, col}, {otherRow, col}}, pairCandidates, null, "column",
                                                "Found a naked pair in column " + (col + 1) + " with candidates " + Arrays.toString(pairCandidates)
                                                        + ". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this column.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds naked pairs in the 3x3 boxes of the Sudoku board candidates.
     * @param candidates the candidates for each cell in the Sudoku board
     * @return a CandidatesHint indicating the naked pair found in boxes, or null if no naked pairs exist
     */
    private CandidatesHint findNakedPairInBoxes(Set<Integer>[][] candidates) {
        // Check 3x3 boxes for naked pairs
        for (int boxRowStart = 0; boxRowStart < 9; boxRowStart += 3) {
            for (int boxColStart = 0; boxColStart < 9; boxColStart += 3) {
                List<int[]> cellsWithTwoCandidates = new ArrayList<>();
                // Collect cells with exactly 2 candidates
                for (int r = boxRowStart; r < boxRowStart + 3; r++) {
                    for (int c = boxColStart; c < boxColStart + 3; c++) {
                        if (candidates[r][c].size() == 2) {
                            cellsWithTwoCandidates.add(new int[]{r, c});
                        }
                    }
                }
                // Compare each pair of cells
                for (int i = 0; i < cellsWithTwoCandidates.size(); i++) {
                    int r1 = cellsWithTwoCandidates.get(i)[0];
                    int c1 = cellsWithTwoCandidates.get(i)[1];
                    int[] pair1 = candidates[r1][c1].stream().mapToInt(Integer::intValue).toArray();
                    for (int j = i + 1; j < cellsWithTwoCandidates.size(); j++) {
                        int r2 = cellsWithTwoCandidates.get(j)[0];
                        int c2 = cellsWithTwoCandidates.get(j)[1];
                        int[] pair2 = candidates[r2][c2].stream().mapToInt(Integer::intValue).toArray();
                        if (Arrays.equals(pair1, pair2)) {
                            // check that this naked pair eliminates candidates in other cells in the box
                            for (int r = boxRowStart; r < boxRowStart + 3; r++) {
                                for (int c = boxColStart; c < boxColStart + 3; c++) {
                                    if ((r != r1 || c != c1) && (r != r2 || c != c2)) {
                                        if (candidates[r][c].contains(pair1[0]) || candidates[r][c].contains(pair1[1])) {
                                            return new CandidatesHint(Hint.HintType.NAKED_PAIR,
                                                    new int[][]{{r1, c1}, {r2, c2}},
                                                    pair1, null,
                                                    "box", "Found a naked pair in the highlighted box with candidates " + Arrays.toString(pair1)
                                                    + ". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this box.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
