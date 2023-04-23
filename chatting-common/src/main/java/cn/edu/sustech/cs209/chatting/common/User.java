package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

  private int userId;
  private String username;
  private String password;

  public User() {
    this.userId = 0;
    this.username = "";
    this.password = "";
  }

  public User(int id, String username, String password) {
    this.userId = id;
    this.username = username;
    this.password = password;
  }

  public User(int id, String password) {
    this.userId = id;
    this.password = password;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }
  public User(String username) {
    this.username = username;
  }

  @Override
  public int hashCode() {
    int result = (int) (userId ^ (userId >>> 32));
    result = 31 * result + username.hashCode();
    result = 31 * result + password.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return username.equals(user.username);
  }

  @Override
  public String toString() {
    return "User{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        '}';
  }

  // getters and setters
  public long getUserId() {
    return userId;
  }
  public void setUserId(int userId) {
    this.userId = userId;
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

  @Override
  public int compareTo(User other) {
    return this.username.compareTo(other.username);
  }
}
