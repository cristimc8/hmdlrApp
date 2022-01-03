package com.heimdallr.hmdlrapp.models.dtos;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.MessagesService;

public class UserMessageDTO {
    private User userOne;
    private User userTwo;
    private Message message;

    private MessagesService messagesService;

    public UserMessageDTO(User userOne, User userTwo, Message message) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        this.message = message;
        try {
            this.messagesService = (MessagesService) HmdlrDI.getContainer().getService(MessagesService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public User getUserOne() {
        return userOne;
    }

    public void setUserOne(User userOne) {
        this.userOne = userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    @Override
    public String toString() {
        User from = message.getSenderId() == userOne.getId() ? userOne : userTwo;
        User to = message.getSenderId() != userOne.getId() ? userOne : userTwo;
        String possibleReply = message.getReplyTo() > 0 ? "Replying to: " + messagesService.findById(message.getReplyTo()).getMessageBody() + "\n" : "";

        return "Message id: " + message.getId() + '\n' +
                "from: " + from.getDisplayUsername() + '\n' +
                "to: " + to.getDisplayUsername() + '\n' +
                "message: " + message.getMessageBody() + '\n' +
                possibleReply +
                "======================\n";
    }

    public void setUserTwo(User userTwo) {
        this.userTwo = userTwo;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
