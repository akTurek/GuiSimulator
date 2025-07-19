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
    public Canvas canvas;
    public Lock lock;
    public Condition rendered;
    public int xsirina;
    public int ysirina;
    public GraphicsContext gc;
    public AtomicBoolean konec;
    public RocnoVneseneTocke list;
    public boolean gui;
    public int numberOfThreads =4;
    public AtomicBoolean isOver;

    //multi = new Multi(row, col, hs, image, lock, rendered, novoRisanje, list, true);

    public Multi(int row, int col, int numOfHeat, WritableImage image, Lock lock, Condition rendered, AtomicBoolean konec, RocnoVneseneTocke list, boolean gui) {
        this.matrikaCelic = new MatrikaCelic(row + 2, col + 2, numOfHeat);
        this.lock = lock;
        this.rendered = rendered;
        this.xsirina = (int) Math.floor(image.getWidth() / (double) col);
        this.ysirina = (int) Math.floor(image.getHeight() / (double) row);
        this.list = list;
        this.canvas = new Canvas(image.getWidth(), image.getHeight());
        this.gc = canvas.getGraphicsContext2D();
        //gc.setFill(matrikaCelic.getCol(0, 0));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.konec = konec;
        this.gui = gui;
        this.isOver = new AtomicBoolean(false);


    }

    public void calTemp() throws InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        Runnable reset = () -> this.isOver.set(true);
        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(numberOfThreads, reset);

        int rows = matrikaCelic.getRow();
        int cols = matrikaCelic.getCol();

        for (int i = 0; i < numberOfThreads; i++) {

            Task task = new Task(this, i, cyclicBarrier,cyclicBarrierStart,gc,xsirina,ysirina);
            executorService.submit(task);
        }


        do {
            lock.lock();
            isOver.set(true);

            try {
                rendered.await();
            } finally {
                lock.unlock();
            }

        }while (!isOver.get());


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
                konec.set(false);
                return null;
            }
        };
    }
}