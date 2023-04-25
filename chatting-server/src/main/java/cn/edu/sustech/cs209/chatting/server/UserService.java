package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserService {

  static final String pUrl = "jdbc:postgresql://localhost:5432/chattingroom";
  static final String pUser = "checker";
  static final String pPassword = "123456";
  private static int idCounter = 0;

  public static void addMessage(String sender, String receiver, String content) {
    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = DriverManager.getConnection(pUrl, pUser, pPassword);
      stmt = con.prepareStatement(
          "INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)");
      stmt.setString(1, sender);
      stmt.setString(2, receiver);
      stmt.setString(3, content);
      stmt.execute();
    } catch (SQLException e) {
      System.err.println("Cannot connect to the database");
      e.printStackTrace();
    }
  }

  public static void addMessage(String sender, String receiver, String content, String groupName,
      String groupMembers) {
    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = DriverManager.getConnection(pUrl, pUser, pPassword);
      stmt = con.prepareStatement(
          "INSERT INTO messages (sender, receiver, content, groupName, groupMembers) VALUES (?, ?, ?, ?,?)");
      stmt.setString(1, sender);
      stmt.setString(2, receiver);
      stmt.setString(3, content);
      stmt.setString(4, groupName);
      stmt.setString(5, groupMembers);
      stmt.execute();
    } catch (SQLException e) {
      System.err.println("Cannot connect to the database");
      e.printStackTrace();
    }
  }

  public static List<Message> getChatList(String sender) {
//    String like = "%"+sender+"%";
    Connection con = null;
    PreparedStatement stmt = null;
    List<Message> messages = new ArrayList<>();
    try {
      con = DriverManager.getConnection(pUrl, pUser, pPassword);
      stmt = con.prepareStatement(
          "SELECT * FROM messages WHERE (sender = ? OR receiver = ?) AND groupName IS NULL");
//      stmt = con.prepareStatement("SELECT * FROM messages WHERE sender = ? OR receiver = ? OR groupMembers LIKE ?");
      stmt.setString(1, sender);
      stmt.setString(2, sender);
//      stmt.setString(3, like);
      stmt.execute();
      ResultSet rs = stmt.getResultSet();
      while (rs.next()) {
        Message message = new Message();
        User user1 = new User(rs.getString("sender"));
        message.setSender(user1);
        User user2 = new User(rs.getString("receiver"));
        message.setReceiver(user2);
        message.setContent(rs.getString("content"));
        message.setGroupName(rs.getString("groupName"));
        String[] ms = rs.getString("groupMembers").split(",");
        message.setGroupMembers(ms);
        System.out.println(message.getSender().getUsername());
        messages.add(message);
      }
    } catch (SQLException e) {
      System.err.println("Cannot connect to the database");
      e.printStackTrace();
    }
    return messages;
  }

  public static void addUser(User user) {
    user.setUserId(++idCounter);

    String query =
        "INSERT INTO usersdata ( userid ,username, password) VALUES (" + user.getUserId() + ", '"
            + user.getUsername() + "', '" + user.getPassword() + "')";

    try (Connection con = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst = con.prepareStatement(query)) {
      pst.executeUpdate();
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }
  }

  public static boolean checkUserName(String name) {
    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = DriverManager.getConnection(pUrl, pUser, pPassword);
      stmt = con.prepareStatement("SELECT * FROM usersdata WHERE userName = ?");
    } catch (SQLException e) {
      System.err.println("Cannot connect to the database");
      e.printStackTrace();
    }
    try {
      stmt.setString(1, name);
      stmt.execute();
      ResultSet rs = stmt.getResultSet();
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      System.err.println("Cannot set the statement");
      e.printStackTrace();
    }
    return false;
  }

  public static boolean login(String userName, String password) {
    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = DriverManager.getConnection(pUrl, pUser, pPassword);
      stmt = con.prepareStatement("SELECT * FROM usersdata WHERE userName = ? AND password = ?");
      stmt.setString(1, userName);
      stmt.setString(2, password);
      stmt.execute();
      ResultSet rs = stmt.getResultSet();
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      System.err.println("Cannot connect to the database");
      e.printStackTrace();
    }
    return false;
  }


  public static void initUser() {
    String query = "create table if not exists usersdata (userId integer, userName varchar(255), password varchar(255))";
    try (Connection con = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst = con.prepareStatement(query)) {
      pst.executeUpdate();
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }

    String query2 = "create table if not exists messages (sender varchar(255), receiver varchar(255), content varchar(800), groupName varchar(255), groupMembers varchar(800))";
    try (Connection con2 = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst2 = con2.prepareStatement(query2)) {
      pst2.executeUpdate();
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }
  }

//  public static void main(String[] args)  {
//    UserService userService = new UserService();
//    userService.initUser();
//    User user01 = new User("aaa", "123456");
//    User user02 = new User("bbb", "654321");
//    userService.addUser(user01);
//    userService.addUser(user02);
//    User tmp = userService.login(1, "123456");
//    System.out.println(tmp);
//  }
}
