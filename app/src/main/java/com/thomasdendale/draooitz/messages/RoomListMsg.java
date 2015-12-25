package com.thomasdendale.draooitz.messages;

/**
 * Created by thomas on 23/12/15.
 */

public class RoomListMsg {
    private RoomMsg[] rooms;

    public RoomMsg[] getRooms() {
        return rooms;
    }

    public void setMsgs(RoomMsg[] msgs) {
        this.rooms = msgs;
    }
}
