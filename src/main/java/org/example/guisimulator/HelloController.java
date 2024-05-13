package org.example.guisimulator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.control.TextField;


public class HelloController {

    double gVisina = 1000;
    double gSirina= 1000;

    Rectangle[][] rectangles;

    int xsirina;
    int ysirina;


    @FXML
    TextField sirinalable;
    @FXML
    TextField visinalable;
    @FXML
    Button start;
    @FXML
    Canvas canvas;

    @FXML
    GraphicsContext gc;


    @FXML
    private GridPane grid = new GridPane();
    @FXML
    private AnchorPane pane;

    public HelloController() {

    }

    @FXML
    private void initialize() {

        grid.setPrefHeight(800);
        grid.setPrefWidth(800);
        pane.setPrefHeight(800);
        pane.setPrefWidth(1000);
        gc = canvas.getGraphicsContext2D();
    }

    @FXML
    private void narisiGrid(double rowNum, double colNum) {

        pane.getChildren().remove(grid);
        grid = new GridPane();


        Rectangle [][] simRectengle= new Rectangle[(int) rowNum][(int) colNum];
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                Rectangle rec = new Rectangle();
                rec.setWidth(gSirina / colNum);
                rec.setHeight(gVisina / rowNum);

                if ((row + col) % 2 == 0) {
                    rec.setFill(Color.GRAY);
                } else {
                    rec.setFill(Color.BLUE);
                }

                simRectengle[row][col] = rec;
                rectangles = simRectengle;
                grid.add(rec, col, row);
            }
            grid.setHgap(0);
            grid.setVgap(0);
        }
        pane.getChildren().add(grid);
       pane.setTopAnchor(grid, 0.0);
       pane.setLeftAnchor(grid, 400.0);
    }

    @FXML
    public void zacbiSim() {


        int row = Integer.valueOf(sirinalable.getText());
        int col = Integer.valueOf(visinalable.getText());

        simulacija(row, col);
    }


    @FXML
    public void pobarvajKvadart(int rowIndex, int colIndex, float temp) {
        Rectangle rec = rectangles[rowIndex][colIndex];
        rec.setFill(barva(temp));

        System.out.println("barvam");
    }

    @FXML
    public void pobarvajKvadartCanvas (int rowIndex, int colIndex, float temp) {
        gc.setFill(barva(temp));
        gc.fillRect(rowIndex,colIndex,rowIndex+xsirina,colIndex+ysirina);
    }

    private Color barva(float temp){
        double normalizedValue = temp / 100.0;
         if (temp< 50){
             return Color.color(0, normalizedValue*2 , 1);

         } else {
             return Color.color(1, 1-(normalizedValue/2), 1 );
         }

    }

    @FXML
    private void simulacija(int row, int col){
        Sekvencno sekvencno = new Sekvencno(row,col,20,100, this);
        grid.getChildren().clear();
        narisiGrid(row, col);
        sekvencno.calTemp();
    }

    @FXML
    private void simulacijaCanvas(int row, int col){
        Sekvencno sekvencno = new Sekvencno(row,col,20,100, this);
        xsirina = 600/row;
        ysirina = 600/col;


        sekvencno.calTemp();
    }







}






