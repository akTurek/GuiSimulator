package org.example.guisimulator;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sekvencno extends Service<Void> {
    public MatrikaCelic matrikaCelic;
    public boolean isOverB = false;

    public int xsirina;
    public int ysirina;
    public int col;
    public int row;
    public BuffImage image;

    public AtomicBoolean isOverMain;
    public float change;

    public Sekvencno(int row, int col, int numOfHeat, AtomicBoolean isOver, BuffImage image) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.isOverMain = isOver;
        this.image = image;

        this.xsirina = 1;
        this.ysirina = 1;
        this.col = col;
        this.row = row;

    }


    public void calTempGUI() throws InterruptedException {
        do {
            isOverB = true;
            try {
                float maxTempChange = 0;
                float change;
                int rows = matrikaCelic.getRow();
                int cols = matrikaCelic.getCol();
                for (int i = 1; i < rows - 1; i++) {
                    for (int j = 1; j < cols - 1; j++) {
                        matrikaCelic.calPrevTemp(i, j);
                    }
                }
                for (int i = 1; i < rows - 1; i++) {
                    for (int j = 1; j < cols - 1; j++) {
                        matrikaCelic.calNowTemp(i, j);
                        change = matrikaCelic.getTempChange(i, j);
                        if (change > maxTempChange) {
                            maxTempChange = change;
                        }
                    }
                }

                draw();
                if (maxTempChange >= 0.25) {
                    isOverB = false;
                    System.out.println(maxTempChange);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } while (!isOverB);
        image.setIsOver();
        System.out.println("Koncal s simulacijo racunanjem");

    }

    public void draw() {

            System.out.println("Back rise ////////////////////////////////");
            Color [][]frame = new Color[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    Color color = matrikaCelic.getBarva(i, j);
                    frame[i][j] = color;
                }
            }
            image.submitDrawnImage(frame);
            System.out.println("Back narisal //////////////////////////////// " + change);



    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                calTempGUI();
                isOverMain.set(true);
                image.setIsOver();
                System.out.println("Koncal s simulacijo nastavil boolean");
                return null;
            }
        };
    }
}
