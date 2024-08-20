package org.example.guisimulator;
import javafx.scene.paint.Color;
import javafx.scene.paint.Color;

import java.util.Random;


public class MatrikaCelic {
    final int row;
    final int col;
    final int numOfHeat;

    private float [][] prevTemp;
    private float [][] nowTemp;
    private boolean [][] isHeatSource;
    Color [] barva = new Color[101];

    public MatrikaCelic(int row, int col, int numOfHeat) {
        this.row = row;
        this.col = col;
        this.numOfHeat = numOfHeat;
        this.prevTemp = new float[row][col];
        this.nowTemp = new float[row][col];
        this.isHeatSource = new boolean[row][col];
        narediMatriko();
        arrayBrav();

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    public Color getCol(int i, int j) {
        return barva[(int) nowTemp[i][j]];
    }

    private void narediMatriko() {

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == 0 || i == row - 1 || j == 0 || j == col - 1){
                    prevTemp[i][j] = 0.F;
                    nowTemp[i][j] = 0.F;
                    isHeatSource[i][j] = true; //robi so 0C ampak  heat sourci, da se jih ne racuna
                }else {
                    prevTemp[i][j] = 0.F;
                    nowTemp[i][j] = 0.F;
                    isHeatSource[i][j] = false;
                }
            }
        }

        Random rand = new Random();
        int count = 0;
        while (count < numOfHeat) {
            int randomRow = rand.nextInt(row);
            int randomCol = rand.nextInt(col);
            if (!isHeatSource[randomRow][randomCol]) {
                prevTemp[randomRow][randomCol] = 100.F;
                nowTemp[randomRow][randomCol] = 100.F;
                isHeatSource[randomRow][randomCol] = true;
                count++;
            }
        }
    }


    public void calNowTemp(int i, int j) {
        if (!isHeatSource[i][j]) {
            nowTemp[i][j] = (prevTemp[i - 1][j]+ prevTemp[i + 1][j] + prevTemp[i][j + 1] + prevTemp[i][j - 1])/4;
        }
    }

    public void calPrevTemp(int i, int j) {
        if (!isHeatSource[i][j]) {
            prevTemp[i][j] = (nowTemp[i - 1][j]+ nowTemp[i + 1][j] + nowTemp[i][j + 1] + nowTemp[i][j - 1])/4;
        }
    }

    public float getTempChange(int i, int j){
        return Math.abs(nowTemp[i][j] - prevTemp[i][j]);
    }

    public void arrayBrav() {
        for (int i = 0; i <= 100; i++) {
            if (i < 25) { // Temno modra do svetlo modra
                double r = 0;
                double g = 0;
                double b = 0.545 + (0.455 / 25.0) * i; // Spreminjanje modre komponente
                barva[i] = Color.color(r, g, b);
            } else if (i < 50) { // Svetlo modra do zelena
                double r = 0;
                double g = (1.0 / 25.0) * (i - 25); // Spreminjanje zelene komponente
                double b = 1.0 - (1.0 / 25.0) * (i - 25); // Spreminjanje modre komponente
                barva[i] = Color.color(r, g, b);
            } else if (i < 75) { // Zelena do rumena
                double r = (1.0 / 25.0) * (i - 50); // Spreminjanje rdeče komponente
                double g = 1.0;
                double b = 0;
                barva[i] = Color.color(r, g, b);
            } else { // Rumena do rdeča
                double r = 1.0;
                double g = 1.0 - (1.0 / 25.0) * (i - 75); // Spreminjanje zelene komponente
                double b = 0;
                barva[i] = Color.color(r, g, b);
            }
        }
    }

}
