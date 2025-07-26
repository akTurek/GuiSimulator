package org.example.guisimulator;

import javafx.scene.image.WritableImage;

import java.util.concurrent.atomic.AtomicBoolean;

public class BuffImage {

    private final int col;
    private final int row;

    private final AtomicBoolean full = new AtomicBoolean(false);
    private final AtomicBoolean isOverBuffImage = new AtomicBoolean(false);

    private volatile WritableImage displayImage;
    private volatile WritableImage nextImage;

    public BuffImage(int col, int row) {
        this.col = col;
        this.row = row;
        this.displayImage = null;
        this.nextImage = null;
    }

    public WritableImage getDisplayImage() {
        if (full.get()) {
            return displayImage;
        } else {
            return null;
        }
    }


    public  boolean markDisplayed() {
        full.set(false);

        if (nextImage != null) {
            displayImage = nextImage;
            nextImage = null;
            full.set(true);
        }

        return (!full.get() && isOverBuffImage.get());
    }

    public WritableImage getDrawImage() {
        return new WritableImage(col, row);
    }


    public void submitDrawnImage(WritableImage image) {
        if (!full.get()) {
            displayImage = image;
            full.set(true);
        } else {
            nextImage = image;
        }
    }

    public void setIsOver() { //to nastavi risalna nit da je koncala
        isOverBuffImage.set(true);
    }
}
