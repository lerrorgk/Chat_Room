package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

  private User sender;
  private User receiver;
  private String content;
  private String groupName;
  private String[] groupMembers;
  boolean isGroupMessage;


  public Message(User sender, String receiverName, String msg) {
    this.content = msg;
    this.sender = sender;
    this.receiver = new User(receiverName);
  }

  public Message(String senderName, String receiverName, String msg) {
    this.content = msg;
    this.sender = new User(senderName);
    this.receiver = new User(receiverName);
  }

  public Message(String senderName, String receiverName, String msg, boolean isGroupMessage,
      String groupName, String[] groupMembers) {
    this.content = msg;
    this.sender = new User(senderName);
    this.receiver = new User(receiverName);
    this.isGroupMessage = isGroupMessage;
    this.groupName = groupName;
    this.groupMembers = groupMembers;
  }

  public Message(User sender, User receiver, String content) {
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
  }

  public Message() {

  }

  public Message(User user, String msg, boolean b, String groupName, String[] uniqueMembers) {
    this.sender = user;
    this.content = msg;
    this.isGroupMessage = b;
    this.groupName = groupName;
    this.groupMembers = uniqueMembers;
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

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String[] getGroupMembers() {
    return groupMembers;
  }

  public void setGroupMembers(String[] groupMembers) {
    this.groupMembers = groupMembers;
  }

  public boolean isGroupMessage() {
    return isGroupMessage;
  }

  public void setGroupMessage(boolean groupMessage) {
    isGroupMessage = groupMessage;
  }


  @Override
  public String toString() {
    return "Message [sender=" + sender + ", receiver=" + receiver + ", content=" + content
        + ", groupName=" + groupName + ", groupMembers=" + Arrays.toString(groupMembers)
        + "]";
  }
}
