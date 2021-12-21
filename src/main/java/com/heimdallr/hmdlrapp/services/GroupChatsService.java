package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.models.BaseEntity;
import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.GroupChatsRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.List;
import java.util.Locale;

@Service(AssociatedRepo = GroupChatsRepository.class)
public class GroupChatsService {
    private GroupChatsRepository groupChatsRepository;

    /**
     * Private default constructor
     * Receives through its arguments the instantiated Group chat Repository class
     *
     * @param groupChatsRepo Instantiated Group chat repository class.
     */
    private GroupChatsService(Object groupChatsRepo) {
        this.groupChatsRepository = (GroupChatsRepository) groupChatsRepo;
    }

    public GroupChat createOne(String alias, List<User> participants) {
        String participantsString = getParticipantsAsString(participants);
        return this.createOne(alias, participantsString);
    }

    private GroupChat createOne(String alias, String participants) {
        GroupChat groupChat = new GroupChat(groupChatsRepository.getNextAvailableId(), alias, participants);
        groupChatsRepository.addOne(groupChat);
        return groupChat;
    }

    /**
     * Return groupChat preview letters
     * First and last letter or only the first if alias is one character.
     * @param groupChat GroupChat to extract info from
     * @return The preview letters String
     */
    public String getChatHeadPreviewLetters(GroupChat groupChat) {
        return this.getChatHeadPreviewLetters(groupChat.getId());
    }

    /**
     * Returns the list of participants as a '<>' delimited String.
     * @return list of participants ids delimited with <> separator
     */
    public String getParticipantsAsString(List<User> participants) {
        List<Integer> participantsIds = participants.stream().map(BaseEntity::getId).toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer elem : participantsIds) {
            stringBuilder.append(elem).append(",");
        }

        return "," + stringBuilder.toString();
    }

    public String getChatHeadPreviewLetters(String id) {
        String groupAlias = this.findById(id).getAlias();
        if(groupAlias.length() == 1) return String.valueOf(groupAlias.charAt(0));
        return String.valueOf(groupAlias.charAt(0)).toUpperCase(Locale.ROOT) + String.valueOf(groupAlias.charAt(groupAlias.length() - 1)).toUpperCase(Locale.ROOT);
    }

    public GroupChat findById(String id) {
        return groupChatsRepository.findById(id);
    }

    /**
     * Returns all groups a user is in.
     * @param user the user
     * @return the list of all GC's the user is in.
     */
    public List<GroupChat> getAllForUser(User user) {
        return this.getAllForUser(user.getId());
    }

    public List<GroupChat> getAllForUser(int uid) {
        return groupChatsRepository.findAllForUser(uid);
    }

}
