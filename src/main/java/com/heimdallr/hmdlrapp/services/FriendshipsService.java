package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.Friendship;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.models.UserFriendshipDTO;
import com.heimdallr.hmdlrapp.repository.FriendshipsRepository;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;

import java.util.ArrayList;
import java.util.List;

@Service(AssociatedRepo = FriendshipsRepository.class)
public class FriendshipsService {
    FriendshipsRepository friendshipsRepository;
    UserService userService;

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
     *
     * @param userOne User one
     * @param userTwo User two
     */
    public void deleteFriendship(User userOne, User userTwo) {
        this.deleteFriendship(userOne.getId(), userTwo.getId());
    }

    public void deleteFriendship(int uidOne, int uidTwo) {
        Friendship friendship = friendshipsRepository.findForTwoUsers(uidOne, uidTwo);
        if (friendship != null) {
            friendshipsRepository.deleteOne(friendship.getId());
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
