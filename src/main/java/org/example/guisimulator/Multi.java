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
import java.util.concurrent.locks.ReentrantLock;

public class Multi  extends Service {
    public MatrikaCelic matrikaCelic;
    public int numberOfThreads;
    public AtomicBoolean isOver;


    public Canvas canvas;
    public Lock lock;
    public Condition rendered;



    public GraphicsContext gc;
    public int xsirina;
    public int ysirina;
    public int rows, cols;

    public AtomicBoolean konec;


    public Multi(int row, int col, int numOfHeat, int numberOfThreads, WritableImage image, Lock lock, Condition rendered, AtomicBoolean konec) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.numberOfThreads = numberOfThreads;
        this.isOver = new AtomicBoolean(false);
        this.rows = row;
        this.cols = col;
        xsirina = (int) Math.round(image.getHeight() / row);
        ysirina = (int) Math.round(image.getWidth()/ col);
        this.canvas = new Canvas(image.getWidth(), image.getHeight());
        this.gc = canvas.getGraphicsContext2D();
        gc.setFill(matrikaCelic.getCol(0,0));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        this.lock = lock;
        this.rendered = rendered;

        this.konec = konec;

    }

    public boolean lockIsOver(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return false;
            }
        }
        return true;
    }



    public void calTemp() throws InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        do {
            lock.lock();
            isOver.set(true);
            try {
                for (int i = 0; i < numberOfThreads; i++) {
                    //Nad skupnim objektom brez konflikta, druge lokacijem, ali pa pregrada
                    Task task = new Task(this, i, cyclicBarrier);
                    executorService.submit(task);
                }

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        gc.setFill(matrikaCelic.getCol(i, j));
                        gc.fillRect(i * xsirina, j * ysirina, xsirina, ysirina);
                    }
                }

                rendered.await();
            }finally {
                lock.unlock();
            }

        }while (!isOver.get());


        // Shutdown the thread pool after all tasks are completed
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
                konec.set(false);
                return null;
            }
        };
    }
}





