package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.SudokuHintService;

import java.util.Set;

public class LastCandidateHint implements HintStrategy {
    private final SudokuHintService sudokuHintService;

    /**
     * Constructor for LastCandidateHint.
     * @param sudokuHintService the service used to manage Sudoku hints
     */
    public LastCandidateHint(SudokuHintService sudokuHintService) {
        this.sudokuHintService = sudokuHintService;
    }

    /**
     * Checks if there is only one candidate left in any empty cell of the current Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the last candidate found, or null if no such candidate exists
     */
    @Override
    public Hint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (candidates[row][col].size() == 1 && board.getCell(row, col) == 0) {
                    int lastCandidate = candidates[row][col].iterator().next();
                    if( solution.getCell(row, col) != lastCandidate) {
                        Set<Integer>[][] c = sudokuHintService.computeAllCandidates(board);
                        return new CandidatesHint(Hint.HintType.ALL_CANDIDATES, new int[][]{{0,0}}, new int[0], c,"candidates",
                                "Candidates for all empty cells have been computed. Some other candidates were possible in some cells, or an incorrect candidate was marked.");
                    }
                    sudokuHintService.setCandidatesGiven(false); // since a new value is set, certain candidates will now be invalid
                    return new Hint(Hint.HintType.LAST_CANDIDATE, row, col, lastCandidate,
                            "Only one candidate left in this cell: " + lastCandidate);
                }
            }
        }
        return null;
    }
}
