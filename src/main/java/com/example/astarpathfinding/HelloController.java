package com.example.astarpathfinding;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    double aStarPlaneWidth;
    double aStarPlaneHeight;
    int tilesAcross;
    int tileAmount;
    int gridSize = 25;

    Color backgroundColor1 = Color.WHITE;
    Color backgroundColor2 = Color.color(0.82,0.82,0.82);

    @FXML
    private AnchorPane gamePlane;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField gridSizeInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        aStarPlaneWidth = gamePlane.getPrefWidth();
        aStarPlaneHeight = gamePlane.getPrefHeight();
        updateGrid();
    }

    private void updateGrid() {
        tilesAcross = (int) (aStarPlaneWidth / gridSize);
        tileAmount = (int) ((aStarPlaneWidth/gridSize) * (aStarPlaneHeight/gridSize));

        for(int i = 0; i < tileAmount; i++){
            int x = (i % tilesAcross);
            int y = (i / tilesAcross);

            Rectangle rectangle = new Rectangle(x * gridSize,y * gridSize,gridSize,gridSize);


            if((x + y) % 2 == 0){
                rectangle.setFill(backgroundColor1);
            } else {
                rectangle.setFill(backgroundColor2);
            }
            gamePlane.getChildren().add(rectangle);
        }
    }


    @FXML
    void planeClicked(MouseEvent event) {
        Color color = colorPicker.getValue();
        double mouseX = event.getX();
        double mouseY = event.getY();

        int x = (int) ((mouseX/gridSize) % tilesAcross) * gridSize;
        int y = (int) ((mouseY/gridSize) % (tileAmount/tilesAcross)) * gridSize;

        ObservableList<Node> rectangles = gamePlane.getChildren();

        for (Node node: rectangles) {
            Rectangle rectangle = (Rectangle) node;
            if(rectangle.getX() == x && rectangle.getY() == y){
                rectangle.setFill(color);
                return;
            }
        }
    }

    @FXML
    void planeDragged(MouseEvent event) {
        Color color = colorPicker.getValue();
        double mouseX = event.getX();
        double mouseY = event.getY();

        int x = (int) ((mouseX/gridSize) % tilesAcross) * gridSize;
        int y = (int) ((mouseY/gridSize) % (tileAmount/tilesAcross)) * gridSize;

        ObservableList<Node> rectangles = gamePlane.getChildren();

        for (Node node: rectangles) {
            Rectangle rectangle = (Rectangle) node;
            if(rectangle.getX() == x && rectangle.getY() == y){
                rectangle.setFill(color);
                return;
            }
        }
    }

    @FXML
    void clearPlane(ActionEvent event) {
        gridSize = Integer.parseInt(gridSizeInput.getText());
        gamePlane.getChildren().clear();
        updateGrid();
    }


}
