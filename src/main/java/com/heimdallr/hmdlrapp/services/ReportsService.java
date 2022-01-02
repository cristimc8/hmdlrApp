package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.Date;

@Service
public class ReportsService {

    UserService userService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    private ReportsService() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void generateNewFriendsAndMessagesForPeriod(Date d1, Date d2) {

    }

    public void generateMessagesWithFriendForPeriod(Date d1, Date d2) {

    }
}
