package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.NakedSingleHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NakedSinglesTests {
    private NakedSingleHint nakedSingleHint;

    @BeforeEach
    public void setUp() {
        nakedSingleHint = new NakedSingleHint(new FullBoardGeneratingService());
    }

    @Test
    public void testNakedSingleFound() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[][]{
                {5, 1, 2, 0, 4, 9, 7, 8, 3},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 6, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        Hint hint = nakedSingleHint.findHint(board, null, null, false);
        Hint expectedHint = new Hint(Hint.HintType.NAKED_SINGLE, 0, 3, 6, "Only one possible value in this cell");
        assertThat(hint).isEqualTo(expectedHint);
    }

    @Test
    public void testNoNakedSingle() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[9][9]); // All cells empty
        Hint hint = nakedSingleHint.findHint(board, null, null, false);
        assertThat(hint).isNull();
    }
}