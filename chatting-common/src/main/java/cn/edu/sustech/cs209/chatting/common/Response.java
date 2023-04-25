package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

  private Type type;
  private boolean isValid;
  private String[] usersList;
  private String content;
  private String receiver;
  private String sender;
  private String groupName;

  private String[] groupMembers;
  private Message[] chatList;

  private int currentOnlineCnt;


  public Response() {
    this.isValid = false;
  }

  public Response(Type type) {
    this.type = type;
    this.isValid = false;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setUsersList(String[] usersList) {
    this.usersList = usersList;
  }

  public String[] getUsersList() {
    return usersList;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
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

  public int getCurrentOnlineCnt() {
    return currentOnlineCnt;
  }

  public void setCurrentOnlineCnt(int currentOnlineCnt) {
    this.currentOnlineCnt = currentOnlineCnt;
  }

  public Message[] getMessages() {
    return chatList;
  }

  public void setMessages(Message[] messages) {
    this.chatList = messages;
  }
}
