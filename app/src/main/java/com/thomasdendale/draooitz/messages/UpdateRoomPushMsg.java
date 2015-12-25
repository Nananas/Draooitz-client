package com.thomasdendale.draooitz.messages;

/**
 * Created by thomas on 23/12/15.
 */
public class UpdateRoomPushMsg {
    private UpdateRoomPushMsgContent content;
    private String msg;

    public UpdateRoomPushMsgContent getContent() {
        return content;
    }

    public void setContent(UpdateRoomPushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
