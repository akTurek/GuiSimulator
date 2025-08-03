package org.example.guisimulator;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

public class BuffImage {

    private final int col;
    private final int row;

    private final AtomicBoolean full = new AtomicBoolean(false);
    private final AtomicBoolean isOver = new AtomicBoolean(false);
    private boolean lastFramePending = false;
    private boolean lastFrameShown = false;
  ;

    private Color[][] barva;
    private Color[][] barva2;
    private Color[][] lastFrame;
    private final ReentrantLock lock = new ReentrantLock();

    public BuffImage(int col, int row) {
        this.col = col;
        this.row = row;
        this.barva = new Color[row][col];
        this.barva2 = new Color[row][col];

        // Inicializacija barv (za varnost)
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                barva[i][j] = Color.BLACK;
                barva2[i][j] = Color.BLACK;
            }
        }
    }

    public WritableImage getDisplayImage() {
        lock.lock();
        try {
            WritableImage display;
            if (lastFramePending){
                display = draw(lastFrame);
                full.set(false);
                lastFrameShown = true;
                System.out.println("Last frame has been shown");
            } else {
                display = draw(barva);
                full.set(false);
            }
            return display;
        } finally {
            lock.unlock();
        }
    }

    public boolean lastFrameDisplayed() {
        lock.lock();
        try {
            return lastFrameShown;
        } finally {
            lock.unlock();
        }
    }

    public Color[][] getDrawImage() {
        return barva2;
    }

    public boolean getFull() {
        return full.get();
    }

    public void submitDrawnImage() {
        lock.lock();
        try {
            if (!full.get()) {
                Color[][] temp = barva;
                barva = barva2;
                barva2 = temp;
                full.set(true);
            }
        } finally {
            lock.unlock();
        }
    }

    public void setLastFrame() {
        lock.lock();
        try {
            Color[][] temp = barva;
            lastFrame = barva2;
            barva2 = temp;
            lastFramePending = true;
            full.set(true);
        } finally {
            lock.unlock();
        }
    }

    public WritableImage draw(Color[][] image) {
        WritableImage nowImage = new WritableImage(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                Color color = image[i][j];
                if (color == null) color = Color.BLACK;
                nowImage.getPixelWriter().setColor(i, j, color);
            }
        }
        return nowImage;
    }
}
