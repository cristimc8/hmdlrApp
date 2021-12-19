package com.heimdallr.hmdlrapp;

import com.heimdallr.hmdlrapp.config.ConfigHelper;
import com.heimdallr.hmdlrapp.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(Constants.appName);
        stage.setScene(scene);
        stage.show();
    }

    public static void runLogic() {
        ConfigHelper configHelper = new ConfigHelper();
        configHelper.runAll();
    }

    public static void main(String[] args) {
        runLogic();
        launch();
    }
}