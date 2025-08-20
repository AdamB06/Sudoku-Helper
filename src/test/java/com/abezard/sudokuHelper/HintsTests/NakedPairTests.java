package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.NakedPairHint;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NakedPairTests {
    private NakedPairHint nakedPairHint;

    @BeforeEach
    public void setUp() {
        nakedPairHint = new NakedPairHint();
    }

    @Test
    public void testNakedPairInRow() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Row 0: cells (0,1) and (0,4) both have {2,3}, (0,2) has 2,3,4. We remove 2,3 from (0,2)
        candidates[0][1] = setOf(2, 3);
        candidates[0][2] = setOf(2, 3, 4);
        candidates[0][4] = setOf(2, 3);

        CandidatesHint hint = nakedPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.NAKED_PAIR,
                new int[][]{{0, 1}, {0, 4}},
                new int[]{2, 3},
                null,
                "row",
                "Found a naked pair in row 1 with candidates [2, 3]. This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this row."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNakedPairInColumn() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Column 2: cells (1,2) and (5,2) both have {4,7}, (3,2) has 4,7,8
        candidates[1][2] = setOf(4, 7);
        candidates[5][2] = setOf(4, 7);
        candidates[3][2] = setOf(4, 7, 8);

        CandidatesHint hint = nakedPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.NAKED_PAIR,
                new int[][]{{1, 2}, {5, 2}},
                new int[]{4, 7},
                null,
                "column",
                "Found a naked pair in column 3 with candidates [4, 7]. This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this column."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNakedPairInBox() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Box (3,3): cells (3,3) and (4,4) both have {5,6}, (5,5) has 5,6,9
        candidates[3][3] = setOf(5, 6);
        candidates[4][4] = setOf(5, 6);
        candidates[5][5] = setOf(5, 6, 9);

        CandidatesHint hint = nakedPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.NAKED_PAIR,
                new int[][]{{3, 3}, {4, 4}},
                new int[]{5, 6},
                null,
                "box",
                "Found a naked pair in the highlighted box with candidates [5, 6]. This means these two cells can only contain these two candidates, and we can eliminate them from other cells in this box."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNoNakedPair() {
        Set<Integer>[][] candidates = emptyCandidates();
        candidates[0][0] = setOf(1, 2, 3);
        candidates[0][1] = setOf(2, 3, 4);
        CandidatesHint hint = nakedPairHint.findHint(null, candidates, null, false);
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