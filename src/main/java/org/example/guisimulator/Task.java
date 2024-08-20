package org.example.guisimulator;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javafx.scene.paint.Color;
class Task implements Runnable {
    private Multi multi;
    private int taskId;
    private int startRow;
    private int endRow;
    private int rows, cols;
    public CyclicBarrier cyclicBarrier;


    public Task(Multi multi, int taskId, CyclicBarrier cyclicBarrier) {
        this.multi = multi;
        this.taskId = taskId;
        this.startRow = taskId * (multi.matrikaCelic.row) / (multi.numberOfThreads);
        this.endRow = Math.min((taskId + 1) * (multi.matrikaCelic.row) / (multi.numberOfThreads), multi.matrikaCelic.row);
        this.rows = multi.matrikaCelic.row;
        this.cols = multi.matrikaCelic.col;
        this.cyclicBarrier = cyclicBarrier;

    }



    public void callTemp(){
        float change;
        float maxChange= 0.F;;

            //System.out.println("racunam "+taskId);
            //calPrevTemp

            for (int k = startRow; k < endRow; k++) {
                for (int j = 0; j < multi.matrikaCelic.col; j++) {
                    multi.matrikaCelic.calPrevTemp(k, j);
                    //System.out.println("racunam "+taskId);
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
                    multi.matrikaCelic.calNowTemp(i, j);
                    change = multi.matrikaCelic.getTempChange(i,j);
                    if (change > maxChange) {
                        maxChange = change;
                    }
                }
            }

            if (maxChange > 0.25){
                multi.isOver.set(false);
            }


            //Barrier
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }
            //System.out.println("max temp change: " + maxChange+ " is over "+ multi.isOver.get()+" thread "+taskId);
    }





    @Override
    public void run() {
        callTemp();
    }

}
