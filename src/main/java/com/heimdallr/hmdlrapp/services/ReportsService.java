package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.models.dtos.UserMessageDTO;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.utils.PDFWriter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

        saveToFile(big, path, "friendshipsAndMessages", "Friendships and messages report");
    }

    public void generateMessagesWithFriendForPeriod(LocalDate d1, LocalDate d2, User other, String path) {
        Timestamp t1 = Timestamp.valueOf(d1.atStartOfDay());
        Timestamp t2 = Timestamp.valueOf(d2.atStartOfDay());
        this.user = userService.getCurrentUser();

        StringBuffer big = new StringBuffer();

        List<UserMessageDTO> userMessageDTOS = messagesService.findAllBetweenUsersForRange(user, other, t1, t2)
                .stream().map(m -> {
                    return new UserMessageDTO(
                            userService.findById(m.getSenderId()),
                            userService.findById(m.getReceiverId()),
                            m
                    );
                }).toList();
        userMessageDTOS.forEach(x -> {
            if(x.getMessage().getReplyTo() != -1)
                big.append(x.toString()).append('\n');
        });
        System.out.println("Generating pdf of convos with user other: " + other.getDisplayUsername());

        saveToFile(big, path, "messages", "Messages with user report");
    }


    private void saveToFile(StringBuffer big, String path, String filename, String title) {
        PDFWriter pdfWriter = new PDFWriter(path, filename);
        pdfWriter.createPdfFile();
        pdfWriter.addPage(title, big);
        pdfWriter.saveAndClose();
    }
}
