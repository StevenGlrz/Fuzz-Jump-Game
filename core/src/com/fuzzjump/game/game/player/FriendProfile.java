package com.fuzzjump.game.game.player;

import com.fuzzjump.api.model.user.ApiFriend;

public class FriendProfile extends Profile {

    public enum FriendStatus {
        STATUS_NONE("Add", "Removing friend"),
        STATUS_PENDING("Cancel", "Sending friend request"),
        STATUS_ACCEPTED("Remove", "Accepting friend request"),
        STATUS_INCOMING("Accept", "");

        private final String buttonLabel;
        private final String statusLabel;

        FriendStatus(String buttonLabel, String statusLabel) {
            this.buttonLabel = buttonLabel;
            this.statusLabel = statusLabel;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public String getButtonLabel() {
            return buttonLabel;
        }
    }

    private static final FriendStatus[] STATUSES = FriendStatus.values();


    private FriendStatus status;

    public FriendProfile(ApiFriend friend) {
        loadProfile(friend.getProfile());

        setDisplayName(friend.getDisplayName());
        setDisplayNameId(friend.getDisplayNameId());
        setUserId(friend.getUserId());
        setStatus(STATUSES[friend.getStatus() + 1]);
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public FriendStatus getStatus() {
        return status;
    }
}
