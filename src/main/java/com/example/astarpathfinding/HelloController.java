package com.example.astarpathfinding;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    ArrayList<GraphNode> graphNodes = new ArrayList<>();
    ArrayList<GraphNode> path = new ArrayList<>();
    double aStarPlaneWidth;
    double aStarPlaneHeight;
    int tilesAcross;
    int tilesDown;
    int tileAmount;
    int gridSize = 50;
    double playbackSpeed = 0.1;
    GraphNode start;
    GraphNode goal;


    Color backgroundColor1 = Color.WHITE;
    Color backgroundColor2 = Color.color(0.82,0.82,0.82);

    @FXML
    private AnchorPane gamePlane;

    @FXML
    private TextField gridSizeInput;

    @FXML
    private ComboBox<String> colorPicker2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        aStarPlaneWidth = gamePlane.getPrefWidth();
        aStarPlaneHeight = gamePlane.getPrefHeight();
        updateGrid();

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Wall",
                        "Goal",
                        "Start",
                        "Blank"
                );
        colorPicker2.setItems(options);
    }

    public Color getColor(String option){
        return switch (option) {
            case "Wall" -> Color.BLACK;
            case "Goal" -> Color.YELLOW;
            case "Start" -> Color.GREEN;
            case "Blank" -> Color.WHITE;
            default -> null;
        };
    }

    private void updateGrid() {
        tilesAcross = (int) (aStarPlaneWidth / gridSize);
        tileAmount = (int) ((aStarPlaneWidth/gridSize) * (aStarPlaneHeight/gridSize));
        tilesDown = tileAmount/tilesAcross;

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
        drawRectangle(event);
    }

    @FXML
    void planeDragged(MouseEvent event) {
        drawRectangle(event);
    }

    private void drawRectangle(MouseEvent event) {
        Color color = getColor(colorPicker2.getValue());
        double mouseX = event.getX();
        double mouseY = event.getY();

        int x = (int) ((mouseX/gridSize) % tilesAcross) * gridSize;
        int y = (int) ((mouseY/gridSize) % tilesDown) * gridSize;

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
        graphNodes.clear();
        updateGrid();
    }

    @FXML
    void runGBFS(ActionEvent event) {
        findGoalNode();
        findStartNode();
        setupNodes();
        path.clear();
        bfs(start);

        Iterator<GraphNode> nodeIterator = path.iterator();
        nodeIterator.next();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(playbackSpeed), ev -> {
            colorRectangle(nodeIterator.next().getRectangle(), Color.RED);
        }));
        timeline.setCycleCount(path.size() - 1);
        timeline.playFromStart();

        timeline.setOnFinished(e -> findPathTraversal());
    }

    public void findPathTraversal(){
        ArrayList<GraphNode> traversalPath = new ArrayList<>();

        path.forEach(node -> node.setVisited(false));

        GraphNode currentNode = path.get(path.size() - 1);
        traversalPath.add(currentNode);

        while (currentNode != start){
            ArrayList<GraphNode> neighbours = getNeighbours(currentNode, path);

            currentNode = neighbours.get(0);

            for (int i = 1; i < neighbours.size(); i++) {
                if(neighbours.get(i).getH() > currentNode.getH()){
                    currentNode = neighbours.get(i);
                }
            }
            traversalPath.add(currentNode);
        }
        Iterator<GraphNode> iterator = traversalPath.iterator();
        Timeline traversalTimeline = new Timeline(new KeyFrame(Duration.seconds(playbackSpeed), ev -> {
            colorRectangle(iterator.next().getRectangle(), Color.BLUE);
        }));
        traversalTimeline.setCycleCount(traversalPath.size() - 1);
        traversalTimeline.playFromStart();
    }

    public void setupNodes(){
        ObservableList<Node> rectangles = gamePlane.getChildren();

        int rectangleIndex = 0;


        for (int i = 0; i < tilesDown; i++) {
            for (int j = 0; j < tilesAcross; j++) {
                Rectangle rectangle = (Rectangle) rectangles.get(rectangleIndex);

                GraphNode graphNode;

                Color color = (Color) rectangle.getFill();
                if(color.equals(Color.BLACK)){
                    graphNode = new GraphNode(rectangle, j, i, -1);
                } else {
                    graphNode = new GraphNode(rectangle, j, i, calculateHeuristic(rectangle, goal.getRectangle()));
                }
                graphNodes.add(graphNode);
                rectangleIndex++;
            }
        }
    }

    public void findGoalNode(){
        ObservableList<Node> rectangles = gamePlane.getChildren();

        int rectangleIndex = 0;

        for (int i = 0; i < tilesDown; i++) {
            for (int j = 0; j < tilesAcross; j++) {
                Rectangle rectangle = (Rectangle) rectangles.get(rectangleIndex);

                Color color = (Color) rectangle.getFill();
                if (color.equals(Color.YELLOW)){
                    goal = new GraphNode(rectangle, j, i, 0);
                    return;
                }
                rectangleIndex++;
            }
        }
    }
    public void findStartNode(){
        ObservableList<Node> rectangles = gamePlane.getChildren();

        int rectangleIndex = 0;

        for (int i = 0; i < tilesDown; i++) {
            for (int j = 0; j < tilesAcross; j++) {
                Rectangle rectangle = (Rectangle) rectangles.get(rectangleIndex);

                Color color = (Color) rectangle.getFill();
                if (color.equals(Color.GREEN)){
                    start = new GraphNode(rectangle, j, i, calculateHeuristic(rectangle, goal.getRectangle()));
                    return;
                }
                rectangleIndex++;
            }
        }
    }

    public double calculateHeuristic(Rectangle rectangle, Rectangle goal){
        return Math.sqrt(Math.pow(goal.getX() - rectangle.getX(), 2) + Math.pow(goal.getY() - rectangle.getY(), 2));
    }

    public void bfs(GraphNode start){
        GraphNode currentNode = start;
        PriorityQueue<GraphNode> queue = new PriorityQueue<GraphNode>();
        queue.add(currentNode);

        while (!queue.isEmpty()){
            currentNode = queue.peek();
            currentNode.setVisited(true);
            path.add(currentNode);
            ArrayList<GraphNode> neighbours = getNeighbours(currentNode, graphNodes);
            for (GraphNode neighbour: neighbours) {
                if(neighbour.getH() == 0){
                    return;
                }
            }
            queue.remove(currentNode);
            queue.addAll(neighbours);
        }
        System.out.println("No result");
    }

    public void colorRectangle(Rectangle rectangle, Color color){
        rectangle.setFill(color);
    }

    public ArrayList<GraphNode> getNeighbours(GraphNode currentNode, ArrayList<GraphNode> possibleGraphNodes){
        ArrayList<GraphNode> neighbours = new ArrayList<>();

        for (GraphNode graphNode: possibleGraphNodes) {
            if(graphNode.isVisited()){
                continue;
            }
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if(graphNode.getI() == currentNode.getI()+i && graphNode.getJ() == currentNode.getJ()+j &&
                    graphNode.getH() != -1){
                        graphNode.setVisited(true);
                        neighbours.add(graphNode);
                    }
                }
            }
        }
        return neighbours;
    }

    @FXML
    void showHeuristic(ActionEvent event) {
        findGoalNode();
        findStartNode();
        setupNodes();

        for (GraphNode graphNode: graphNodes) {
            Text text = new Text();
            text.setText("h:"  + (int)graphNode.getH());
            text.setX(graphNode.getX());
            text.setY(graphNode.getY() + 15);

            gamePlane.getChildren().add(text);
        }

    }

}
