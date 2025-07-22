package com.abezard.sudokuHelper.service;

import com.abezard.sudokuHelper.model.Hint;
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
    private SudokuBoard solution;

    /**
     * Constructor for SudokuGeneratingService.
     * @param generator The FullBoardGeneratingService used to generate a complete Sudoku board and check placements.
     */
    public SudokuGeneratingService(FullBoardGeneratingService generator) {
        this.generator = generator;
    }

    /**
     * Generates a Sudoku puzzle based on the specified difficulty level.
     * @param difficulty The difficulty level of the puzzle, either "easy" or "hard".
     * @return A SudokuBoard object representing the generated puzzle with a certain amount of removed values. Ensures uniqueness of the solution.
     */
    public SudokuBoard generatePuzzle(String difficulty) {
        solution = generator.generateFullBoard();
        SudokuBoard puzzle = new SudokuBoard(solution);

        // Create a list of all coordinates in the Sudoku grid
        List<int[]> positions = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                positions.add(new int[]{row, col});
            }
        }

        Collections.shuffle(positions, random); // Shuffle all the coordinates to randomly remove cells
        int clues = difficulty.equals("easy") ? 28 : 18; // Number of clues based on difficulty
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

    /**
     * Counts the number of solutions for a given Sudoku board. (Helper method for ensuring uniqueness of the solution)
     * @param board The SudokuBoard to check for solutions.
     * @return The number of valid solutions for the given board.
     */
    public int countSolutions(SudokuBoard board) {
        return countSolutionsHelper(board, 0);
    }

    /**
     * Recursive helper method to count the number of solutions for a Sudoku board.
     * @param board The SudokuBoard to check for solutions.
     * @param count The current count of valid solutions found.
     * @return The total number of valid solutions found.
     */
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

    /**
     * Checks the user's solution against the generated solution.
     * @param currentBoard The SudokuBoard object representing the user's current inputs.
     * @return A SudokuBoard object with the user's inputs marked as correct (0) or incorrect (-1).
     */
    public SudokuBoard checkSolution(SudokuBoard currentBoard) {
        if (solution == null) {
            throw new IllegalStateException("No solution available. Generate a puzzle first.");
        }
        // Use the solution to return a mapping of which inputs are wrong.
        SudokuBoard userSolution = new SudokuBoard(currentBoard);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (userSolution.getCell(row, col) != solution.getCell(row, col)) {
                    userSolution.setCell(row, col, -1); //Incorrect value
                }
                else{
                    userSolution.setCell(row, col, 0); // Correct value
                }
            }
        }
        return userSolution; // return the board with correct and incorrect values
    }

    /**
     * Returns the solution to the generated Sudoku puzzle.
     * @return A SudokuBoard object representing the solution, or null if no solution has been generated yet.
     */
    public SudokuBoard getSolution() {
        if (solution == null) {
            return null;
        }
        return new SudokuBoard(solution); // Return a copy of the solution
    }
}
