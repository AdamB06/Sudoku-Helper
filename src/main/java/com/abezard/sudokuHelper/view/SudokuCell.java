package com.abezard.sudokuHelper.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import java.util.Set;

public class SudokuCell extends StackPane {

    private final TextField valueField = new TextField();
    private final GridPane candidatesGrid = new GridPane();
    private boolean candidateMode = false;
    private Set<Integer> candidates = new java.util.HashSet<>();

    /**
     * Constructor for the custom JavaFX object SudokuCell. Allows for easy switching
     * between candidate mode and regular value entry mode.
     */
    public SudokuCell() {
        getStyleClass().add("sudoku-cell");
        setPrefSize(50, 50);

        valueField.getStyleClass().add("value-field");
        valueField.setAlignment(Pos.CENTER);
        valueField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        valueField.setPrefSize(50, 50);
        valueField.setOnKeyPressed(e -> {
            String text = e.getText();
            if (candidateMode && valueField.getText().isEmpty() && text.matches("[1-9]")) {
                int digit = Integer.parseInt(text);
                if (candidates.contains(digit)) {
                    candidates.remove(digit);
                } else {
                    candidates.add(digit);
                }
                updateCandidateLabels();
                e.consume();
            } else if (!candidateMode && text.matches("[1-9]")) {
                if (valueField.getText().equals(text)) {
                    Platform.runLater(valueField::clear);
                }
                else {
                    valueField.clear(); // Note: Happens before the typed key is processed, hence replacing the old value
                }
                candidates.clear();
                updateCandidateLabels();
                candidatesGrid.setVisible(false);
                e.consume();
            }
        });

        // Candidates grid setup
        candidatesGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        candidatesGrid.setAlignment(Pos.CENTER);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100.0 / 3);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(100.0 / 3);
        for (int i = 0; i < 3; i++) {
            candidatesGrid.getColumnConstraints().add(cc);
            candidatesGrid.getRowConstraints().add(rc);
        }
        for (int i = 0; i < 9; i++) {
            Label l = new Label();
            l.getStyleClass().add("candidate");
            l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            l.setAlignment(Pos.CENTER);
            GridPane.setColumnIndex(l, i % 3);
            GridPane.setRowIndex(l, i / 3);
            candidatesGrid.getChildren().add(l);
        }
        getChildren().addAll(candidatesGrid, valueField);
    }

    /**
     * Sets the value of this Sudoku cell and updates the candidates grid visibility.
     * @param v the integer value to set (0 for empty).
     * @param disable true to disable the value field, false to enable it.
     */
    public void setValue(int v, boolean disable) {
        if (v == 0) {
            valueField.setText("");
            valueField.setDisable(disable);
            candidatesGrid.setVisible(true);
        } else {
            valueField.setText(String.valueOf(v));
            valueField.setDisable(disable);
            candidates.clear();
            updateCandidateLabels();
            candidatesGrid.setVisible(false);
        }
    }

    /**
     * Getter for the value of this Sudoku cell.
     * @return the integer value of the cell, or 0 if the cell is empty.
     */
    public Integer getValue() {
        String t = valueField.getText();
        return (t == null || t.isEmpty()) ? 0 : Integer.parseInt(t);
    }

    /**
     * Getter for the value field.
     * @return the TextField used for entering values in the Sudoku cell.
     */
    public TextField valueField() { return valueField; }

    /**
     * Sets the candidates for this Sudoku cell.
     * @param cands a Set of integers representing the candidates (1-9) for this cell.
     */
    public void setCandidates(Set<Integer> cands) {
        candidates = cands;
        updateCandidateLabels();
    }

    /**
     * Getter for the candidates of this Sudoku cell.
     * @return a Set of integers representing the candidates (1-9) for this cell.
     */
    public Set<Integer> getCandidates() {
        return candidates;
    }

    /**
     * Adds a hint style to the Sudoku cell and its value field.
     */
    public void addHintStyle() {
        getStyleClass().add("hint-cell");
    }

    /**
     * Toggles the candidate mode for this Sudoku cell.
     * @param mode true to enable candidate mode, false for regular value entry mode.
     */
    public void setCandidateMode(boolean mode) {
        candidateMode = mode;
        valueField.setEditable(!candidateMode);
    }

    /**
     * Updates the candidate labels in the grid based on the current candidates set.
     * This method is called whenever a modification is made to the set of candidates.
     */
    private void updateCandidateLabels() {
        for (int i = 0; i < 9; i++) {
            Label label = (Label) candidatesGrid.getChildren().get(i);
            int digit = i + 1;
            label.setText(candidates.contains(digit) ? String.valueOf(digit) : "");
        }
        candidatesGrid.setVisible(valueField.getText().isEmpty());
    }

    /**
     * Getter for the text field.
     * @return the TextField used for entering values in the Sudoku cell.
     */
    public TextField getValueField() { return valueField; }
}
