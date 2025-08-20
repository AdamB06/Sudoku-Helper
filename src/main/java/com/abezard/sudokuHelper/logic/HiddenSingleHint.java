package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;

import java.util.Set;

public class HiddenSingleHint implements HintStrategy {

    private final FullBoardGeneratingService boardGenerator;
    /**
     * Constructor for HiddenSingleHint.
     * @param boardGenerator the service used to generate and validate Sudoku boards
     */
    public HiddenSingleHint(FullBoardGeneratingService boardGenerator) {
        this.boardGenerator = boardGenerator;
    }

    /**
     * Finds hidden singles in the current Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the hidden single found, or null if none exists
     */
    @Override
    public Hint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        Hint hint = findHiddenSingleRows(board);
        if(hint == null) {
            hint = findHiddenSingleColumns(board);
        }
        if(hint == null) {
            hint = findHiddenSingleBoxes(board);
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
}
