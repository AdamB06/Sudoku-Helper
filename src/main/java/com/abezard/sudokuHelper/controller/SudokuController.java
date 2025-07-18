package com.abezard.sudokuHelper.controller;

import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.SudokuGeneratorService;
import com.abezard.sudokuHelper.view.SudokuGridView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
@Component
public class SudokuController implements Initializable {

    @Autowired
    private SudokuGeneratorService sudokuGenerator;

    private SudokuGridView sudokuGridView;

    @FXML
    private GridPane sudokuGrid;  // This is a placeholder container in FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        System.out.println("New Easy Sudoku Puzzle Clicked");
        SudokuBoard newBoard = sudokuGenerator.generateEasySudoku();
        loadNewPuzzle(newBoard);
    }

    @FXML
    public void onNewHardClicked(ActionEvent actionEvent) {
        System.out.println("New Hard Sudoku Puzzle Clicked");
        SudokuBoard newBoard = sudokuGenerator.generateHardSudoku();
        loadNewPuzzle(newBoard);
    }
}
