package com.heimdallr.hmdlrapp.controllers.main.popups.reports;

import com.heimdallr.hmdlrapp.controllers.CustomController;
import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.ReportsService;
import com.heimdallr.hmdlrapp.services.pubSub.Subscriber;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class GenerateReportsController extends Subscriber implements CustomController {

    Button friendsAndMessagesButton;
    Button messagesWithFriendButton;
    DatePicker firstDatePicker;
    DatePicker secondDatePicker;
    ImageView closeGenerateReportsContainer;
    BorderPane generateReportsPopupContainer;
    BorderPane parent;

    ReportsService reportsService;

    public GenerateReportsController(
            Button friendsAndMessagesButton,
            Button messagesWithFriendButton,
            DatePicker firstDatePicker,
            DatePicker secondDatePicker,
            ImageView closeGenerateReportsContainer,
            BorderPane generateReportsPopupContainer,
            BorderPane parent) {
        this.friendsAndMessagesButton = friendsAndMessagesButton;
        this.messagesWithFriendButton = messagesWithFriendButton;
        this.firstDatePicker = firstDatePicker;
        this.secondDatePicker = secondDatePicker;
        this.closeGenerateReportsContainer = closeGenerateReportsContainer;
        this.generateReportsPopupContainer = generateReportsPopupContainer;
        this.parent = parent;
    }

    @Override
    public void initialize() {
        try {
            this.reportsService = (ReportsService) HmdlrDI.getContainer().getService(ReportsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        this.addEventListeners();
    }

    @Override
    protected void newContent(String info) {

    }

    private void addEventListeners() {
        closeGenerateReportsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                generateReportsPopupContainer.setVisible(false);
                parent.setOpacity(1);
            }
        });

        friendsAndMessagesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //get dates and access service functions to export
                if(datesValid()) {
                    String path = getPath();
                    if(path == null) return;
                    reportsService.generateNewFriendsAndMessagesForPeriod(LocalDate.ofEpochDay(firstDatePicker.getValue().toEpochDay()), LocalDate.ofEpochDay(secondDatePicker.getValue().toEpochDay()), path);
                }
            }
        });

        messagesWithFriendButton.setOnMouseClicked(e -> {
            // get dates and open a controller to select a friend
            if(datesValid()) {

            }
        });
    }

    private String getPath() {
        Stage stage = (Stage) messagesWithFriendButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if(selectedDirectory == null){
            //No Directory selected
        }else{
            System.out.println("Selected path: " + selectedDirectory.getAbsolutePath());
        }
        return selectedDirectory == null ? null : selectedDirectory.getAbsolutePath();
    }

    private boolean datesValid() {
        return firstDatePicker.getValue() != null && secondDatePicker.getValue() != null;
    }
}
