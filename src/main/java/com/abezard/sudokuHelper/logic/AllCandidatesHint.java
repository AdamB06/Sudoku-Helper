package com.abezard.sudokuHelper.logic;

import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.SudokuHintService;

import java.util.Set;

public class AllCandidatesHint implements  HintStrategy {
    private final SudokuHintService sudokuHintService;
    /**
     * Constructor for AllCandidatesHint.
     * @param sudokuHintService the service used to compute candidates for Sudoku cells
     */
    public AllCandidatesHint(SudokuHintService sudokuHintService) {
        this.sudokuHintService = sudokuHintService;
    }

    /**
     * Finds all candidates for each empty cell in the Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a Hint indicating the candidates for each empty cell, or null if no candidates exist
     */
    @Override
    public CandidatesHint findHint(SudokuBoard board, Set<Integer>[][] candidates, SudokuBoard solution, boolean candidatesGiven) {
        if (!candidatesGiven){
            // If candidates have never been given, compute and return them all candidates
            Set<Integer>[][] computedCandidates = sudokuHintService.computeAllCandidates(board);
            sudokuHintService.setCandidatesGiven(true);
            return new CandidatesHint(Hint.HintType.ALL_CANDIDATES, new int[][]{{0,0}}, new int[0], computedCandidates,"candidates",
                    "Candidates for all empty cells have been computed. You can now see which numbers can go in each cell.");
        }
        return null;
    }
}
