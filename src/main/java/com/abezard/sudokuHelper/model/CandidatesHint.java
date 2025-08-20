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

    /**
     * Getter methods to access the properties of the CandidatesHint.
     * @return the respective properties of the CandidatesHint.
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandidatesHint that)) return false;
        if (!super.equals(o)) return false;

        if (cells.length != that.cells.length || candidates.length != that.candidates.length) return false;

        for (int i = 0; i < cells.length; i++) {
            if (cells[i][0] != that.cells[i][0] || cells[i][1] != that.cells[i][1]) return false;
        }
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] != that.candidates[i]) return false;
        }
        return scope.equals(that.scope);
    }
}
