package com.abezard.sudokuHelper.controller;

import com.abezard.sudokuHelper.model.SudokuBoard;
import com.abezard.sudokuHelper.service.FullBoardGeneratingService;
import com.abezard.sudokuHelper.service.SudokuGeneratingService;
import com.abezard.sudokuHelper.service.SudokuHintService;
import com.abezard.sudokuHelper.view.SudokuGridView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
@Component
public class SudokuController implements Initializable {

    private FullBoardGeneratingService boardGenerator;
    private SudokuGeneratingService sudokuGenerator;
    private SudokuHintService hintGenerator;

    private SudokuGridView sudokuGridView;

    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Button candidateModeButton;

    private boolean candidateMode = false;

    /**
     * Initializes the SudokuController, sets up the sudoku grid.
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object does not need localization.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setup grid
        boardGenerator = new FullBoardGeneratingService();
        sudokuGridView = new SudokuGridView();
        sudokuGrid.getChildren().clear();
        sudokuGrid.add(sudokuGridView, 0, 0);
        GridPane.setHgrow(sudokuGridView, Priority.ALWAYS);
        GridPane.setVgrow(sudokuGridView, Priority.ALWAYS);
        sudokuGridView.clearAndDisableGrid();

        // Wait for the scene to be ready and register key events
        sudokuGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if ("SHIFT".equals(event.getCode().toString())) {
                        candidateMode = !candidateMode;
                        candidateModeButton.setText(candidateMode ? "Candidate Mode: ON" : "Candidate Mode: OFF");
                        sudokuGridView.setCandidateMode(candidateMode);
                    }
                });
            }
        });
    }


    /**
     * Loads a new Sudoku puzzle into the grid view.
     * @param board The SudokuBoard to load into the grid view.
     */
    private void loadNewPuzzle(SudokuBoard board) {
        sudokuGridView.enableGrid();
        sudokuGridView.updateFromModel(board);
    }

    /**
     * Handles the event when the "New Easy Sudoku" button is clicked.
     */
    @FXML
    public void onNewEasyClicked() {
        sudokuGenerator = new SudokuGeneratingService(boardGenerator);
        SudokuBoard newBoard = sudokuGenerator.generatePuzzle("easy");
        hintGenerator = new SudokuHintService(sudokuGenerator.getSolution(), boardGenerator);
        loadNewPuzzle(newBoard);
    }

    /**
     * Handles the event when the "New Hard Sudoku" button is clicked.
     */
    @FXML
    public void onNewHardClicked() {
        sudokuGenerator = new SudokuGeneratingService(boardGenerator);
        SudokuBoard newBoard = sudokuGenerator.generatePuzzle("hard");
        hintGenerator = new SudokuHintService(sudokuGenerator.getSolution(), boardGenerator);
        loadNewPuzzle(newBoard);
    }

    /**
     * Handles the event when the "Submit" button is clicked.
     */
    @FXML
    public void onSubmitClicked() {
        if (sudokuGenerator == null) {
            showInfoDialog("Please start a puzzle before submitting.");
            return;
        }
        sudokuGridView.checkSolution(sudokuGenerator.checkSolution(sudokuGridView.getCurrentBoard()));
    }

    /**
     * Handles the event when the "Solution" button is clicked.
     */
    @FXML
    public void onRevealSolution() {
        if (sudokuGenerator == null) {
            showInfoDialog("Please start a puzzle before revealing the solution.");
            return;
        }
        sudokuGridView.revealSolution(sudokuGenerator.getSolution());
    }

    /**
     * Handles the event when the "Get Hint" button is clicked.
     */
    @FXML
    public void onGetHint() {
        if (hintGenerator == null) {
            showInfoDialog("Please start a puzzle before requesting a hint.");
            return;
        }
        if (!hintGenerator.getSolution().equals(sudokuGenerator.getSolution())) {
            hintGenerator = new SudokuHintService(sudokuGenerator.getSolution(), boardGenerator);
        }
        sudokuGridView.showHint(hintGenerator.computeHint(sudokuGridView.getCurrentBoard()));
    }

    /**
     * Toggles candidate mode on or off.
     * In candidate mode, users can enter candidates in cells without affecting the main value.
     */
    @FXML
    public void onToggleCandidateMode() {
        candidateMode = !candidateMode;
        sudokuGridView.setCandidateMode(candidateMode);
        candidateModeButton.setText(candidateMode ? "Disable Candidate Mode" : "Enable Candidate Mode");
    }

    /**
     * Displays an information dialog with the given message.
     * @param message The message to display in the dialog.
     */
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
