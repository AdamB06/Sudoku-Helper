package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.controller.SudokuController;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.*;

public class SudokuHintService {
    private final SudokuBoard solution;
    private final FullBoardGeneratingService boardGenerator;
    private boolean candidatesGiven = false; // Flag to show if candidates have been given to user
    private final SudokuController controller;

    /**
     * Constructor for SudokuHintService.
     * @param solution the solution SudokuBoard
     * @param boardGenerator the service used to generate and validate Sudoku boards
     * @param controller the SudokuController to interact with the view
     */
    public SudokuHintService(SudokuBoard solution, FullBoardGeneratingService boardGenerator, SudokuController controller) {
        this.solution = solution;
        this.boardGenerator = boardGenerator;
        this.controller = controller;
    }

    /**
     * Computes a hint for the current Sudoku board.
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint object containing the hint information, or null if no hint can be provided
     */
    public Hint computeHint(SudokuBoard currentBoard) {
        Hint mistakeHint = findMistakes(currentBoard);
        if(mistakeHint != null) {
            return mistakeHint;
        }
        Hint alreadySolvedHint = isSolved(currentBoard);
        if (alreadySolvedHint != null) {
            return alreadySolvedHint;
        }
        // Naked Single: Check each empty cell for a single candidate
        Hint nakedSingleHint = findNakedSingle(currentBoard);
        if (nakedSingleHint != null) {
            return nakedSingleHint;
        }
        // Hidden Single: Check rows, columns, and boxes for unique candidates
        Hint hiddenSingleHint = findHiddenSingle(currentBoard);
        if (hiddenSingleHint != null) {
            return hiddenSingleHint;
        }

        if (!candidatesGiven){
            // If candidates have never been given, compute and return them all candidates
            Set<Integer>[][] candidates = computeAllCandidates(currentBoard);
            candidatesGiven = true;
            return new CandidatesHint(Hint.HintType.ALL_CANDIDATES, new int[][]{{0,0}}, new int[0], candidates,"candidates",
                    "Candidates for all empty cells have been computed. You can now see which numbers can go in each cell.");
        }

        Hint oneCandidateLeftHint = checkLastCandidate(currentBoard);
        if (oneCandidateLeftHint != null) {
            return oneCandidateLeftHint;
        }

        // Naked Pair: Check for pairs of cells in rows, columns, and boxes that can only contain the same two candidates, and no others.
        Hint nakedPairHint = findNakedPair(currentBoard);
        if (nakedPairHint != null) {
            return nakedPairHint;
        }

        // Hidden Pair: Check for pairs of candidates in rows, columns, and boxes that can only fit in two cells.
        Hint hiddenPairHint = findHiddenPair(currentBoard);
        if (hiddenPairHint != null) {
            return hiddenPairHint;
        }
        // Pointing Pair: Check for candidates that can be eliminated from other cells in the same row or column of a box.
        return findPointingPairHint(currentBoard);
    }

    /**
     * Getter for the solution SudokuBoard.
     * @return the solution SudokuBoard
     */
    public SudokuBoard getSolution() {
        return solution;
    }

