package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.Set;

public class AlreadySolvedHint implements HintStrategy {
    /**
     * Checks if the current Sudoku board is already solved.
     *
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint indicating that the puzzle is already solved, or null if not solved
     */
    @Override
    public Hint findHint(SudokuBoard currentBoard, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
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
}