package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.LastCandidateHint;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.SudokuHintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LastCandidateHintTests {
    private SudokuHintService sudokuHintService;
    private LastCandidateHint lastCandidateHint;
    private SudokuBoard solution;
    private SudokuBoard board;
    private Set<Integer>[][] candidates;

    @BeforeEach
    public void setUp() {
        solution = new SudokuBoard();
        board = new SudokuBoard();
        // Mock the hint service to avoid unnecessary complexity
        sudokuHintService = mock(SudokuHintService.class);
        lastCandidateHint = new LastCandidateHint(sudokuHintService);
        candidates = emptyCandidates();
    }

@Test
public void testLastCandidateFound() {
    // Only one candidate in (2,3), cell is empty, and matches solution
    candidates[2][3].add(7);
    board.setBoard(new int[9][9]);
    solution.setBoard(new int[9][9]);
    solution.setCell(2, 3, 7);
    Hint hint = lastCandidateHint.findHint(board, candidates, solution, false);
    Hint expected = new Hint(Hint.HintType.LAST_CANDIDATE, 2, 3, 7,
            "Only one candidate left in this cell: 7");
    assertThat(hint).isEqualTo(expected);
}

    @Test
    public void testLastCandidateIncorrectTriggersAllCandidates() {
        // Only one candidate in (2,4), cell is empty, but does NOT match solution
        candidates[2][4].add(5);
        board.setBoard(new int[9][9]);
        solution.setBoard(new int[9][9]);
        solution.setCell(2, 4, 7);

        // Mock computeAllCandidates to return a known value since computation has already been tested with other types of tests (repeats general logic of placing values)
        Set<Integer>[][] allCands = emptyCandidates();
        allCands[2][4].add(5); // Reflect the candidate in the tested cell
        when(sudokuHintService.computeAllCandidates(board)).thenReturn(allCands);
        Hint hint = lastCandidateHint.findHint(board, candidates, solution, false);

        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.ALL_CANDIDATES,
                new int[][]{{0, 0}},
                new int[0],
                allCands,
                "candidates",
                "Candidates for all empty cells have been computed. Some other candidates were possible in some cells, or an incorrect candidate was marked."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNoLastCandidate() {
        // No cell has only one candidate
        candidates[1][1].add(1);
        candidates[1][1].add(2);
        board.setBoard(new int[9][9]);
        solution.setBoard(new int[9][9]);
        solution.setCell(1, 1, 1);

        Hint hint = lastCandidateHint.findHint(board, candidates, solution, false);

        assertThat(hint).isNull();
    }

    // Helper to create empty candidate arrays
    @SuppressWarnings("unchecked")
    private Set<Integer>[][] emptyCandidates() {
        Set<Integer>[][] cands = new HashSet[9][9];
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                cands[r][c] = new HashSet<>();
        return cands;
    }
}