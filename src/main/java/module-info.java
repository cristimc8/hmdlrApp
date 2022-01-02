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
    opens com.heimdallr.hmdlrapp.models;
    exports com.heimdallr.hmdlrapp.models;
    exports com.heimdallr.hmdlrapp.controllers.main.popups.users.requests;
    opens com.heimdallr.hmdlrapp.controllers.main.popups.users.requests;
    exports com.heimdallr.hmdlrapp.controllers.main.popups.users;
    opens com.heimdallr.hmdlrapp.controllers.main.popups.users;
    exports com.heimdallr.hmdlrapp.controllers.main.popups.groupChats;
    opens com.heimdallr.hmdlrapp.controllers.main.popups.groupChats;
    exports com.heimdallr.hmdlrapp.controllers.chat;
    opens com.heimdallr.hmdlrapp.controllers.chat to javafx.fxml;
    exports com.heimdallr.hmdlrapp.controllers.misc;
    opens com.heimdallr.hmdlrapp.controllers.misc to javafx.fxml;
    exports com.heimdallr.hmdlrapp.controllers.main.sliderMenu;
    opens com.heimdallr.hmdlrapp.controllers.main.sliderMenu to javafx.fxml;
}