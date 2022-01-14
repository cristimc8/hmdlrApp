package com.heimdallr.hmdlrapp.models.dtos;

import com.heimdallr.hmdlrapp.exceptions.ServiceNotRegisteredException;
import com.heimdallr.hmdlrapp.models.FriendRequest;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendRequestService;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.UserService;

import java.util.List;

public class Page {
    User user;

    String fname;
    String lname;
    List<UserFriendshipDTO> friends;

    List<FriendRequest> friendRequests;

    public Page() {
        try {
            this.user = ((UserService) HmdlrDI.getContainer().getService(UserService.class)).getCurrentUser();
            this.fname = user.getFirstName();
            this.lname = user.getLastName();
            this.friends = ((FriendshipsService) HmdlrDI.getContainer().getService(FriendshipsService.class)).findAllWithUser(user);
            this.friendRequests = ((FriendRequestService) HmdlrDI.getContainer().getService(FriendRequestService.class)).findAllForUser(user);
        } catch (ServiceNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    public String getFname() {
        return this.fname;
    }

    public String getLname() {
        return this.lname;
    }

    public User getUser() {
        return user;
    }

    public List<UserFriendshipDTO> getFriends() {
        return friends;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }
}
