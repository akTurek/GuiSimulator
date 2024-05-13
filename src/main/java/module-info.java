module org.example.guisimulator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens org.example.guisimulator to javafx.fxml;
    exports org.example.guisimulator;
}