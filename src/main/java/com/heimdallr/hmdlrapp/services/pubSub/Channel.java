package com.heimdallr.hmdlrapp.services.pubSub;

public enum Channel {
    onNewMessage,
    onFriendshipsChanged,
    groupContentChanged,
    onSaveToPDFCompleted,
    onReplyStarted,
    onEventsChanged,
    onEventDeleted,
    onEventSuccessfullyCreated,
    onFriendSelectedForReports,
    guiVisibleAllUsersController,
    guiVisibleFriendsController,
    guiVisibleRequestsController,
    guiVisibleGCController,
    guiVisibleGenerateReports,
    guiVisibleSelectAFriend
}
