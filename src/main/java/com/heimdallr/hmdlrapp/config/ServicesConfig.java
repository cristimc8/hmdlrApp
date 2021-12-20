package com.heimdallr.hmdlrapp.config;

import com.heimdallr.hmdlrapp.exceptions.ServiceInitException;
import com.heimdallr.hmdlrapp.services.DI.HmdlrDI;
import com.heimdallr.hmdlrapp.services.FriendRequestService;
import com.heimdallr.hmdlrapp.services.FriendshipsService;
import com.heimdallr.hmdlrapp.services.GroupChatsService;
import com.heimdallr.hmdlrapp.services.UserService;

import java.util.List;

public class ServicesConfig {
    HmdlrDI hmdlrDI = HmdlrDI.getContainer();

    /**
     * method that instantiates the application's services(logical controllers)
     *
     * @throws ServiceInitException Throws if any error occurs while instantiating
     */
    public void initServices() throws ServiceInitException {
        List<Class<?>> servicesToInit = List.of(
                DBConfig.class,
                UserService.class,
                FriendshipsService.class,
                FriendRequestService.class,
                GroupChatsService.class);
        hmdlrDI.initialize(servicesToInit);
    }
}
