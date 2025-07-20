package com.abezard.sudokuHelper.controller;

import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import com.abezard.sudokuHelper.service.SudokuGeneratingService;
import com.abezard.sudokuHelper.view.SudokuGridView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
@Component
public class SudokuController implements Initializable {

    private FullBoardGeneratingService boardGenerator;
    private SudokuGeneratingService sudokuGenerator;

    private SudokuGridView sudokuGridView;

    @FXML
    private GridPane sudokuGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boardGenerator = new FullBoardGeneratingService();
        sudokuGridView = new SudokuGridView();
        sudokuGrid.getChildren().clear();
        sudokuGrid.add(sudokuGridView, 0, 0);
        GridPane.setHgrow(sudokuGridView, Priority.ALWAYS);
        GridPane.setVgrow(sudokuGridView, Priority.ALWAYS);

        // Instead of loading a puzzle, clear and disable grid on startup
        sudokuGridView.clearAndDisableGrid();
    }


    private void loadNewPuzzle(SudokuBoard board) {
        sudokuGridView.enableGrid();
        sudokuGridView.updateFromModel(board);
    }

    @FXML
    public void onNewEasyClicked(ActionEvent actionEvent) {
        sudokuGenerator = new SudokuGeneratingService(boardGenerator);
        SudokuBoard newBoard = sudokuGenerator.generatePuzzle("easy");
        loadNewPuzzle(newBoard);
    }

    @FXML
    public void onNewHardClicked(ActionEvent actionEvent) {
        sudokuGenerator = new SudokuGeneratingService(boardGenerator);
        SudokuBoard newBoard = sudokuGenerator.generatePuzzle("hard");
        loadNewPuzzle(newBoard);
    }

    @FXML
    public void onSubmitClicked(ActionEvent actionEvent) {
        sudokuGridView.checkSolution(sudokuGenerator.checkSolution(sudokuGridView.getCurrentBoard()));
    }

    @FXML
    public void onRevealSolution(ActionEvent actionEvent) {
        sudokuGridView.revealSolution(sudokuGenerator.getSolution());
    }
}
