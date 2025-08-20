package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.controller.SudokuController;
import com.abezard.sudokuHelper.logic.*;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;

import java.util.*;

public class SudokuHintService {
    private final SudokuBoard solution;
    private final FullBoardGeneratingService boardGenerator;
    private boolean candidatesGiven = false; // Flag to show if candidates have been given to user
    private final SudokuController controller;
    List<HintStrategy> hintStrategies;

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
        this.hintStrategies = List.of(new MistakeHint(), new AlreadySolvedHint(),
                new NakedSingleHint(boardGenerator),
                new HiddenSingleHint(boardGenerator),
                new AllCandidatesHint(this),
                new LastCandidateHint(this),
                new NakedPairHint(),
                new HiddenPairHint(),
                new PointingPairHint()
        );
    }

    /**
     * Computes a hint for the current Sudoku board.
     * @param currentBoard the current state of the Sudoku board
     * @return a Hint object containing the hint information, or null if no hint can be provided
     */
    public Hint computeHint(SudokuBoard currentBoard) {
        Set<Integer>[][] candidates = controller.getCandidates();
        for (HintStrategy strategy : hintStrategies) {
            Hint hint = strategy.findHint(currentBoard, candidates, solution, candidatesGiven);
            if (hint instanceof CandidatesHint) return hint;
            if( hint != null) {
                candidatesGiven = false; // since a new hint is given, candidates might need to be recomputed
                return hint;
            }
        }
        return null;
    }

    /**
     * Computes all candidates for each empty cell in the Sudoku board.
     * @param board the current state of the Sudoku board
     * @return a 2D array of sets containing candidates for each cell
     */
    @SuppressWarnings("unchecked")
    public Set<Integer>[][] computeAllCandidates(SudokuBoard board) {
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

    /**
     * Sets the candidatesGiven flag.
     * @param candidatesGiven boolean flag indicating if candidates have been given to the user
     */
    public void setCandidatesGiven(boolean candidatesGiven) {
        this.candidatesGiven = candidatesGiven;
    }
}
