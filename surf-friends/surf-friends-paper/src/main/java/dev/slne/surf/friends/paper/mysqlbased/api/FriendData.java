package dev.slne.surf.friends.paper.mysqlbased.api;

import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class FriendData {
    private List<UUID> friendList;
    private List<UUID> friendRequests;
    private Boolean allowRequests;

    public FriendData friendList(List<UUID> friendList){
        this.friendList = friendList;
        return this;
    }

    public FriendData friendRequests(List<UUID> friendRequests){
        this.friendRequests = friendRequests;
        return this;
    }

    public FriendData allowRequests(Boolean allowRequests){
        this.allowRequests = allowRequests;
        return this;
    }
}
