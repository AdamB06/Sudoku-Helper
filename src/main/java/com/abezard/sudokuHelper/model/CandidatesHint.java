package com.abezard.sudokuHelper.model;

import java.util.Set;

public class CandidatesHint extends Hint {

    private final int[][] cells; // { {row1, col1}, {row2, col2}, ... }
    private final int[] candidates;
    private final String scope; // "row", "column", "box"
    private final Set<Integer>[][] multipleCandidates; // Set of candidates for multiple cells, if needed

    public CandidatesHint(HintType type, int[][] cells, int[] candidates, Set<Integer>[][] multipleCandidates, String scope, String explanation) {
        super(type, cells[0][0], cells[0][1], 0, explanation);
        this.cells = cells;
        this.candidates = candidates;
        this.scope = scope;
        this.multipleCandidates = multipleCandidates;
    }

    public int[][] getCellCoordinates() {
        return cells;
    }

    public int[] getCandidates() {
        return candidates;
    }

    public String getScope() {
        return scope;
    }

    public Set<Integer>[][] getMultipleCandidates() {
        return  multipleCandidates;
    }
}