    /**
     * Checks if the current Sudoku board is already solved.
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint indicating that the puzzle is already solved, or null if not solved
     */
    private Hint isSolved(SudokuBoard currentBoard) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (solution.getCell(row, col) != currentBoard.getCell(row, col)) {
                    return null;
                }
            }
        }
        return new Hint(Hint.HintType.ALREADY_SOLVED, -1, -1, 0,
                "The Sudoku puzzle is already solved. No hints needed.");
    }

    /**
     * Finds mistakes in the current Sudoku board by comparing it to the solution.
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint indicating the first mistake found, or null if no mistakes are found
     */
    private Hint findMistakes(SudokuBoard currentBoard) {
        // Check each cell against the solution
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int currentValue = currentBoard.getCell(row, col);
                int correctValue = solution.getCell(row, col);
                if (currentValue != 0 && currentValue != correctValue) {
                    return new Hint(Hint.HintType.INCORRECT_INPUT, row, col, correctValue,
                            "Incorrect value in this cell");
                }
            }
        }
        return null;
    }

    /**
     * Finds a naked single in the current Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the naked single found, or null if none exists
     */
    private Hint findNakedSingle(SudokuBoard board) {
        // Check each empty cell to see if it has exactly one possible value
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    int candidateCount = 0;
                    int candidateValue = -1;
                    for (int num = 1; num <= 9; num++) {
                        if (boardGenerator.isValidPlacement(board, row, col, num)) {
                            candidateCount++;
                            candidateValue = num;
                        }
                    }
                    if (candidateCount == 1) {
                        return new Hint(Hint.HintType.NAKED_SINGLE, row, col, candidateValue,
                                "Only one possible value in this cell");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds hidden singles in the current Sudoku board.
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint indicating the hidden single found, or null if none exists
     */
    private Hint findHiddenSingle(SudokuBoard currentBoard) {
        Hint hint = findHiddenSingleRows(currentBoard);
        if(hint == null) {
            hint = findHiddenSingleColumns(currentBoard);
        }
        if(hint == null) {
            hint = findHiddenSingleBoxes(currentBoard);
        }
        return hint;
    }

    /**
     * Finds hidden singles in the rows of the Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the hidden single found in a row, or null if none exists
     */
    private Hint findHiddenSingleRows(SudokuBoard board) {
        // Check each row to see if a number can only fit in one cell
        for(int row = 0; row < 9; row++) {
            for(int num = 1; num <= 9; num++) {
                int count = 0;
                int colIndex = -1;
                for(int col = 0; col < 9; col++) {
                    if(board.getCell(row, col) == 0 && boardGenerator.isValidPlacement(board, row, col, num)) {
                        count++;
                        colIndex = col;
                    }
                }
                if(count == 1) {
                    return new Hint(Hint.HintType.HIDDEN_SINGLE, row, colIndex, num,
                            "Row " + (row + 1) +" can only contain this number in this cell.");
                }
            }
        }
        return null;
    }

    /**
     * Finds hidden singles in the columns of the Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the hidden single found in a column, or null if none exists
     */
    private Hint findHiddenSingleColumns(SudokuBoard board) {
        // Check each column to see if a number can only fit in one cell
        for(int col = 0; col < 9; col++) {
            for(int num = 1; num <= 9; num++) {
                int count = 0;
                int rowIndex = -1;
                for(int row = 0; row < 9; row++) {
                    if(board.getCell(row, col) == 0 && boardGenerator.isValidPlacement(board, row, col, num)) {
                        count++;
                        rowIndex = row;
                    }
                }
                if(count == 1) {
                    return new Hint(Hint.HintType.HIDDEN_SINGLE, rowIndex, col, num,
                            "Column " + (col + 1) +" can only contain this number in this cell.");
                }
            }
        }
        return null;
    }

    /**
     * Finds hidden singles in the boxes of the Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the hidden single found in a boxes, or null if none exists
     */
    private Hint findHiddenSingleBoxes(SudokuBoard board) {
        // Check each 3x3 box to see if a number can only fit in one cell
        for(int boxRow = 0; boxRow < 3; boxRow++) {
            for(int boxCol = 0; boxCol < 3; boxCol++) {
                for(int num = 1; num <= 9; num++) {
                    int count = 0;
                    int rowIndex = -1, colIndex = -1;
                    for(int row = boxRow * 3; row < (boxRow + 1) * 3; row++) {
                        for(int col = boxCol * 3; col < (boxCol + 1) * 3; col++) {
                            if(board.getCell(row, col) == 0 && boardGenerator.isValidPlacement(board, row, col, num)) {
                                count++;
                                rowIndex = row;
                                colIndex = col;
                            }
                        }
                    }
                    if(count == 1) {
                        return new Hint(Hint.HintType.HIDDEN_SINGLE, rowIndex, colIndex, num,
                                "This box can only contain this number in this cell.");
                    }
                }
            }
        }
        return null;
    }

    private CandidatesHint findNakedPair(SudokuBoard currentBoard) {
        Set<Integer>[][] candidates = controller.getCandidates();
        // Check rows for naked pairs
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                if (candidates[row][col].isEmpty()) {
                    continue; // Skip cells with no candidates (i.e., already filled cells)
                }
                // check the row
                if(candidates[row][col].size() == 2) {
                    int[] pairCandidates = candidates[row][col].stream().mapToInt(Integer::intValue).sorted().toArray();
                    for(int otherCol = col + 1; otherCol < 9; otherCol++) {
                        if(Arrays.equals(pairCandidates, candidates[row][otherCol].stream().mapToInt(Integer::intValue).sorted().toArray())) {
                            // Found a naked pair
                            // check that this naked pair eliminates candidates in other cells in the row
                            for(int otherCellCol = 0; otherCellCol < 9; otherCellCol++) {
                                if(otherCellCol != col && otherCellCol != otherCol) {
                                    if(candidates[row][otherCellCol].contains(pairCandidates[0]) || candidates[row][otherCellCol].contains(pairCandidates[1])) {
                                        return new CandidatesHint(Hint.HintType.NAKED_PAIR, new int[][]{{row, col}, {row, otherCol}}, pairCandidates, null, "row",
                                                "Found a naked pair in row " + (row + 1) + " with candidates " + Arrays.toString(pairCandidates)
                                                        +". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this row.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Check columns for naked pairs
        for(int col = 0; col < 9; col++) {
            for(int row = 0; row < 9; row++) {
                if (candidates[row][col].isEmpty()) {
                    continue; // Skip cells with no candidates (i.e., already filled cells)
                }
                // check the column
                if(candidates[row][col].size() == 2) {
                    int[] pairCandidates = candidates[row][col].stream().mapToInt(Integer::intValue).sorted().toArray();
                    for(int otherRow = row + 1; otherRow < 9; otherRow++) {
                        if(Arrays.equals(pairCandidates, candidates[otherRow][col].stream().mapToInt(Integer::intValue).sorted().toArray())) {
                            // Found a naked pair
                            // check that this naked pair eliminates candidates in other cells in the column
                            for(int otherCellRow = 0; otherCellRow < 9; otherCellRow++) {
                                if(otherCellRow != row && otherCellRow != otherRow) {
                                    if(candidates[otherCellRow][col].contains(pairCandidates[0]) || candidates[otherCellRow][col].contains(pairCandidates[1])) {
                                        return new CandidatesHint(Hint.HintType.NAKED_PAIR,
                                    new int[][]{{row, col}, {otherRow, col}}, pairCandidates, null, "column",
                                    "Found a naked pair in column " + (col + 1) + " with candidates " + Arrays.toString(pairCandidates)
                                            +". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this column.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
                                                    +". This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this box.");
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

    private CandidatesHint findHiddenPair(SudokuBoard board) {
        Set<Integer>[][] candidates = controller.getCandidates();

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

    private boolean sameLocations(List<int[]> a, List<int[]> b) {
        return (a.size() == b.size()) &&
                a.stream().allMatch(cellA ->
                        b.stream().anyMatch(cellB ->
                                cellA[0] == cellB[0] && cellA[1] == cellB[1]));
    }


    @SuppressWarnings("unchecked")
    private Set<Integer>[][] computeAllCandidates(SudokuBoard board) {
        Set<Integer>[][] candidates = new HashSet[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) != 0) {
                    candidates[row][col] = new HashSet<>();
                    continue;
                }
                Set<Integer> candidateSet = new HashSet<>();
                for( int num = 1; num <= 9; num++) {
                    if (boardGenerator.isValidPlacement(board, row, col, num)) {
                        candidateSet.add(num);
                    }
                }
                candidates[row][col] = candidateSet;
            }
        }
        return candidates;
    }

    private CandidatesHint findPointingPairHint(SudokuBoard currentBoard) {
        Set<Integer>[][] candidates = controller.getCandidates();

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
                                                " confined to box (row: " + (3*boxRow+1) + ", col: " + (3*boxCol+1) + "). This means this digit can only appear in this row of this box, so we can eliminate it from other cells outside this box in this row."
                                );                            }
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
                                                " confined to box (row: " + (3*boxRow+1) + ", col: " + (3*boxCol+1) + "). This means this digit can only appear in this column of this box, so we can eliminate it from other cells outside this box in this column."
                                );
                            }
                        }
                    }
                }
            }
        }

        return null; // No pointing pair found
    }

    private Hint checkLastCandidate(SudokuBoard currentBoard) {
        // Check if there is only one candidate left in the board
        Set<Integer>[][] candidates = controller.getCandidates();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (candidates[row][col].size() == 1 && currentBoard.getCell(row, col) == 0) {
                    int lastCandidate = candidates[row][col].iterator().next();
                    if( solution.getCell(row, col) != lastCandidate) {
                        Set<Integer>[][] c = computeAllCandidates(currentBoard);
                        return new CandidatesHint(Hint.HintType.ALL_CANDIDATES, new int[][]{{0,0}}, new int[0], c,"candidates",
                                "Candidates for all empty cells have been computed. Some other candidates were possible in some cells, or an incorrect candidate was marked.");
                    }
                    candidatesGiven = false; // since a new value is set, certain candidates will now be invalid
                    return new Hint(Hint.HintType.LAST_CANDIDATE, row, col, lastCandidate,
                            "Only one candidate left in this cell: " + lastCandidate);
                }
            }
        }
        return null;
    }
}
