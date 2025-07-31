package org.example.guisimulator;



import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class BuffImage {

    private final int col;
    private final int row;

    private final AtomicBoolean full = new AtomicBoolean(false);
    private final AtomicBoolean isOverBuffImage = new AtomicBoolean(false);

    private Color[][] barva;
    private Color[][] barva2;
    private final ReentrantLock lock = new ReentrantLock();  // Dodano zaklepanje

    public BuffImage(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public WritableImage getDisplayImage() {
        WritableImage disply = draw();
        return disply;
    }

    public boolean getFull() {
        return full.get();
    }

    public boolean markDisplayed() {
        lock.lock();  // Zaklepanje pred spremembo stanja
        try {
            full.set(false);

            if (!full.get() && isOverBuffImage.get()) {
                full.set(false);
                return true;
            }

            return false;
        } finally {
            lock.unlock();  // Odklepanje po uporabi
        }
    }

    public void submitDrawnImage(Color[][] image) {
        lock.lock();  // Zaklepanje pred spremembo stanja
        try {
            if (!full.get()) {
                barva = image;
                full.set(true);
            }
        } finally {
            lock.unlock();  // Odklepanje po uporabi
        }
    }

    public void setIsOver() {
        isOverBuffImage.set(true);
    }

    public boolean isImageReadyToDisplay() {
        return full.get() && isOverBuffImage.get();
    }

    public WritableImage draw() {
        WritableImage nowImage = new WritableImage(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                nowImage.getPixelWriter().setColor(i, j, barva[i][j]);
            }
        }

        return nowImage;
    }
}

