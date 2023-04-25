package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Request implements Serializable {

  private Type type;
  String username;
  String password;
  private String content;
  private String receiver;
  private String sender;
  private String groupName;
  private String[] groupMembers;
  boolean isGroupMessage;

  public Request(Type register) {
    this.type = register;
  }

  public String[] getGroupMembers() {
    return groupMembers;
  }

  public void setGroupMembers(String[] groupMembers) {
    this.groupMembers = groupMembers;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public boolean isGroupMessage() {
    return isGroupMessage;
  }

  public void setGroupMessage(boolean groupMessage) {
    isGroupMessage = groupMessage;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

}
