package com.thomasdendale.draooitz.messages;

/**
 * Created by thomas on 23/12/15.
 */
public class NewRoomPushMsg {
    private NewRoomPushMsgContent content;
    private String msg;

    public NewRoomPushMsgContent getContent() {
        return content;
    }

    public void setContent(NewRoomPushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
