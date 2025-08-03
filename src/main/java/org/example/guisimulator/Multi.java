package org.example.guisimulator;


import javafx.concurrent.Service;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Multi  extends Service {
    public MatrikaCelic matrikaCelic;

    public int xsirina;
    public int ysirina;
    public int numberOfThreads = Runtime.getRuntime().availableProcessors()-1;
    public AtomicBoolean isOverMain;
    public AtomicBoolean isOver;
    public BuffImage image;
    public Color [][] frame;
    int col, row;


    public Multi(int row, int col, int numOfHeat, AtomicBoolean isOverMain, BuffImage image) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.xsirina = 1;
        this.ysirina = 1;
        this.image = image;
        this.isOverMain = isOverMain;
        this.isOver = new AtomicBoolean(true);
        this.row = row;
        this.col = col;
        this.frame = new Color[row][col];
    }

    public void calTemp() throws InterruptedException {


        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Runnable reset = () -> {
            this.isOver.set(true);
            this.frame = image.getDrawImage();
            System.out.println("Back narisal");
        };

        Runnable publish = () -> {
            if(!image.getFull()){
                this.image.submitDrawnImage();
            }

        };

        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(numberOfThreads, reset);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads,publish);


        for (int i = 0; i < numberOfThreads; i++) {

            Task task = new Task(this, i, cyclicBarrier,cyclicBarrierStart);
            executorService.submit(task);
        }


        // Shutdown the thread pool
        executorService.shutdown();

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while waiting for termination");
        }

    }





    @Override
    protected javafx.concurrent.Task createTask() {
        return new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                calTemp();
                System.out.println("cal temp multi konec");
                isOverMain.set(true);
                image.setLastFrame();
                return null;
            }
        };
    }
}