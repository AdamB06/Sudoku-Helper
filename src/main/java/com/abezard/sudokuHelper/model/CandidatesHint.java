package com.abezard.sudokuHelper.model;

public class CandidatesHint extends Hint {

    private final int[][] cells; // { {row1, col1}, {row2, col2}, ... }
    private final int[] candidates;
    private final String scope; // "row", "column", "box"

    public CandidatesHint(HintType type, int[][] cells, int[] candidates, String scope, String explanation) {
        super(type, cells[0][0], cells[0][1], 0, explanation);
        this.cells = cells;
        this.candidates = candidates;
        this.scope = scope;
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
}
