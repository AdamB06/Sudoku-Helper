package com.abezard.sudokuHelper;

import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import com.abezard.sudokuHelper.service.SudokuGeneratingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleGeneratingTests {

    SudokuGeneratingService generateSudoku;
    FullBoardGeneratingService boardGenerator;

    @BeforeEach
    void setUp() {
        boardGenerator = new FullBoardGeneratingService();
        generateSudoku = new SudokuGeneratingService(boardGenerator);
    }

    @Test
    void testGenerateEasySudokuHasCorrectNumberOfClues() {
        SudokuBoard sudoku = generateSudoku.generatePuzzle(SudokuGeneratingService.Difficulty.EASY);
        long filledCells = countFilledCells(sudoku);
        assertEquals(28, filledCells, "Easy puzzle should have exactly 28 clues");
    }

    @Test
    void testGenerateHardSudokuHasCorrectNumberOfClues() {
        SudokuBoard sudoku = generateSudoku.generatePuzzle(SudokuGeneratingService.Difficulty.HARD);
        long filledCells = countFilledCells(sudoku);
        assertTrue(filledCells >= 18, "Hard puzzle should have at least 18 clues for a unique solution");
    }

    @Test
    void testSudokuHaveUniqueSolutions() {
        SudokuBoard easy = generateSudoku.generatePuzzle(SudokuGeneratingService.Difficulty.EASY);
        SudokuBoard hard = generateSudoku.generatePuzzle(SudokuGeneratingService.Difficulty.HARD);
        assertEquals(1, generateSudoku.countSolutions(easy), "Generated Sudoku puzzle should have a unique solution");
        assertEquals(1, generateSudoku.countSolutions(hard), "Generated Sudoku puzzle should have a unique solution");
    }

    private long countFilledCells(SudokuBoard sudoku) {
        int[][] board = sudoku.getBoard();
        return java.util.Arrays.stream(board)
                .flatMapToInt(java.util.Arrays::stream)
                .filter(n -> n != 0)
                .count();
    }
}
