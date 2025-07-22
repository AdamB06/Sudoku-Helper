package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

public class SudokuHintService {
    private final SudokuBoard solution;
    private final FullBoardGeneratingService boardGenerator;

    /**
     * Constructor for SudokuHintService.
     * @param solution the solution SudokuBoard
     * @param boardGenerator the service used to generate and validate Sudoku boards
     */
    public SudokuHintService(SudokuBoard solution, FullBoardGeneratingService boardGenerator) {
        this.solution = solution;
        this.boardGenerator = boardGenerator;
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
        // Add more advanced hint types here (e.g., naked pairs, pointing pairs, etc.)
        return null;
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
                            "In row " + (row + 1) +", a certain number can only be placed in this cell.");
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
                            "In column " + (col + 1) +", a certain number can only be placed in this cell.");
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
                                "This cell is the only remaining empty one in its box");
                    }
                }
            }
        }
        return null;
    }
}
