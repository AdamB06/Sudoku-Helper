package com.abezard.sudokuHelper.model;

public record Hint(HintType type, int row, int col, int value, String explanation) {
    public enum HintType {
        ALREADY_SOLVED, // The Sudoku puzzle is already solved
        INCORRECT_INPUT, // User has a mistake in their inputs
        NAKED_SINGLE, // Only candidate in a cell (basic solving step)
        HIDDEN_SINGLE, // Only place a digit can go in a row/col/box
        NAKED_PAIR, // Two cells in a unit share the same two candidates - Since both cells can only have the same two digits, we can eliminate those candidates from other cells in that unit
        HIDDEN_PAIR, // Two digits only appear in the same two cells in a unit - we can eliminate those candidates from other cells in that unit
        POINTING_PAIR, // Candidate confined to a row/col inside a box — eliminate from rest of that row/col
    }

}
