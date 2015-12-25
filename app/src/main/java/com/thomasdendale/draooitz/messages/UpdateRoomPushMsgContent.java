package com.thomasdendale.draooitz.messages;

/**
 * Created by thomas on 23/12/15.
 */

public class UpdateRoomPushMsgContent {
    private String name;
    private int people;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }
}
