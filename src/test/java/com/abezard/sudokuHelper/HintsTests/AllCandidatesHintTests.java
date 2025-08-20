package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.AllCandidatesHint;
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

public class AllCandidatesHintTests {
    private SudokuHintService sudokuHintService;
    private AllCandidatesHint allCandidatesHint;
    private SudokuBoard board;

    @BeforeEach
    public void setUp() {
        board = new SudokuBoard();
        sudokuHintService = mock(SudokuHintService.class);
        allCandidatesHint = new AllCandidatesHint(sudokuHintService);
    }

    @Test
    public void testReturnsCandidatesHintWhenNotGiven() {
        // Mock computeAllCandidates to return a known value since computation has already been tested with other types of tests (repeats general logic of placing values)
        Set<Integer>[][] computed = emptyCandidates();
        computed[5][5].add(1);
        when(sudokuHintService.computeAllCandidates(board)).thenReturn(computed);
        CandidatesHint hint = allCandidatesHint.findHint(board, null, null, false);
        CandidatesHint expectedHint = new CandidatesHint(Hint.HintType.ALL_CANDIDATES, new int[][]{{0,0}}, new int[]{}, computed, "candidates", "Candidates for all empty cells have been computed. You can now see which numbers can go in each cell.");
        assertThat(hint).isEqualTo(expectedHint);
    }

    @Test
    public void testReturnsNullWhenCandidatesGiven() {
        CandidatesHint hint = allCandidatesHint.findHint(board, null, null, true);
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