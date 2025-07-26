package org.example.guisimulator;


import javafx.concurrent.Service;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Multi  extends Service {
    public MatrikaCelic matrikaCelic;

    public int xsirina;
    public int ysirina;
    public int numberOfThreads =7;
    public AtomicBoolean isOver;
    public BuffImage image;
    public WritableImage nowImage;

    public Multi(int row, int col, int numOfHeat, AtomicBoolean isOver, BuffImage image) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.xsirina = 1;
        this.ysirina = 1;
        this.image = image;
        this.isOver = isOver;

    }

    public void calTemp() throws InterruptedException {


        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Runnable reset = () -> {
            this.isOver.set(true);
            this.nowImage = image.getDrawImage();
        };

        Runnable publish = () -> {
            this.image.submitDrawnImage(nowImage);
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
                isOver.set(true);
                image.setIsOver();
                return null;
            }
        };
    }
}