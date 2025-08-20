package com.abezard.sudokuHelper.HintsTests;

import com.abezard.sudokuHelper.logic.HiddenSingleHint;
import com.abezard.sudokuHelper.model.Hint;
import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HiddenSingleTests {
    private HiddenSingleHint hiddenSingleHint;
    @BeforeEach
    public void setUp() {
        hiddenSingleHint = new HiddenSingleHint(new FullBoardGeneratingService());
    }

    @Test
    public void testHiddenSingleInRow() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[][] {
                {5, 1, 2, 0, 4, 9, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 6, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        Hint hint = hiddenSingleHint.findHint(board, null, null, false);
        Hint expectedHint = new Hint(Hint.HintType.HIDDEN_SINGLE, 0, 3, 6,
                "Row 1 can only contain this number in this cell.");
        assertThat(hint).isEqualTo(expectedHint);
    }

    @Test
    public void testHiddenSingleInColumn() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[][] {
                {5, 0, 0, 0, 0, 0, 0, 0, 0},
                {6, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1},
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        Hint hint = hiddenSingleHint.findHint(board, null, null, false);
        Hint expectedHint = new Hint(Hint.HintType.HIDDEN_SINGLE, 6, 0, 1,
                "Column 1 can only contain this number in this cell.");
        assertThat(hint).isEqualTo(expectedHint);
    }

    @Test
    public void testHiddenSingleInBox() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[][] {
                {5, 0, 0, 0, 0, 0, 0, 0, 0},
                {6, 0, 1, 0, 0, 0, 0, 0, 0},
                {4, 0, 0, 0, 2, 0, 0, 0, 1},
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        Hint hint = hiddenSingleHint.findHint(board, null, null, false);
        Hint expectedHint = new Hint(Hint.HintType.HIDDEN_SINGLE, 0, 2, 2,
                "This box can only contain this number in this cell.");
        assertThat(hint).isEqualTo(expectedHint);
    }

    @Test
    public void testNoHiddenSingle() {
        SudokuBoard board = new SudokuBoard();
        board.setBoard(new int[9][9]);
        Hint hint = hiddenSingleHint.findHint(board, null, null, false);
        assertThat(hint).isNull();
    }
}
