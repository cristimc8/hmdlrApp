package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.FriendRequestRepository;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.services.pubSub.Channel;
import com.heimdallr.hmdlrapp.services.pubSub.EventDispatcher;
import com.heimdallr.hmdlrapp.utils.Constants;

import java.util.List;

@Service(AssociatedRepo = FriendRequestRepository.class)
public class FriendRequestService {
    private FriendRequestRepository friendRequestRepository;

    /**
     * Private default constructor
     * Receives through its arguments the instantiated Friend Request Repository class
     *
     * @param friendRequestRepo Instantiated Friend request repository class.
     */
    private FriendRequestService(Object friendRequestRepo) {
        this.friendRequestRepository = (FriendRequestRepository) friendRequestRepo;
    }

    /**
     * Gets all the friend requests (even past ones) for a certain user.
     *
     * @param user User we want the friend requests for
     * @return All the user's past friend requests.
     */
    public List<FriendRequest> findAllForUser(User user) {
        return this.findAllForUser(user.getId());
    }

    public List<FriendRequest> findAllForUser(int uid) {
        return friendRequestRepository.findAllForUser(uid);
    }

    /**
     * Updates the status of an existing friend request.
     *
     * @param friendRequest           Existing friend request
     * @param friendshipRequestStatus New status we want to use
     */
    public void setFriendRequestStatus(FriendRequest friendRequest, Constants.FriendshipRequestStatus friendshipRequestStatus) {
        this.setFriendRequestStatus(friendRequest.getId(), friendshipRequestStatus);
    }

    public void setFriendRequestStatus(int friendRequestId, Constants.FriendshipRequestStatus friendshipRequestStatus) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId);
        friendRequest.setFriendshipRequestStatus(friendshipRequestStatus);

        if (friendshipRequestStatus == Constants.FriendshipRequestStatus.APPROVED) {
            try {
                ((MessagesService) HmdlrDI.getContainer().getService(MessagesService.class))
                        .sendMessage(
                                friendRequest.getSenderId(),
                                friendRequest.getReceiverId(),
                                Constants.sayHi
                        );

                notifySubs();
            } catch (ServiceNotRegisteredException e) {
                e.printStackTrace();
            }
        }

        friendRequestRepository.updateOne(friendRequest, friendRequest);
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
     * Creates a pending friend request between two users.
     *
     * @param userOne User one
     * @param userTwo User two
     */
    public void createFriendRequest(User userOne, User userTwo) {
        this.createFriendRequest(userOne.getId(), userTwo.getId());
    }

    public void createFriendRequest(int uidOne, int uidTwo) {
        FriendRequest friendRequest = new FriendRequest(
                this.friendRequestRepository.getNextAvailableId(),
                uidOne,
                uidTwo
        );

        friendRequestRepository.addOne(friendRequest);
    }
}
