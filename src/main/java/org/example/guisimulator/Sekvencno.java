package org.example.guisimulator;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    public Color [][] frame;

    public Sekvencno(int row, int col, int numOfHeat, AtomicBoolean isOver, BuffImage image) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.isOverMain = isOver;
        this.image = image;
        this.xsirina = 1;
        this.ysirina = 1;
        this.col = col;
        this.row = row;
        this.frame = new Color[row][col];

    }


    public void calTempGUI() throws InterruptedException {
        do {
            frame = image.getDrawImage();
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
                for (int i = 0; i < rows ; i++) {
                    for (int j = 0; j < cols ; j++) {
                        matrikaCelic.calNowTemp(i, j);
                        change = matrikaCelic.getTempChange(i, j);
                        if (change > maxTempChange) {
                            maxTempChange = change;
                        }
                        frame[i][j]=matrikaCelic.getBarva(i,j);
                    }
                }

                image.submitDrawnImage();
                if (maxTempChange >= 0.25) {
                    isOverB = false;
                    System.out.println(maxTempChange);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } while (!isOverB);
        System.out.println("Koncal s simulacijo racunanjem");

    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                calTempGUI();
                isOverMain.set(true);
                image.setLastFrame();
                System.out.println("Koncal s simulacijo nastavil boolean");
                return null;
            }
        };
    }
}
