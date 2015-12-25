package com.thomasdendale.draooitz.messages;

/**
 * Created by thomas on 23/12/15.
 */
public class PushMsg {
    private PushMsgContent content;
    private String msg;

    public PushMsgContent getContent() {
        return content;
    }

    public void setContent(PushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

