package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.model.SudokuBoard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class FullBoardGeneratingService {

    private final Random random = new Random();

    /**
     * Generates a full Sudoku board with all cells filled.
     * @return The filled SudokuBoard.
     */
    public SudokuBoard generateFullBoard() {
        SudokuBoard board = new SudokuBoard();
        fillBoard(board);
        return board;
    }

    /**
     * Fills the Sudoku board using a backtracking algorithm, and the minimum remaining values heuristic.
     * @param board The SudokuBoard to fill.
     * @return true if the board is successfully filled, false otherwise.
     */
   private boolean fillBoard(SudokuBoard board) {
        int minOptions = 10;
        int targetRow = -1, targetCol = -1;
        List<Integer> candidates = null;
        // Find the cell with the minimum remaining values
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    List<Integer> options = new ArrayList<>();
                    for (int num = 1; num <= 9; num++) {
                        if (isValidPlacement(board, row, col, num)) {
                            options.add(num);
                        }
                    }
                    if (options.size() < minOptions) {
                        minOptions = options.size();
                        targetRow = row;
                        targetCol = col;
                        candidates = options;
                        if (minOptions == 1) break;
                    }
                }
            }
        }

        // None are empty -> board is complete
        if (targetRow == -1) return true;

        Collections.shuffle(candidates, random);
        for (int num : candidates) {
            board.setCell(targetRow, targetCol, num);
            if (fillBoard(board)) {
                return true;
            }
            board.setCell(targetRow, targetCol, 0);
        }
        return false;
    }

    /**
     * Checks if placing a number in the specified cell is valid according to Sudoku rules.
     * @param board The SudokuBoard to check.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param num The number to place in the cell.
     * @return true if the placement is valid, false otherwise.
     */
    public boolean isValidPlacement(SudokuBoard board, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board.getCell(row, i) == num || board.getCell(i, col) == num) {
                return false;
            }
        }
        // Check 3x3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getCell(boxRow + i, boxCol + j) == num) {
                    return false;
                }
            }
        }
        return true;
    }
}
