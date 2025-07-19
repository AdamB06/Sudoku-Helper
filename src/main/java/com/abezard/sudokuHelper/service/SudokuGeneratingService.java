package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.model.SudokuBoard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class SudokuGeneratingService {

    private final FullBoardGeneratingService generator;
    private final Random random = new Random();

    public SudokuGeneratingService(FullBoardGeneratingService generator) {
        this.generator = generator;
    }

    public SudokuBoard generatePuzzle(String difficulty) {
        SudokuBoard fullBoard = generator.generateFullBoard();
        SudokuBoard puzzle = new SudokuBoard(fullBoard);

        // Create a list of all coordinates in the Sudoku grid
        List<int[]> positions = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                positions.add(new int[]{row, col});
            }
        }

        Collections.shuffle(positions, random); // Shuffle all the coordinates to randomly remove cells
        int clues = difficulty.equals("easy") ? 36 : 24; // Number of clues based on difficulty
        int removed = 0;

        for (int[] pos : positions) {
            if (81 - removed <= clues) break; // stop if we removed enough cells

            int row = pos[0], col = pos[1];
            int backup = puzzle.getCell(row, col);
            puzzle.setCell(row, col, 0);

            if (countSolutions(puzzle) != 1) {
                puzzle.setCell(row, col, backup); // Restore if not uniquely solvable
            } else {
                removed++;
            }
        }
        return puzzle; // Return the newly created sudoku puzzle
    }


    public int countSolutions(SudokuBoard board) {
        return countSolutionsHelper(board, 0);
    }

    private int countSolutionsHelper(SudokuBoard board, int count) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (generator.isValidPlacement(board, row, col, num)) {
                            board.setCell(row, col, num);
                            count = countSolutionsHelper(board, count);
                            board.setCell(row, col, 0);
                            if (count > 1) return count; // Early stop if more than 1 solution
                        }
                    }
                    return count; // No valid number -> invalid board
                }
            }
        }
        return count + 1; // Only one valid solution
    }
}
