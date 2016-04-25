package com.thomasdendale.draooitz;

/**
 * Created by thomas on 6/12/15.
 */
public class Room {
    private String name;

    private int players; // player count

    Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return name;
    }

    // This can be extended with additional statistics data
    // We could even make a room-master of some sort?


}
