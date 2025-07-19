package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.model.SudokuBoard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class SudokuGeneratorService {

    private final Random random = new Random();

    public SudokuBoard generateFullBoard() {
        SudokuBoard board = new SudokuBoard();
        fillBoard(board);
        return board;
    }

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

    private boolean isValidPlacement(SudokuBoard board, int row, int col, int num) {
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
