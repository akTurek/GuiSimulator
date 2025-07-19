package org.example.guisimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.concurrent.atomic.AtomicBoolean;


public class HelloController {

    String[] izbira = {"Sekvencno", "Multi GUI"};
    public AtomicBoolean isOverMain = new AtomicBoolean(true);
    public AtomicBoolean emptyBuff = new AtomicBoolean(true);



    @FXML
    WritableImage image;
    @FXML
    VBox vBox;
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
    ImageView imageView;

    public Sekvencno sekvencno;
    public Multi multi;
    private AnimationTimer animationTimer;

    public HelloController() {
    }

    @FXML
    private void initialize() {
        choiceBox.getItems().addAll(izbira);
        choiceBox.setValue(izbira[0]);

        racunanje.setText("Zacni simulacijo");
        image = new WritableImage(600, 600);
        PixelWriter pixelWriter = image.getPixelWriter();

        // Nastavite vse piksle na modro
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixelWriter.setColor(x, y, Color.BLUE); // Nastavite modro barvo
            }
        }
        imageView.setImage(image);

        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(pane.widthProperty().subtract(vBox.widthProperty()));
        imageView.fitHeightProperty().bind(pane.heightProperty());



        slable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() || newValue.equals("0")) {
                Platform.runLater(() -> slable.setText("100")); // Nastavi vrednost na 100
                System.out.println("Polje je bilo prazno ali 0, nastavitev na 100.");
            }


        });

        vlable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() || newValue.equals("0")) {
                Platform.runLater(() -> vlable.setText("100")); // Nastavi vrednost na 100
                System.out.println("Polje je bilo prazno ali 0, nastavitev na 100.");
            }

        });

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!emptyBuff.get()) {
                    System.out.println("///////////////////////////risem");
                    sekvencno.canvas.snapshot(null, image);
                    Platform.runLater(() -> imageView.setImage(image));
                    emptyBuff.set(true);
                    System.out.println("///////////////////////////narisal");
                }

                if (isOverMain.get()) {
                    stop();  // Ustavi AnimationTimer, ko je isOverMain = true
                    System.out.println("AnimationTimer ustavljen, simulacija končana.");
                    toggleUI(false);
                }
            }
        };

        System.out.println("Sirina image " + image.getWidth() + " visina " + image.getHeight());

    }






    private void toggleUI(boolean disable) {
        slable.setDisable(disable);
        vlable.setDisable(disable);
        hslable.setDisable(disable);
        choiceBox.setDisable(disable);
        start.setDisable(disable);

    }




    @FXML
    public void zacbiSim() {
        if (isOverMain.get() == true) {
            isOverMain.set(false);
            racunanje.setText("Racunanje");
            toggleUI(true);
            render();
            startCal();

        } else {

            racunanje.setText("Zacni Simulacijo");
        }

    }

    public void startCal() {
        String bizbira = choiceBox.getValue();
        int row = Integer.parseInt(slable.getText());
        int col = Integer.parseInt(vlable.getText());
        int hs = Integer.parseInt(hslable.getText());
        WritableImage newImage = new WritableImage((col), (row));
        image = newImage;

        switch (bizbira) {

            case "Sekvencno":
                sekvencno = new Sekvencno(row, col, hs, isOverMain, image, emptyBuff);
                sekvencno.start();
                break;
        }
    }

    public void render() {
        animationTimer.start();

    }


}
