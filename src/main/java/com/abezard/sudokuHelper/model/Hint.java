package com.abezard.sudokuHelper.model;

public class Hint{
    public enum HintType {
        ALREADY_SOLVED, // The Sudoku puzzle is already solved
        INCORRECT_INPUT, // User has a mistake in their inputs
        NAKED_SINGLE, // Only candidate in a cell (basic solving step)
        HIDDEN_SINGLE, // Only place a digit can go in a row/col/box
        NAKED_PAIR, // Two cells in a unit share the same two candidates - Since both cells can only have the same two digits, we can eliminate those candidates from other cells in that unit
        HIDDEN_PAIR, // Two digits only appear in the same two cells in a unit - we can eliminate those candidates from other cells in that unit
        POINTING_PAIR, // Candidate confined to a row/col inside a box — eliminate from rest of that row/col;
    }
    private final int row;
    private final int col;
    private final int value; // The value related to the hint, e.g., candidate digit
    private final String explanation;
    private final HintType type;

    /**
     * Constructor for Hint.
     * @param type        the type of the hint
     * @param row         the row of the cell related to the hint
     * @param col         the column of the cell related to the hint
     * @param value       the value related to the hint (e.g., candidate digit)
     * @param explanation a brief explanation of the hint
     */
    public Hint(HintType type, int row, int col, int value, String explanation) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.explanation = explanation;
        this.type = type;
    }

    public int row() {
        return row;
    }
    public int col() {
        return col;
    }
    public int value() {
        return value;
    }
    public String explanation() {
        return explanation;
    }
    public HintType type() {
        return type;
    }
}