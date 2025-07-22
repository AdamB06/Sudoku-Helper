package com.abezard.sudokuHelper.model;

public record Hint(HintType type, int row, int col, int value, String explanation) {
    public enum HintType {
        ALREADY_SOLVED,   // The Sudoku puzzle is already solved
        INCORRECT_INPUT, // User has a mistake in their inputs
        NAKED_SINGLE,     // Only candidate in a cell (basic solving step)
        HIDDEN_SINGLE,    // Only place a digit can go in a row/col/box
        // add more advanced types later (Naked pairs, pointing pairs, etc.)
    }

}
