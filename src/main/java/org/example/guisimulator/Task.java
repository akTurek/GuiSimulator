package org.example.guisimulator;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Task implements Runnable {
    private Multi multi;
    private int taskId;
    private int startRow;
    private int endRow;
    private int rows, cols;
    CyclicBarrier cyclicBarrier;

    public Task(Multi multi, int taskId, CyclicBarrier cyclicBarrier) {
        this.multi = multi;
        this.taskId = taskId;
        this.startRow = taskId * (multi.matrikaCelic.row) / (multi.numberOfThreads);
        this.endRow = Math.min((taskId + 1) * (multi.matrikaCelic.row) / (multi.numberOfThreads), multi.matrikaCelic.row);
        this.rows = multi.matrikaCelic.row;
        this.cols = multi.matrikaCelic.col;
        this.cyclicBarrier = cyclicBarrier;
    }

    public boolean lock(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public float maxTempChanfe() {
        float maxTempChange = 0;
        float change = 0;
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < cols; j++) {
                change = multi.matrikaCelic.tempChange(i, j);
                if (change >= maxTempChange) {
                    maxTempChange = change;

                }
            }
        }

        return maxTempChange;
    }

    public void isOver() {
        float maxTempChange = maxTempChanfe();
        //System.out.println(" temperaturna sprememba " +maxTempChange+" Threda "+taskId);
        if (maxTempChange >= 0.25) {
            multi.areAllTasksOverArr[taskId] = 0;
        } else {
            multi.areAllTasksOverArr[taskId] = 1;
        }
    }

    private synchronized void waitForCondition() {
        multi.wait[this.taskId] = 1;
        while (lock(multi.wait)) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted");
            }
        }
        System.out.println("Lock opend");
        multi.wait[this.taskId] = 0;
        notifyAll();
    }

    @Override
    public void run() {

        System.out.println("Task " + taskId + " is being processed by thread " + Thread.currentThread().getName() +
                " My startRow " + startRow + " My endRow " + endRow);
        do {
            //calPrevTemp
            for (int k = startRow; k < endRow; k++) {
                for (int j = 0; j < multi.matrikaCelic.col; j++) {
                    multi.matrikaCelic.calPrevTemp(k, j, rows, cols);
                }
            }

            //Barrier
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }

            //calNowTemp
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < cols; j++) {
                    multi.matrikaCelic.calNowTemp(i, j, rows, cols);
                }
            }
            //Barrier
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }
            //max sprememba temp <0.25
            isOver();

            //Barrier
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }
            //System.out.println();


            System.out.println(
                    " Nit  " + taskId + " tocka " + 2 + " ima temperaturo " + multi.matrikaCelic.getMatrikaCelic()[5][2].getNowTemp());
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }


            System.out.println();


        } while (!lock(multi.areAllTasksOverArr));


    }

}
