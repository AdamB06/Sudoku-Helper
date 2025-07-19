package com.abezard.sudokuHelper.model;

public class SudokuBoard {
    private int[][] board;

    public SudokuBoard() {
        this.board = new int[9][9];
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        if (board.length == 9 && board[0].length == 9) {
            this.board = board;
        } else {
            throw new IllegalArgumentException("Board must be 9x9.");
        }
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }

    public boolean isEmpty(int row, int col) {
        return board[row][col] == 0;
    }


    public void reset() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 0;
            }
        }
    }
}
