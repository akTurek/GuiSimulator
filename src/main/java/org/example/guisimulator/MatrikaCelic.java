package org.example.guisimulator;

import java.util.Random;


public class MatrikaCelic {
    final int row;
    final int col;
    final int numOfHeat;
    final double time;
    private int[][] heatSources;
    private Celica[][] matrikaCelic;

    public MatrikaCelic(int row, int col, int numOfHeat, double time) {
        this.row = row;
        this.col = col;
        this.numOfHeat = numOfHeat;
        this.time = time * 1000;
        this.heatSources = new int[numOfHeat][2];
        this.matrikaCelic = narediMatriko();

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

    private Celica[][] narediMatriko() {
        Celica[][] m = new Celica[this.row][this.col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                m[i][j] = new Celica(0.0F, 0.0F, false);
            }
        }
        Random rand = new Random();
        int count = 0;
        for (int i = 0; i < this.numOfHeat; i++) {
            int randomRow = rand.nextInt(m.length);
            int randomCol = rand.nextInt(m[0].length);

            m[randomRow][randomCol].setHeatSource(true);
            m[randomRow][randomCol].setPreTemp(100);
            m[randomRow][randomCol].setNowTemp(100);
            //System.out.println(randomCol+" "+randomRow+" "+m[randomRow][randomCol].getPreTemp());

            this.heatSources[count][0] = randomRow;
            this.heatSources[count][1] = randomCol;
            count++;
        }
        return m;
    }

    public boolean setFallsHeatsources(long timestamp) {
        if ((System.currentTimeMillis() - timestamp) >= time) {

            for (int i = 0; i < numOfHeat; i++) {
                this.matrikaCelic[heatSources[i][0]][heatSources[i][1]].setHeatSource(false);

            }
            return true;
        }
        return false;
    }

    public void printMatriko() {
        System.out.print("   ");
        for (int i = 0; i < col; i++) {
            System.out.print(i + " ");

        }
        System.out.println();
        for (int h = 0; h < row; h++) {

            System.out.print(h + "  ");
            for (int k = 0; k < col; k++) {
                System.out.print((int) this.matrikaCelic[h][k].getNowTemp() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void calNowTemp(int i, int j, int rows, int cols) {
        if (!matrikaCelic[i][j].isHeatSource) { // Check if it's not a heat source
            if (i == 0 && j == 0) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j + 1].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp()) / 2);
            } else if (i == 0 && j == cols - 1) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j - 1].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp()) / 2);
            } else if (i == rows - 1 && j == 0) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j + 1].getPreTemp() + matrikaCelic[i - 1][j].getPreTemp()) / 2);
            } else if (i == rows - 1 && j == cols - 1) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j - 1].getPreTemp() + matrikaCelic[i - 1][j].getPreTemp()) / 2);
            } else if (i == 0) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j - 1].getPreTemp() + matrikaCelic[i][j + 1].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp()) / 3);
            } else if (j == 0) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i - 1][j].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp() + matrikaCelic[i][j + 1].getPreTemp()) / 3);
            } else if (i == rows - 1) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i][j - 1].getPreTemp() + matrikaCelic[i][j + 1].getPreTemp() + matrikaCelic[i - 1][j].getPreTemp()) / 3);
            } else if (j == cols - 1) {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i - 1][j].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp() + matrikaCelic[i][j - 1].getPreTemp()) / 3);
            } else {
                matrikaCelic[i][j].setNowTemp((matrikaCelic[i - 1][j].getPreTemp() + matrikaCelic[i + 1][j].getPreTemp() + matrikaCelic[i][j - 1].getPreTemp() + matrikaCelic[i][j + 1].getPreTemp()) / 4);
            }

        }
    }

    public void calPrevTemp(int i, int j, int rows, int cols) {
        if (!matrikaCelic[i][j].isHeatSource) { // Check if it's not a heat source
            if (i == 0 && j == 0) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j + 1].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp()) / 2);
            } else if (i == 0 && j == cols - 1) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j - 1].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp()) / 2);
            } else if (i == rows - 1 && j == 0) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j + 1].getNowTemp() + matrikaCelic[i - 1][j].getNowTemp()) / 2);
            } else if (i == rows - 1 && j == cols - 1) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j - 1].getNowTemp() + matrikaCelic[i - 1][j].getNowTemp()) / 2);
            } else if (i == 0) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j - 1].getNowTemp() + matrikaCelic[i][j + 1].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp()) / 3);
            } else if (j == 0) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i - 1][j].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp() + matrikaCelic[i][j + 1].getNowTemp()) / 3);
            } else if (i == rows - 1) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i][j - 1].getNowTemp() + matrikaCelic[i][j + 1].getNowTemp() + matrikaCelic[i - 1][j].getNowTemp()) / 3);
            } else if (j == cols - 1) {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i - 1][j].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp() + matrikaCelic[i][j - 1].getNowTemp()) / 3);
            } else {
                matrikaCelic[i][j].setPreTemp((matrikaCelic[i - 1][j].getNowTemp() + matrikaCelic[i + 1][j].getNowTemp() + matrikaCelic[i][j - 1].getNowTemp() + matrikaCelic[i][j + 1].getNowTemp()) / 4);
            }
        }
    }

    public float tempChange(int i, int j) {
        return Math.abs(matrikaCelic[i][j].getNowTemp() - matrikaCelic[i][j].getPreTemp());
    }

    public float[] matrikaToArrayPrevTemp() {
        float[] arrayPrevTemp = new float[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayPrevTemp[i * col + j] = matrikaCelic[i][j].getPreTemp();
            }
        }

        return arrayPrevTemp;
    }

    public float[] matrikaToArrayNowTemp() {
        float[] arrayNowTemp = new float[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayNowTemp[i * col + j] = matrikaCelic[i][j].getNowTemp();
            }
        }

        return arrayNowTemp;
    }

    public boolean[] matrikaToArrayIsHeatSource() {
        boolean[] arrayIsHeatSourc = new boolean[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayIsHeatSourc[i * col + j] = matrikaCelic[i][j].isHeatSource;
            }
        }

        return arrayIsHeatSourc;
    }

    public void arraysToMatrika(float[] arrayNowTemp, float[] arrayPrevTemp) {

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                matrikaCelic[i][j].setPreTemp(arrayPrevTemp[i * col + j]);
                matrikaCelic[i][j].setNowTemp(arrayNowTemp[i * col + j]);
            }
        }

    }

    public static Celica[][] matrikaToMejneVrednosti(Celica[][] matrikaCelic, int rows, int cols, int size) {
        int rowsPerProcess = rows / size;
        int stMejnihRows = (size * 2) - 2;

        int startRow = 0;
        int endRow = startRow + rowsPerProcess - 1;

        Celica[][] mejneVrednosti = new Celica[stMejnihRows][cols];

        mejneVrednosti[0] = matrikaCelic[endRow];
        startRow = startRow + rowsPerProcess;
        endRow = endRow + rowsPerProcess;

        for (int i = 1; i < stMejnihRows - 1; i += 2) {
            mejneVrednosti[i] = matrikaCelic[startRow];
            mejneVrednosti[i + 1] = matrikaCelic[endRow];
            startRow += rowsPerProcess;
            endRow += rowsPerProcess;
        }

        mejneVrednosti[stMejnihRows - 1] = matrikaCelic[startRow];

        return mejneVrednosti;
    }

}
