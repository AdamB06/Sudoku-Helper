package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.Set;

public class MistakeHint implements HintStrategy {

    /**
     * Finds mistakes in the current Sudoku board by comparing it to the solution.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the first mistake found, or null if no mistakes are found
     */
    @Override
    public Hint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        // Check each cell against the solution
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int currentValue = board.getCell(row, col);
                int correctValue = solution.getCell(row, col);
                if (currentValue != 0 && currentValue != correctValue) {
                    return new Hint(Hint.HintType.INCORRECT_INPUT, row, col, correctValue,
                            "Incorrect value in this cell");
                }
            }
        }
        return null;
    }
}
