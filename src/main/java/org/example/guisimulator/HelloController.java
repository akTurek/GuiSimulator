package org.example.guisimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HelloController {

    String[] izbira = {"Sekvencno", "Multi"};
    boolean novoRisanje;

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
        System.out.println("Sirina "+image.getWidth()+" visina "+image.getHeight());
    }

    @FXML
    public void zacbiSim() {
        if (novoRisanje == true){
            new CanvasRedrawHandler().stop();
        }
        novoRisanje = true;
        new CanvasRedrawHandler().start();
    }

    private class CanvasRedrawHandler extends AnimationTimer {
        long time = System.nanoTime();
        Sekvencno sekvencno;
        Multi multi;

        @Override
        public void handle(long now) {
        String bizbira = choiceBox.getValue();


        switch (bizbira) {

            case "Sekvencno":
                if (sekvencno == null && novoRisanje) {
                    int row = Integer.parseInt(slable.getText());
                    int col = Integer.parseInt(vlable.getText());
                    int hs = Integer.parseInt(hslable.getText());
                    String nacin = choiceBox.getValue();

                    sekvencno = new Sekvencno(row, col, hs, image, lock, rendered);
                    sekvencno.start();

                }

                if (sekvencno != null && novoRisanje ) {
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
                    novoRisanje = false;
                    System.out.println("konec z racunananjem");
                    racunanje.setText("Konec");
                    System.out.println("Sekvencno je končano ali še ni bilo začeto izvajanje.");
                }


                break;

            case "Multi":
                if (multi == null && novoRisanje) {
                    int row = Integer.parseInt(slable.getText());
                    int col = Integer.parseInt(vlable.getText());
                    int hs = Integer.parseInt(hslable.getText());
                    String nacin = choiceBox.getValue();

                    multi = new Multi(row, col, hs,4, image, lock, rendered);
                    multi.start();

                }

                if (multi != null && novoRisanje ) {
                    if (lock.tryLock()) {
                        try {
                            System.out.println("sem v kljucavnici");
                            multi.canvas.snapshot(null, image);
                            Platform.runLater(() -> imageView.setImage(image));
                            System.out.println("Canvas rendered, waiting for next update");
                            rendered.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    }
                } else {
                    System.out.println("Unlocking in Redraw");
                    //novoRisanje = false;

                }

        }
            long elapsedTime = (System.nanoTime() - time) / 1_000_000;
           // System.out.println("Time since last redraw: " + elapsedTime + " ms");
            time = System.nanoTime();


        }
    }



}
