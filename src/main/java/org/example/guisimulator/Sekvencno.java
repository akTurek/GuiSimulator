package org.example.guisimulator;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


import java.util.concurrent.atomic.AtomicBoolean;


public class Sekvencno extends Service<Void> {
    public MatrikaCelic matrikaCelic;
    public boolean isOverB= false;;

    public int xsirina;
    public int ysirina;
    public int col;
    public int row;

    public AtomicBoolean isOverMain;
    public AtomicBoolean emptyBuff;
    public WritableImage lastImage;


    public Sekvencno(int row, int col, int numOfHeat, AtomicBoolean isOver ,WritableImage lastImage, AtomicBoolean emptyBuff ) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.isOverMain = isOver;
        this.emptyBuff=emptyBuff;
        this.xsirina = 1;
        this.ysirina = 1;
        this.col = col;
        this.row = row;;
        this.lastImage = lastImage;


    }

    public void calTempGUI() throws InterruptedException {

        do {

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

    }


    public void draw(){
        if (emptyBuff.get()){
            WritableImage current = new WritableImage(row, col);
            PixelWriter pixelWriter = current.getPixelWriter();
            for (int i = 1; i < row - 1; i++) {
                for (int j = 1; j < col - 1; j++) {
                    Color color = matrikaCelic.getBarva(i,j);
                    pixelWriter.setColor(i, j, color);

                }
            }
            lastImage = current;
            emptyBuff.set(false);
        }

    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                calTempGUI();
                isOverMain.set(false);
                return null;
            }


        };
    }
}
