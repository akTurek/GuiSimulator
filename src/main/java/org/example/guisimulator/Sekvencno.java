package org.example.guisimulator;

public class Sekvencno {
    public MatrikaCelic matrikaCelic;
    public HelloController helloController;

    public Sekvencno(int row, int col, int numOfHeat, int time, HelloController controller){
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat, time);
        this.helloController= controller;

    }

    public MatrikaCelic getMatrikaCelic() {
        return matrikaCelic;
    }

    public void  calTemp() {
        long t0 = System.currentTimeMillis();
        int rows = matrikaCelic.getRow();
        int cols = matrikaCelic.getCol();
        do{
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrikaCelic.calPrevTemp(i,j, rows, cols);

                }
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrikaCelic.calNowTemp(i,j, rows, cols);
                    helloController.pobarvajKvadartCanvas(i, j, matrikaCelic.getCelica(i,j).getNowTemp());
                }
            }



        }while (!isOver(rows, cols));
        long t1 = System.currentTimeMillis();
        System.out.println("Trajanje programa v ms: " +(t1-t0)+ " max temp change "+maxTempChanfe(rows, cols));
    }
    public boolean isOver(int rows, int cols){
        float maxTempChange = 0;
        float change;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                change = matrikaCelic.tempChange(i,j);
                if (change>maxTempChange){
                    maxTempChange = change;
                }
            }
        }
        // System.out.println(maxTempChange);
        if (maxTempChange >= 0.25) {
            return false;
        }else {
            return true;
        }
    }

    public float maxTempChanfe(int rows, int cols){
        float maxTempChange = 0;
        float change = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                change = matrikaCelic.tempChange(i, j);
                if (change >= maxTempChange) {
                    maxTempChange = change;

                }
            }
        }

        return maxTempChange;
    }
}
