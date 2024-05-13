package org.example.guisimulator;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Multi {
    public MatrikaCelic matrikaCelic;
    public int numberOfThreads;
    public int[] wait;
    public int[] areAllTasksOverArr;


    public Multi(int row, int col, int numOfHeat, double time, int numberOfThreads) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat, time);
        this.numberOfThreads = numberOfThreads;
        this.wait = new int[numberOfThreads];
        this.areAllTasksOverArr = new int[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            wait[i] = 0;
            areAllTasksOverArr[i] = 0;
        }
    }


    public void calTemp() {

        long t0 = System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Submit tasks to the thread pool
        for (int i = 0; i < numberOfThreads; i++) {
            // Each task operates on the same shared object
            Runnable task = new Task(this, i, cyclicBarrier);
            executorService.submit(task);
        }

        // Shutdown the thread pool after all tasks are completed
        executorService.shutdown();

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while waiting for termination");
        }

        long t1 = System.currentTimeMillis();
        System.out.println("Trajanje programa v ms: " + (t1 - t0));
    }
}





