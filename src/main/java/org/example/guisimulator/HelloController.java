package org.example.guisimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HelloController {

    String[] izbira = {"Sekvencno", "Multi"};
    public AtomicBoolean novoRisanje = new AtomicBoolean();

    private final Lock lock = new ReentrantLock();
    private final Condition rendered = lock.newCondition();

    @FXML
    WritableImage image = new WritableImage(800, 800);

    @FXML
    TextField slable;
    @FXML
    TextField vlable;
    @FXML
    TextField hslable;
    @FXML
    Button start;

    @FXML
    AnchorPane pane;
    @FXML
    Text racunanje;
    @FXML
    ChoiceBox<String> choiceBox;
    @FXML
    ImageView imageView = new ImageView(image);

    public HelloController() {
    }

    @FXML
    private void initialize() {
        choiceBox.getItems().addAll(izbira);
        choiceBox.setValue(izbira[1]);
        System.out.println("Sirina "+image.getWidth()+" visina "+image.getHeight());
        racunanje.setText("Zacni simulacijo");
    }

    @FXML
    public void zacbiSim() {
        if (novoRisanje.get() == false){
            novoRisanje.set(true);
            new CanvasRedrawHandler().start();
            racunanje.setText("Racunanje");

        } else {
            new CanvasRedrawHandler().stop();
            new CanvasRedrawHandler().start();
            racunanje.setText("Racunanje");
        }

    }


    private class CanvasRedrawHandler extends AnimationTimer {
        public long time = System.currentTimeMillis();
        public Sekvencno sekvencno;
        public Multi multi;
        public long timeS;

        @Override
        public void handle(long now) {
        String bizbira = choiceBox.getValue();


        switch (bizbira) {

            case "Sekvencno":
                if (sekvencno == null && novoRisanje.get()) {
                    int row = Integer.parseInt(slable.getText());
                    int col = Integer.parseInt(vlable.getText());
                    int hs = Integer.parseInt(hslable.getText());
                    String nacin = choiceBox.getValue();

                    timeS = System.currentTimeMillis();
                    sekvencno = new Sekvencno(row, col, hs, image, lock, rendered, novoRisanje);
                    sekvencno.start();

                }

                if (sekvencno != null && novoRisanje.get()) {
                    if (lock.tryLock()) {
                        try {
                            System.out.println("Rendering canvas");
                            sekvencno.canvas.snapshot(null, image);
                            Platform.runLater(() -> imageView.setImage(image));
                            rendered.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                } else {
                    novoRisanje.set(false);
                    long l = System.currentTimeMillis() - timeS;
                    racunanje.setText("Konec, cas simulacije: "+l+" ms");
                    this.stop();
                }

                break;

            case "Multi":
                if (multi == null && novoRisanje.get()) {
                    int row = Integer.parseInt(slable.getText());
                    int col = Integer.parseInt(vlable.getText());
                    int hs = Integer.parseInt(hslable.getText());
                    String nacin = choiceBox.getValue();

                    multi = new Multi(row, col, hs,4, image, lock, rendered, novoRisanje);
                    multi.start();
                    timeS = System.currentTimeMillis();

                }

                if (multi != null && novoRisanje.get()) {
                    if (lock.tryLock()) {
                        try {
                            System.out.println("Rendering canvas");
                            multi.canvas.snapshot(null, image);
                            Platform.runLater(() -> imageView.setImage(image));
                            System.out.println("Canvas rendered, waiting for next update");
                            rendered.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    }
                } else {
                    long l = System.currentTimeMillis() - timeS;
                    racunanje.setText("Konec, cas simulacije: "+l+" ms");
                    novoRisanje.set(false);
                    this.stop();
                }

        }
            long elapsedTime = (System.nanoTime() - time) / 1_000_000;
            System.out.println("Time since last redraw: " + elapsedTime + " ms");
            time = System.nanoTime();


        }
    }



}
