package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NakedSingleHint implements  HintStrategy {
    private final FullBoardGeneratingService boardGenerator;

    /**
     * Constructor for NakedSingleHint.
     * @param boardGenerator the service used to generate and validate Sudoku boards
     */
    public NakedSingleHint(FullBoardGeneratingService boardGenerator) {
        this.boardGenerator = boardGenerator;
    }
    /**
     * Finds a naked single in the current Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the naked single found, or null if none exists
     */
    @Override
    public Hint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
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
}
