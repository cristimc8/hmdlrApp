package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.models.GroupChat;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.GroupChatsRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.List;

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
