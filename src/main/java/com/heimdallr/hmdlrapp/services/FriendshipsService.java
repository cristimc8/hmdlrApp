package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.dtos.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.repository.FriendshipsRepository;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;

import java.util.ArrayList;
import java.util.List;

@Service(AssociatedRepo = FriendshipsRepository.class)
public class FriendshipsService {
    FriendshipsRepository friendshipsRepository;
    UserService userService;
    MessagesService messagesService;

    /**
     * Private default constructor
     * Receives through its arguments the instantiated Friendships Repository class
     *
     * @param friendshipsRepo Instantiated Users repository class.
     */
    private FriendshipsService(Object friendshipsRepo) {
        this.friendshipsRepository = (FriendshipsRepository) friendshipsRepo;
        try {
            this.userService = (UserService) HmdlrDI.getContainer().getService(UserService.class);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a friendship between two users.
     * Also deletes all the messages they shared.
     *
     * @param userOne User one
     * @param userTwo User two
     */
    public void deleteFriendship(User userOne, User userTwo) {
        this.deleteFriendship(userOne.getId(), userTwo.getId());
    }

    public void deleteFriendship(int uidOne, int uidTwo) {
        this.messagesService.deleteAllBetweenUsers(uidOne, uidTwo);
        Friendship friendship = friendshipsRepository.findForTwoUsers(uidOne, uidTwo);
        if (friendship != null) {
            friendshipsRepository.deleteOne(friendship.getId());
        }

        notifySubs();
    }

    /**
     * Notifies subscribers that a new message arrived(or was sent)
     */
    private void notifySubs() {
        try {
            ((EventDispatcher) HmdlrDI.getContainer().getService(EventDispatcher.class))
                    .dispatch(Channel.onNewMessage, null);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a friendship between two users
     *
     * @param userOne User 1
     * @param userTwo User 2
     * @return Their friendship object
     */
    public Friendship findForTwoUsers(User userOne, User userTwo) {
        return findForTwoUsers(userOne.getId(), userTwo.getId());
    }

    public Friendship findForTwoUsers(int uidOne, int uidTwo) {
        return friendshipsRepository.findForTwoUsers(uidOne, uidTwo);
    }

    /**
     * Method returning all the friendships a user is in, as a list of DTO's
     * containing both users and the friendship object.
     *
     * @param user User we search for
     * @return List of user - friendship DTO's
     */
    public List<UserFriendshipDTO> findAllWithUser(User user) {
        return findAllWithUser(user.getId());
    }

    /**
     * Method returning all the friendships a user is in, as a list of DTO's
     * containing both users and the friendship object.
     *
     * @param uid User's id we search for
     * @return List of user - friendship DTO's
     */
    public List<UserFriendshipDTO> findAllWithUser(int uid) {
        List<UserFriendshipDTO> userFriendshipDTOS = new ArrayList<>();

        List<Friendship> friendships = friendshipsRepository.findAllWithUser(uid);

        for (Friendship friendship : friendships) {
            User userOne = userService.findById(friendship.getUserOne());
            User userTwo = userService.findById(friendship.getUserTwo());

            userFriendshipDTOS.add(new UserFriendshipDTO(friendship, userOne, userTwo));
        }

        return userFriendshipDTOS;
    }
}
