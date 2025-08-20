package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.PointingPairHint;
import com.abezard.sudokuHelper.model.CandidatesHint;
import com.abezard.sudokuHelper.model.Hint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PointingPairTests {
    private PointingPairHint pointingPairHint;

    @BeforeEach
    public void setUp() {
        pointingPairHint = new PointingPairHint();
    }

    @Test
    public void testPointingPairInRow() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Box (0,0): cells (0,0) and (0,1) both have 5, and (0,3) outside box also has 5
        candidates[0][0] = setOf(5);
        candidates[0][1] = setOf(5);
        candidates[0][3] = setOf(5);

        CandidatesHint hint = pointingPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.POINTING_PAIR,
                new int[][]{{0, 0}, {0, 1}},
                new int[]{5},
                null,
                "row",
                "Pointing pair: digit 5 in row 1 confined to box (row: 1, col: 1). This means this digit can only appear in this row of this box, so we can eliminate it from other cells outside this box in this row."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testPointingPairInColumn() {
        Set<Integer>[][] candidates = emptyCandidates();
        // Box (0,0): cells (0,0) and (1,0) both have 7, and (3,0) outside box also has 7
        candidates[0][0] = setOf(7);
        candidates[1][0] = setOf(7);
        candidates[3][0] = setOf(7);

        CandidatesHint hint = pointingPairHint.findHint(null, candidates, null, false);
        CandidatesHint expected = new CandidatesHint(
                Hint.HintType.POINTING_PAIR,
                new int[][]{{0, 0}, {1, 0}},
                new int[]{7},
                null,
                "column",
                "Pointing pair: digit 7 in column 1 confined to box (row: 1, col: 1). This means this digit can only appear in this column of this box, so we can eliminate it from other cells outside this box in this column."
        );
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNoPointingPair() {
        Set<Integer>[][] candidates = emptyCandidates();
        candidates[0][0] = setOf(1, 2);
        candidates[1][1] = setOf(2, 3);
        CandidatesHint hint = pointingPairHint.findHint(null, candidates, null, false);
        assertThat(hint).isNull();
    }

    // Helpers
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