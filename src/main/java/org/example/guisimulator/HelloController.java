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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HelloController {

    String[] izbira = {"Sekvencno GUI", "Sekvencno", "Multi GUI"};
    public AtomicBoolean novoRisanje = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final Condition rendered = lock.newCondition();
    public RocnoVneseneTocke list = new RocnoVneseneTocke();
    private final List<double[]> clickedCells = new LinkedList<>();


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

    public HelloController() {
    }

    @FXML
    private void initialize() {
        choiceBox.getItems().addAll(izbira);
        choiceBox.setValue(izbira[0]);

        racunanje.setText("Zacni simulacijo");
        image = new WritableImage(600, 600);
        redraw();
        imageView.setImage(image);

        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(pane.widthProperty().subtract(vBox.widthProperty()));
        imageView.fitHeightProperty().bind(pane.heightProperty());


        imageView.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            relCordClick(mouseX, mouseY);
            if (novoRisanje.get() == true) {
                convertList();
            }
        });

        slable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() || newValue.equals("0")) {
                Platform.runLater(() -> slable.setText("100")); // Nastavi vrednost na 100
                System.out.println("Polje je bilo prazno ali 0, nastavitev na 100.");
            }

            redraw();
        });

        vlable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() || newValue.equals("0")) {
                Platform.runLater(() -> vlable.setText("100")); // Nastavi vrednost na 100
                System.out.println("Polje je bilo prazno ali 0, nastavitev na 100.");
            }
            redraw();
        });

        System.out.println("Sirina image " + image.getWidth() + " visina " + image.getHeight());

    }


    private void relCordClick(double mouseX, double mouseY) {
        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();
        double displayedWidth = imageView.getFitWidth();
        double displayedHeight = imageView.getFitHeight();
        double scaleX = originalWidth / displayedWidth;
        double scaleY = originalHeight / displayedHeight;
        double offsetX = (imageView.getFitWidth() - displayedWidth) / 2;
        double offsetY = (imageView.getFitHeight() - displayedHeight) / 2;

        double imageX = (mouseX - offsetX) * scaleX;
        double imageY = (mouseY - offsetY) * scaleY;

        if (imageX >= 0 && imageX < originalWidth && imageY >= 0 && imageY < originalHeight) {
            double relativeX = imageX / originalWidth;
            double relativeY = imageY / originalHeight;

            int pixelX = (int) imageX;
            int pixelY = (int) imageY;

            System.out.println("Klik relativno: (" + relativeX + ", " + relativeY + ")");

            Color pixelColor = image.getPixelReader().getColor(pixelX, pixelY);
            boolean isRed = pixelColor.getRed() > 0.8 && pixelColor.getGreen() < 0.2 && pixelColor.getBlue() < 0.2;

            if (isRed) {
                double[] closestPoint = null;
                double minDistance = Double.MAX_VALUE;

                for (double[] cell : clickedCells) {
                    double distance = Math.sqrt(Math.pow(cell[0] - relativeX, 2) + Math.pow(cell[1] - relativeY, 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestPoint = cell;
                    }
                }

                if (closestPoint != null) {
                    hslable.setText(String.valueOf(Integer.parseInt(hslable.getText()) - 1));

                    clickedCells.remove(closestPoint);
                }
            } else {
                clickedCells.add(new double[]{relativeX, relativeY});
                hslable.setText(String.valueOf(Integer.parseInt(hslable.getText()) + 1));
                System.out.println("Točka dodana.");
            }

            redraw();
        }
    }

    @FXML
    private void redraw() {
        PixelWriter pixelWriter = image.getPixelWriter();
        String rowS = slable.getText();
        String colS = vlable.getText();

        if ((rowS.isBlank()) || (colS.isBlank())) {
            return;
        }

        int row = Integer.parseInt(rowS);
        int col = Integer.parseInt(colS);

        if (row == 0 || col == 0) {
            return;
        }

        double cellWidth = image.getWidth() / col;
        double cellHeight = image.getHeight() / row;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixelWriter.setColor(x, y, Color.rgb(0, 0, 254));
            }
        }

        for (double[] point : clickedCells) {
            int cellX = (int) (point[0] * col);
            int cellY = (int) (point[1] * row);

            int startX = (int) (cellX * cellWidth);
            int startY = (int) (cellY * cellHeight);

            for (int x = startX; x < startX + cellWidth && x < image.getWidth(); x++) {
                for (int y = startY; y < startY + cellHeight && y < image.getHeight(); y++) {
                    pixelWriter.setColor(x, y, Color.RED);
                }
            }
        }
    }


    private void convertList() {
        int row = Integer.parseInt(slable.getText());
        int col = Integer.parseInt(vlable.getText());

        for (double[] point : clickedCells) {
            int cellX = (int) (point[0] * col);
            int cellY = (int) (point[1] * row);
            list.insert(cellX, cellY);
        }
        clickedCells.clear();
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
        if (novoRisanje.get() == false) {
            convertList();
            novoRisanje.set(true);
            new CanvasRedrawHandler().start();
            racunanje.setText("Racunanje");
            toggleUI(true);

        } else {
            new CanvasRedrawHandler().stop();
            racunanje.setText("Zacni Simulacijo");
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

                case "Sekvencno GUI":
                    if (sekvencno == null && novoRisanje.get()) {
                        int row = Integer.parseInt(slable.getText());
                        int col = Integer.parseInt(vlable.getText());
                        int hs = Integer.parseInt(hslable.getText());
                        WritableImage newImage = new WritableImage((col + 2) * 6, (row + 2) * 6);
                        image = newImage;

                        timeS = System.currentTimeMillis();
                        sekvencno = new Sekvencno(row, col, hs, image, lock, rendered, novoRisanje, list, true);
                        sekvencno.start();

                    }

                    if (sekvencno != null && novoRisanje.get()) {
                        if (lock.tryLock()) {
                            try {
                                //System.out.println("Rendering canvas");
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
                        racunanje.setText("Konec, cas simulacije: " + l + " ms");
                        Platform.runLater(() -> toggleUI(false));
                        Platform.runLater(() -> hslable.setText("100"));
                        this.stop();
                    }

                    break;

                case "Sekvencno":
                    if (sekvencno == null && novoRisanje.get()) {
                        int row = Integer.parseInt(slable.getText());
                        int col = Integer.parseInt(vlable.getText());
                        int hs = Integer.parseInt(hslable.getText());
                        String nacin = choiceBox.getValue();

                        timeS = System.currentTimeMillis();
                        sekvencno = new Sekvencno(row, col, hs, image, lock, rendered, novoRisanje, list, false);
                        sekvencno.start();

                    }

                    if (sekvencno != null && novoRisanje.get()) {

                    } else {
                        novoRisanje.set(false);
                        long l = System.currentTimeMillis() - timeS;
                        racunanje.setText("Konec, cas simulacije: " + l + " ms");
                        Platform.runLater(() -> toggleUI(false));
                        this.stop();
                    }

                    break;

                case "Multi GUI":
                    if (multi == null && novoRisanje.get()) {
                        int row = Integer.parseInt(slable.getText());
                        int col = Integer.parseInt(vlable.getText());
                        int hs = Integer.parseInt(hslable.getText());
                        String nacin = choiceBox.getValue();

                        timeS = System.currentTimeMillis();
                        multi = new Multi(row, col, hs, image, lock, rendered, novoRisanje, list, true);
                        multi.start();

                    }

                    if (multi != null && novoRisanje.get()) {
                        if (lock.tryLock()) {
                            try {
                                System.out.println("Rendering canvas");
                                multi.canvas.snapshot(null, image);
                                Platform.runLater(() -> imageView.setImage(image));
                                rendered.signal();
                            } finally {
                                lock.unlock();
                            }
                        }

                    } else {
                        novoRisanje.set(false);
                        long l = System.currentTimeMillis() - timeS;
                        racunanje.setText("Konec, cas simulacije: " + l + " ms");
                        Platform.runLater(() -> toggleUI(false));
                        this.stop();
                    }

            }
            long elapsedTime = (System.nanoTime() - time) / 1_000_000;
            System.out.println("Time since last redraw: " + elapsedTime + " ms");
            time = System.nanoTime();

        }
    }


}
