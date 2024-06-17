package org.example.guisimulator;
import javafx.scene.paint.Color;

import java.util.Random;


public class MatrikaCelic {
    final int row;
    final int col;
    final int numOfHeat;
    private int[][] heatSources;
    private Celica[][] matrikaCelic;
    Color [] barva = new Color[101];

    public MatrikaCelic(int row, int col, int numOfHeat) {
        this.row = row;
        this.col = col;
        this.numOfHeat = numOfHeat;
        this.heatSources = new int[numOfHeat][2];
        this.matrikaCelic = narediMatriko();
        arrayBrav();

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Celica[][] getMatrikaCelic() {
        return matrikaCelic;
    }

    public Celica getCelica(int i, int j) {
        return matrikaCelic[i][j];
    }

    public Color getCol(int i, int j) {
        return barva[(int) matrikaCelic[i][j].getNowTemp()];
    }

    private void arrayBrav() {
        double normalizedValue;
        for (int i = 0; i <= 100; i++) {
            normalizedValue = i / 100.0;
            if (i < 50) {
                barva[i] = Color.color(0, normalizedValue * 2, 1);
            } else {
                barva[i] = Color.color(1, 1 - (normalizedValue / 2), 1);
            }
        }
    }

    private Celica[][] narediMatriko() {
        Celica[][] m = new Celica[this.row][this.col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == 0 || i == row - 1 || j == 0 || j == col - 1){
                    m[i][j] = new Celica(0.0F, 0.0F, true);  //robi so 0C ampak  heat sourci, da se jih ne racuna
                }else {
                    m[i][j] = new Celica(0.0F, 0.0F, false);
                }
            }
        }

        Random rand = new Random();
        int count = 0;
        while (count < numOfHeat) {
            int randomRow = rand.nextInt(m.length);
            int randomCol = rand.nextInt(m[0].length);
            if (!m[randomRow][randomCol].isHeatSource) {
                m[randomRow][randomCol].setHeatSource(true);
                m[randomRow][randomCol].setPreTemp(100);
                m[randomRow][randomCol].setNowTemp(100);
                this.heatSources[count][0] = randomRow;
                this.heatSources[count][1] = randomCol;
                count++;
            }
        }
        return m;
    }




    public void calNowTemp(int i, int j) {
        if (i > 0 && i < row - 1 && j > 0 && j < col - 1) {
            if (!matrikaCelic[i][j].isHeatSource) {
                matrikaCelic[i][j].setNowTemp(
                        (matrikaCelic[i - 1][j].getPreTemp()
                                + matrikaCelic[i + 1][j].getPreTemp()
                                + matrikaCelic[i][j - 1].getPreTemp()
                                + matrikaCelic[i][j + 1].getPreTemp()) / 4);
            }
        }
    }

    public void calPrevTemp(int i, int j) {
        if (i > 0 && i < row - 1 && j > 0 && j < col - 1) {
            if (!matrikaCelic[i][j].isHeatSource) {
                matrikaCelic[i][j].setPreTemp(
                        (matrikaCelic[i - 1][j].getNowTemp()
                                + matrikaCelic[i + 1][j].getNowTemp()
                                + matrikaCelic[i][j - 1].getNowTemp()
                                + matrikaCelic[i][j + 1].getNowTemp()) / 4);
            }
        }
    }

    public float tempChange(int i, int j) {
        return Math.abs(matrikaCelic[i][j].getNowTemp() - matrikaCelic[i][j].getPreTemp());
    }

}
