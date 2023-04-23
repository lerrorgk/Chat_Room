package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

  private Type type;
  private boolean isValid;

  private String[] usersList;

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

}
