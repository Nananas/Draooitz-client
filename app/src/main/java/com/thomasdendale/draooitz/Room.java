package com.thomasdendale.draooitz;

/**
 * Created by thomas on 6/12/15.
 */
public class Room {
    private String name;

    // TODO: replace with actual player objects
    private int players;

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

    // & data

}
