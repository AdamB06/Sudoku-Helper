package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.AlreadySolvedHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AlreadySolvedHintTests {

    @Test
    public void testAlreadySolvedReturnsHint() {
        SudokuBoard board = new SudokuBoard();
        int[][] solved = {
                {1,2,3,4,5,6,7,8,9},
                {4,5,6,7,8,9,1,2,3},
                {7,8,9,1,2,3,4,5,6},
                {2,3,4,5,6,7,8,9,1},
                {5,6,7,8,9,1,2,3,4},
                {8,9,1,2,3,4,5,6,7},
                {3,4,5,6,7,8,9,1,2},
                {6,7,8,9,1,2,3,4,5},
                {9,1,2,3,4,5,6,7,8}
        };
        board.setBoard(solved);

        AlreadySolvedHint alreadySolvedHint = new AlreadySolvedHint();
        Hint hint = alreadySolvedHint.findHint(board, null, board, false);

        Hint expected = new Hint(Hint.HintType.ALREADY_SOLVED, -1, -1, 0,
                "The Sudoku puzzle is already solved. No hints needed.");
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNotSolvedReturnsNull() {
        SudokuBoard board = new SudokuBoard();
        int[][] notSolved = {
                {1,2,3,4,5,6,7,8,0},
                {4,5,6,7,8,9,1,2,3},
                {7,8,9,1,2,3,4,5,6},
                {2,3,4,5,6,7,8,9,1},
                {5,6,7,8,9,1,2,3,4},
                {8,9,1,2,3,4,5,6,7},
                {3,4,5,6,7,8,9,1,2},
                {6,7,8,9,1,2,3,4,5},
                {9,1,2,3,4,5,6,7,8}
        };
        SudokuBoard solution = new SudokuBoard();
        int[][] solved = {
                {1,2,3,4,5,6,7,8,9},
                {4,5,6,7,8,9,1,2,3},
                {7,8,9,1,2,3,4,5,6},
                {2,3,4,5,6,7,8,9,1},
                {5,6,7,8,9,1,2,3,4},
                {8,9,1,2,3,4,5,6,7},
                {3,4,5,6,7,8,9,1,2},
                {6,7,8,9,1,2,3,4,5},
                {9,1,2,3,4,5,6,7,8}
        };
        board.setBoard(notSolved);
        solution.setBoard(solved);

        AlreadySolvedHint alreadySolvedHint = new AlreadySolvedHint();
        Hint hint = alreadySolvedHint.findHint(board, null, solution, false);

        assertThat(hint).isNull();
    }
}