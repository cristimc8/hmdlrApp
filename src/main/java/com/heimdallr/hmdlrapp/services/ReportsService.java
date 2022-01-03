package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.utils.PDFWriter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ReportsService {

    UserService userService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    private User user;

    private ReportsService() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }

        this.user = userService.getCurrentUser();
    }

    public void generateNewFriendsAndMessagesForPeriod(LocalDate d1, LocalDate d2) {
        Timestamp t1 = Timestamp.valueOf(d1.atStartOfDay());
        Timestamp t2 = Timestamp.valueOf(d2.atStartOfDay());
        List<Friendship> friendships = friendshipsService.findForUserInRange(t1, t2);
        StringBuffer big = new StringBuffer();
        friendships.forEach(f -> {
            String text = f.toString();
            big.append("\n").append(text);
        });

        String outputDir = "/home/cristi/college/map/docs";
        String fileName = "ex";

        PDFWriter pdfWriter = new PDFWriter(outputDir, fileName);
        pdfWriter.createPdfFile();
        String heading = "Conva";
        pdfWriter.addPage(heading, big);
        pdfWriter.saveAndClose();
    }

    public void generateMessagesWithFriendForPeriod(LocalDate d1, LocalDate d2) {
        Timestamp t1 = Timestamp.valueOf(d1.atStartOfDay());
        Timestamp t2 = Timestamp.valueOf(d2.atStartOfDay());
    }
}
