package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.models.BaseEntity;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.Message;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.MessagesRepository;
import com.heimdallr.hmdlrapp.repository.pagination.Page;
import com.heimdallr.hmdlrapp.repository.pagination.Pageable;
import com.heimdallr.hmdlrapp.repository.pagination.PageableImplementation;
import com.heimdallr.hmdlrapp.repository.pagination.PagingRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service(AssociatedRepo = MessagesRepository.class)
public class MessagesService {
    private int page = -1;
    private int size = 14;

    private Pageable pageable;

    private MessagesRepository messagesRepository;

    private MessagesService(Object messagesRepo) {
        this.messagesRepository = (MessagesRepository) messagesRepo;
    }

    public int countForRange(Timestamp t1, Timestamp t2, User user, List<GroupChat> userGroups) {
        List<String> userGroupsStringList = userGroups.stream().map(BaseEntity::getId).toList();

        return messagesRepository.countForRangeForUser(t1, t2, user.getId(), userGroupsStringList);
    }

    public Message findById(int id) {
        return this.messagesRepository.findById(id);
    }

    public List<Message> findAllBetweenUsers(User userOne, User userTwo) {
        return this.findAllBetweenUsers(userOne.getId(), userTwo.getId());
    }

    // Overload with id's
    public List<Message> findAllBetweenUsers(int uidOne, int uidTwo) {
        return this.nextPageBetweenUsers(uidOne, uidTwo).stream().toList();
    }

    public Message latestMessageBetweenUsers(int uidOne, int uidTwo) {
        List<Message> lastPage = findAllBetweenUsers(0, uidOne, uidTwo);
        return lastPage.get(lastPage.size() - 1);
    }

    // Retrieves the next page
    private List<Message> nextPageBetweenUsers(int uidOne, int uidTwo) {
        this.page++;
        return findAllBetweenUsers(this.page, uidOne, uidTwo);
    }

    private List<Message> findAllBetweenUsers(int page, int uidOne, int uidTwo) {
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message> messagePage = messagesRepository.getAllBetweenUsers(pageable, uidOne, uidTwo);
        List<Message> retrievedMessages = messagePage.getContent().toList();
        List<Message> tmpList = new ArrayList<>(retrievedMessages);
        Collections.reverse(tmpList);
        return tmpList;
    }

    // for groups

    public Message latestMessageForGroup(String gid) {
        List<Message> lastPage = findAllForGroup(0, gid);
        return lastPage.get(lastPage.size() - 1);
    }

    // Retrieves the next page
    private List<Message> nextPageForGroup(String gid) {
        this.page++;
        return findAllForGroup(this.page, gid);
    }

    private List<Message> findAllForGroup(int page, String gid) {
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message> messagePage = messagesRepository.getAllForGroup(pageable, gid);
        List<Message> retrievedMessages = messagePage.getContent().toList();
        List<Message> tmpList = new ArrayList<>(retrievedMessages);
        Collections.reverse(tmpList);
        return tmpList;
    }


    public List<Message> findAllForGroup(GroupChat groupChat) {
        return this.findAllForGroup(groupChat.getId());
    }

    public List<Message> findAllForGroup(String gid) {
        return this.nextPageForGroup(gid);
    }

    /**
     * Deletes all messages between two users.
     *
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
        List<Message> previews = this.messagesRepository.getAllPreviewsForUser(uid, userGroups);
        previews.forEach(message -> {
            message.setMessageBody(message.getMessageBody().split("\n")[0]);
        });
        return previews;

    }

    public List<Message> getAllUserMessages(User user) {
        return this.getAllUserMessages(user.getId());
    }

    public List<Message> getAllUserMessages(int uid) {
        return this.messagesRepository.getAllMessagesForUser(uid);
    }

    /**
     * Method used to send(not reply) a message.
     *
     * @param sender      Sender user
     * @param receiver    Receiver user
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

    public void setPageSize(int size) {
        this.size = size;
    }

    public void resetPage() {
        this.page = -1;
    }
}
