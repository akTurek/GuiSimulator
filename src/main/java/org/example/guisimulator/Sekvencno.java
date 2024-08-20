package org.example.guisimulator;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Sekvencno extends Service<Void> {
    public MatrikaCelic matrikaCelic;
    public boolean isOverB;
    public Canvas canvas;
    public Lock lock;
    public Condition rendered;
    public int xsirina;
    public int ysirina;
    public GraphicsContext gc;
    public AtomicBoolean konec;


    public Sekvencno(int row, int col, int numOfHeat, WritableImage image, Lock lock, Condition rendered, AtomicBoolean konec) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.isOverB = false;
        this.lock = lock;
        this.rendered = rendered;
        xsirina = (int) Math.round(image.getHeight() / row);
        ysirina = (int) Math.round(image.getWidth()/ col);
        this.canvas = new Canvas(image.getWidth(), image.getHeight());
        this.gc = canvas.getGraphicsContext2D();
        gc.setFill(matrikaCelic.getCol(0,0));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.konec = konec;
    }

    public MatrikaCelic getMatrikaCelic() {
        return matrikaCelic;
    }

    public void calTemp() throws InterruptedException {
        do {
            lock.lock();
            try {
                float maxTempChange = 0;
                float change;
                int rows = matrikaCelic.getRow();
                int cols = matrikaCelic.getCol();
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        matrikaCelic.calPrevTemp(i, j);
                    }
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        matrikaCelic.calNowTemp(i, j);
                        gc.setFill(matrikaCelic.getCol(i, j));
                        gc.fillRect(i * xsirina, j * ysirina, xsirina, ysirina);


                        change = matrikaCelic.getTempChange(i, j);
                        if (change > maxTempChange) {
                            maxTempChange = change;
                        }
                    }
                }
                if (maxTempChange >= 0.25) {
                    isOverB = false;
                } else {
                    isOverB = true;
                }
                rendered.await();
            } finally {
                lock.unlock();
            }
        } while (!isOverB);

    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                calTemp();
                konec.set(false);
                return null;
            }
        };
    }
}
