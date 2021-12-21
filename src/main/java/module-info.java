module com.heimdallr.hmdlrapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.heimdallr.hmdlrapp to javafx.fxml;
    exports com.heimdallr.hmdlrapp;
    exports com.heimdallr.hmdlrapp.controllers.auth;
    opens com.heimdallr.hmdlrapp.controllers.auth to javafx.fxml;
    exports com.heimdallr.hmdlrapp.controllers.main;
    opens com.heimdallr.hmdlrapp.controllers.main to javafx.fxml;
    opens com.heimdallr.hmdlrapp.utils to javafx.fxml;
    exports com.heimdallr.hmdlrapp.utils;
}