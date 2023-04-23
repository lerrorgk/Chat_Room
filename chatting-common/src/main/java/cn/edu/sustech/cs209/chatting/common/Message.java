package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private User sender;
    private User receiver;
    private String content;
    private Date sendTime;


    public Message(User sender, String receiverName,String msg) {
        this.content = msg;
        this.sender = sender;
        this.receiver = new User(receiverName);
    }

    public Message(User sender, User receiver, String content, Date sendTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendTime = sendTime;
    }

    public Message() {
    }



    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public User getReceiver() {
        return receiver;
    }
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getSendTime() {
        return sendTime;
    }
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "Message [sender=" + sender + ", receiver=" + receiver + ", content=" + content + ", sendTime=" + sendTime
                + "]";
    }
}
