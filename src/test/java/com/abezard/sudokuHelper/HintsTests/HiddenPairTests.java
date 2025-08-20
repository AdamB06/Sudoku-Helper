package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.HiddenPairHint;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class HiddenPairTests {
    private HiddenPairHint hiddenPairHint;

    @BeforeEach
    public void setUp() {
        hiddenPairHint = new HiddenPairHint();
    }

    @Test
    public void testHiddenPairInRow() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Box (3,3): cells (3,3) and (4,4) both have {5,6}, (5,5) has 5,6,9
        candidates[0][3] = setOf(5, 6);
        candidates[0][4] = setOf(5, 6, 8, 9);

        CandidatesHint hint = hiddenPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.HIDDEN_PAIR,
                new int[][]{{0, 3}, {0, 4}},
                new int[]{5, 6},
                null,
                "row",
                "Hidden pair [5, 6] found in row 1. This means these two cells are the only ones that can contain these two candidates, and we can eliminate all other candidates from these two cells."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testHiddenPairInColumn() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Column 2: cells (3,2) and (5,2) both have {4,7}, eliminate 8 from (3,2)
        candidates[5][2] = setOf(4, 7);
        candidates[3][2] = setOf(4, 7, 8);

        CandidatesHint hint = hiddenPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.HIDDEN_PAIR,
                new int[][]{{3, 2}, {5, 2}},
                new int[]{4, 7},
                null,
                "column",
                "Hidden pair [4, 7] found in column 3. This means these two cells are the only ones that can contain these two candidates, and we can eliminate all other candidates from these two cells."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testHiddenPairInBox() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Box 1 has candidate pair 2,3 only in cells (0,1) and (0,2), meaning we can remove 4 as a candidate from cell (0,2)
        candidates[0][1] = setOf(2, 3);
        candidates[1][2] = setOf(2, 3, 4);

        CandidatesHint hint = hiddenPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.HIDDEN_PAIR,
                new int[][]{{0, 1}, {1, 2}},
                new int[]{2, 3},
                null,
                "box",
                "Hidden pair [2, 3] found in box (row: 1, col: 1). This means these two cells are the only ones that can contain these two candidates, and we can eliminate all other candidates from these two cells.");
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNoHiddenPair() {
        Set<Integer>[][] candidates = emptyCandidates();
        CandidatesHint hint = hiddenPairHint.findHint(null, candidates, null, false);
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

    private Set<Integer> setOf(int... vals) {
        Set<Integer> set = new HashSet<>();
        for (int v : vals) set.add(v);
        return set;
    }
}
