package org.example.guisimulator;
import java.util.concurrent.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

class Task implements Runnable {
    private Multi multi;
    private int taskId;
    private int startRow;
    private int endRow;
    private int rows, cols;
    public CyclicBarrier cyclicBarrier;
    public CyclicBarrier cyclicBarrierStart;
    int xsirina,ysirina;


    public Task(Multi multi, int taskId, CyclicBarrier cyclicBarrier,CyclicBarrier cyclicBarrierStart) {
        this.multi = multi;
        this.taskId = taskId;
        this.startRow = taskId * (multi.matrikaCelic.row) / (multi.numberOfThreads);
        this.endRow = Math.min((taskId + 1) * (multi.matrikaCelic.row) / (multi.numberOfThreads), multi.matrikaCelic.row);

        this.rows = multi.matrikaCelic.row;
        this.cols = multi.matrikaCelic.col;

        this.cyclicBarrier = cyclicBarrier;
        this.cyclicBarrierStart = cyclicBarrierStart;

        this.xsirina=multi.xsirina;
        this.ysirina=multi.ysirina;

    }



    public void callTemp(){
        float change;
        float maxChange= 0.F;;



        do {


            for (int k = startRow; k < endRow; k++) {
                for (int j = 0; j < multi.matrikaCelic.col; j++) {
                    multi.matrikaCelic.calPrevTemp(k, j);
                }
            }

            //Barrier///////////////////////////////////////////////////////////////////////////////
            try {
                cyclicBarrierStart.await(); //tukaj se klice get image
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }


            maxChange = 0.F;


            //calNowTemp
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < cols; j++) {
                    multi.matrikaCelic.calNowTemp(i, j);
                    change = multi.matrikaCelic.getTempChange(i,j);

                    if (change > maxChange) {
                        maxChange = change;
                    }
                    multi.frame[i][j] = multi.matrikaCelic.getBarva(i,j);
                }
            }
            if (maxChange > 0.25F){
                multi.isOver.set(false);
                //System.out.println(maxChange + " set false "+taskId);
            }


            //Barrier//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try {
                cyclicBarrier.await(); //tukaj se klice commit image
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }


        } while (!multi.isOver.get());

    }



    @Override
    public void run() {
        callTemp();
    }

}