package com.abezard.sudokuHelper.model;

import java.util.HashSet;
import java.util.Set;

public class SudokuBoard {
    private int[][] board;
    @SuppressWarnings("unchecked")
    private final Set<Integer>[][] candidates = new HashSet[9][9];

    /**
     * Constructor for the SudokuBoard class. Initializes a 9x9 board and sets up candidates.
     */
    public SudokuBoard() {
        this.board = new int[9][9];
        initCandidates();
    }

    /**
     * Copy constructor for the SudokuBoard class. Creates a deep copy of another SudokuBoard.
     * @param other The SudokuBoard to copy from.
     */
    public SudokuBoard(SudokuBoard other) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, 9);
        }
        initCandidates();
        // deep copy candidates
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                this.candidates[r][c].clear();
                this.candidates[r][c].addAll(other.candidates[r][c]);
            }
        }
    }

    /**
     * Initializes the hashset of candidates for each cell in the Sudoku board.
     */
    private void initCandidates() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                candidates[r][c] = new HashSet<>();
            }
        }
    }

    /**
     * Sets the board to a new 9x9 integer array.
     * @param board The new board to set.
     * @throws IllegalArgumentException if the board is not 9x9.
     */
    public void setBoard(int[][] board) {
        if (board.length != 9 || board[0].length != 9) {
            throw new IllegalArgumentException("Board must be 9x9.");
        }
        this.board = board;
    }

    /**
     * Returns the current board.
     * @return The 9x9 integer array representing the Sudoku board.
     */
    public int[][] getBoard() { return board; }

    /**
     * Gets the value of a specific cell in the Sudoku board.
     * @param row The row index (0-8).
     * @param col The column index (0-8).
     * @return The value at the specified cell.
     */
    public int getCell(int row, int col) { return board[row][col]; }

    /**
     * Sets the value of a specific cell in the Sudoku board.
     * @param row The row index (0-8).
     * @param col The column index (0-8).
     * @param value The value to set at the specified cell.
     */
    public void setCell(int row, int col, int value) { board[row][col] = value; }

    /**
     * Gets the candidates for a specific cell in the Sudoku board.
     * @param row The row index (0-8).
     * @param col The column index (0-8).
     * @return A set of candidates for the specified cell.
     */
    public Set<Integer> getCandidates(int row, int col) { return candidates[row][col]; }
}
