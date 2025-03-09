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
    public RocnoVneseneTocke list;
    public boolean gui;


    public Sekvencno(int row, int col, int numOfHeat, WritableImage image, Lock lock, Condition rendered, AtomicBoolean konec, RocnoVneseneTocke list, boolean gui) {
        this.matrikaCelic = new MatrikaCelic(row+2, col+2, numOfHeat, list);
        this.isOverB = false;
        this.lock = lock;
        this.rendered = rendered;
        this.xsirina = (int) Math.floor(image.getWidth() / (double) col);
        this.ysirina = (int) Math.floor(image.getHeight() / (double) row);
        this.list = list;
        this.canvas = new Canvas(image.getWidth(), image.getHeight());
        this.gc = canvas.getGraphicsContext2D();
        gc.setFill(matrikaCelic.getCol(0,0));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.konec = konec;
        this.gui = gui;

        System.out.println("v sekvencnem image w and h "+image.getWidth()+" "+image.getHeight());
        System.out.println("v sekvencenm velikost canvasa w h "+canvas.getWidth()+" "+canvas.getHeight());
        System.out.println("Celice: xsirina=" + xsirina + ", ysirina=" + ysirina);
        System.out.println("Izračunana širina (xsirina * col): " + (xsirina * col));
        System.out.println("Izračunana višina (ysirina * row): " + (ysirina * row));
        System.out.println("Slika w x h: " + image.getWidth() + " x " + image.getHeight());
    }

    public MatrikaCelic getMatrikaCelic() {
        return matrikaCelic;
    }

    public void calTempGUI() throws InterruptedException {
        do {
            lock.lock();
            matrikaCelic.newHS();

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


    public void calTemp() throws InterruptedException {
        do {
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

                        change = matrikaCelic.getTempChange(i, j);
                        if (change > maxTempChange) {
                            maxTempChange = change;
                        }
                    }
                }
            System.out.println(maxTempChange);
            if (maxTempChange >= 0.25) {
                isOverB = false;
            } else {
                isOverB = true;
            }
        } while (!isOverB);

    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (gui){
                    calTempGUI();
                } else {
                    calTemp();
                }
                konec.set(false);
                return null;
            }


        };
    }
}
