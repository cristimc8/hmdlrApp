package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.models.BaseEntity;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.MessagesRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.List;

@Service(AssociatedRepo = MessagesRepository.class)
public class MessagesService {
    private MessagesRepository messagesRepository;

    private MessagesService(Object messagesRepo) {
        this.messagesRepository = (MessagesRepository) messagesRepo;
    }

    public List<Message> findAllBetweenUsers(User userOne, User userTwo) {
        return this.findAllBetweenUsers(userOne.getId(), userTwo.getId());
    }

    public List<Message> findAllBetweenUsers(int uidOne, int uidTwo) {
        return this.messagesRepository.getAllBetweenUsers(uidOne, uidTwo);
    }

    public List<Message> findAllForGroup(GroupChat groupChat) {
        return this.findAllForGroup(groupChat.getId());
    }

    public List<Message> findAllForGroup(String gid) {
        return this.messagesRepository.getAllForGroup(gid);
    }

    /**
     * Deletes all messages between two users.
     * @param userOne User one
     * @param userTwo User two
     */
    public void deleteAllBetweenUsers(User userOne, User userTwo) {
        this.deleteAllBetweenUsers(userOne.getId(), userTwo.getId());
    }

    public void deleteAllBetweenUsers(int uidOne, int uidTwo) {
        this.messagesRepository.deleteAllBetweenTwoUsers(uidOne, uidTwo);
    }

    public List<Message> getAllUserPreviews(User user, List<GroupChat> userGroups) {
        List<String> userGroupsStringList = userGroups.stream().map(BaseEntity::getId).toList();
        return this.getAllUserPreviews(user.getId(), userGroupsStringList);
    }

    public List<Message> getAllUserPreviews(int uid, List<String> userGroups) {
        return this.messagesRepository.getAllPreviewsForUser(uid, userGroups);
    }

    public List<Message> getAllUserMessages(User user) {
        return this.getAllUserMessages(user.getId());
    }

    public List<Message> getAllUserMessages(int uid) {
        return this.messagesRepository.getAllMessagesForUser(uid);
    }

    /**
     * Method used to send(not reply) a message.
     * @param sender Sender user
     * @param receiver Receiver user
     * @param messageBody The body of the message to send
     */
    public void sendMessage(User sender, User receiver, String messageBody) {
        this.sendMessage(sender.getId(), receiver.getId(), messageBody);
    }

    public void sendMessage(User sender, GroupChat groupChat, String messageBody) {
        this.sendMessage(sender.getId(), groupChat.getId(), messageBody);
    }

    public void sendMessage(int uidSender, String groupChatId, String messageBody) {
        Message message = new Message(
                this.messagesRepository.getNextAvailableId(),
                uidSender,
                0,
                groupChatId,
                0,
                messageBody
        );

        this.messagesRepository.addOne(message);
    }

    public void sendMessage(int uidSender, int uidReceiver, String messageBody) {
        Message message = new Message(
                this.messagesRepository.getNextAvailableId(),
                uidSender,
                uidReceiver,
                null,
                0,
                messageBody
        );

        this.messagesRepository.addOne(message);
    }

    /**
     * Method used to reply to a message's id
     */
    public void replyToMessage(User sender, User receiver, int messageId, String messageBody) {
        this.replyToMessage(sender.getId(), receiver.getId(), messageId, messageBody);
    }

    public void replyToMessage(User sender, GroupChat groupChat, int messageId, String messageBody) {
        this.replyToMessage(sender.getId(), groupChat.getId(), messageId, messageBody);
    }

    public void replyToMessage(int uidSender, String groupChatId, int messageId, String messageBody) {
        Message message = new Message(
                this.messagesRepository.getNextAvailableId(),
                uidSender,
                0,
                groupChatId,
                messageId,
                messageBody
        );

        this.messagesRepository.addOne(message);
    }

    public void replyToMessage(int uidSender, int uidReceiver, int messageId, String messageBody) {
        Message message = new Message(
                this.messagesRepository.getNextAvailableId(),
                uidSender,
                uidReceiver,
                null,
                messageId,
                messageBody
        );

        this.messagesRepository.addOne(message);
    }
}
