package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserService {

  static final String pUrl = "jdbc:postgresql://localhost:5432/chattingroom";
  static final String pUser = "checker";
  static final String pPassword = "123456";

  private static int idCounter = 0;

  public void addUser(User user) {
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
    String query = "SELECT * FROM usersdata WHERE username = " + name;
    try (Connection con = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst = con.prepareStatement(query)) {
      ResultSet rs = pst.executeQuery();
      while(rs.next()) {
        System.out.println(rs.getString("username"));
      }
      return true;
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }
    return false;
  }

  public User login(int id, String password) {
    User result = null;
    String query = "SELECT * FROM usersdata WHERE userid = " + id + " AND password = '" + password + "'";
    try (Connection con = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst = con.prepareStatement(query)) {
      ResultSet rs = pst.executeQuery();
      if (rs != null && rs.next()) {
        result = new User(rs.getString("username"), rs.getString("password"));
        result.setUserId(rs.getInt("userid"));
      }
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }
    return result;
  }


  public void initUser() {
    String query = "create table if not exists usersdata (userId integer, userName varchar(255), password varchar(255))";
    try (Connection con = DriverManager.getConnection(pUrl, pUser, pPassword);
        PreparedStatement pst = con.prepareStatement(query)) {
      pst.executeUpdate();
    } catch (SQLException ex) {
      System.out.println("SQLException");
    }
  }

  public static void main(String[] args)  {
    UserService userService = new UserService();
    userService.initUser();
    User user01 = new User("aaa", "123456");
    User user02 = new User("bbb", "654321");
    userService.addUser(user01);
    userService.addUser(user02);
    User tmp = userService.login(1, "123456");
    System.out.println(tmp);
  }
}
