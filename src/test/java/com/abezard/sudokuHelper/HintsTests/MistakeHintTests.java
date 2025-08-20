package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.MistakeHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MistakeHintTests {

    @Test
    public void testDetectsMistake() {
        SudokuBoard board = new SudokuBoard();
        SudokuBoard solution = new SudokuBoard();
        // Set up a mistake at (0,0)
        board.setBoard(new int[][]{
                {5, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 2, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        solution.setBoard(new int[][]{
                {5, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 3, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        });

        MistakeHint mistakeHint = new MistakeHint();
        Hint hint = mistakeHint.findHint(board, null, solution, false);

        Hint expected = new Hint(Hint.HintType.INCORRECT_INPUT, 4, 4, 3, "Incorrect value in this cell");
        assertThat(hint).isEqualTo(expected);
    }

    @Test
    public void testNoMistake() {
        SudokuBoard board = new SudokuBoard();
        SudokuBoard solution = new SudokuBoard();
        // Both boards are the same
        board.setBoard(new int[][]{
                {3, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 4, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1}
        });
        solution.setBoard(new int[][]{
                {3, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 4, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1}
        });

        MistakeHint mistakeHint = new MistakeHint();
        Hint hint = mistakeHint.findHint(board, null, solution, false);

        assertThat(hint).isNull();
    }

    @Test
    public void testIgnoresEmptyCells() {
        SudokuBoard board = new SudokuBoard();
        SudokuBoard solution = new SudokuBoard();
        // Both boards are empty
        board.setBoard(new int[9][9]);
        solution.setBoard(new int[9][9]);

        MistakeHint mistakeHint = new MistakeHint();
        Hint hint = mistakeHint.findHint(board, null, solution, false);

        assertThat(hint).isNull();
    }
}