package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.utils.PDFWriter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportsService {

    UserService userService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;
    GroupChatsService groupChatsService;

    private User user;

    private ReportsService() {
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
            this.friendshipsService = (FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class);
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
            this.groupChatsService = (GroupChatsService) HmdlrDI.getContainer().getService(GroupChatsService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void generateNewFriendsAndMessagesForPeriod(LocalDate d1, LocalDate d2, String path) {
        this.user = userService.getCurrentUser();
        Timestamp t1 = Timestamp.valueOf(d1.atStartOfDay());
        Timestamp t2 = Timestamp.valueOf(d2.atStartOfDay());

        int numberOfMessages = messagesService.countForRange(t1, t2, user, groupChatsService.getAllForUser(user));
        StringBuffer big = new StringBuffer();

        big.append("\n").append("Number of messages that were visible to you in this time range: ")
                .append(numberOfMessages).append('\n').append("(Including group chats)").append('\n');

        List<Friendship> friendships = friendshipsService.findForUserInRange(t1, t2);
        List<UserFriendshipDTO> userFriendshipDTOS =
                friendships.stream().map(f -> {
                    return new UserFriendshipDTO(f,
                            userService.findById(f.getUserOne()),
                            userService.findById(f.getUserTwo()));
                }).toList();
        userFriendshipDTOS.forEach(f -> {
            String text = f.toString();
            big.append("\n").append(text);
        });

        String fileName = "friendshipsAndMessages";

        PDFWriter pdfWriter = new PDFWriter(path, fileName);
        pdfWriter.createPdfFile();
        String heading = "Friendships and messages report";
        pdfWriter.addPage(heading, big);
        pdfWriter.saveAndClose();
    }

    public void generateMessagesWithFriendForPeriod(LocalDate d1, LocalDate d2, User other, String path) {
        Timestamp t1 = Timestamp.valueOf(d1.atStartOfDay());
        Timestamp t2 = Timestamp.valueOf(d2.atStartOfDay());
        System.out.println("Generating pdf of convos with user other: " + other.getDisplayUsername());
    }
}
