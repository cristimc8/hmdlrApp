package com.heimdallr.hmdlrapp.controllers.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ProfileHeadController extends AnchorPane {
    @FXML
    Label lettersLabel;

    @FXML
    AnchorPane profilePictureCircle;

    public ProfileHeadController(String imagePath, String letters) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/heimdallr/hmdlrapp/main/profileHead.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            if(imagePath == null) {
                // circle color and letters
                lettersLabel.setText(letters);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
