package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataBuffer {
  public static User currentUser;
  public static List<String> userList;
  public static Socket clientSeocket;

  public static List<Message> messageList;
  public static Map<String, String> groupMap;
  public static ObjectOutputStream oos;
  public static ObjectInputStream ois;
  public static Map<String, String> configMap;
  public static String ip ;
  public static final int RECEIVE_FILE_PORT = 56667;


  static{
    userList = new ArrayList<>();
    messageList = new ArrayList<>();
    groupMap = new HashMap<>();
    configMap = new HashMap<>();
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
      configMap.put("data", "user.txt");
      configMap.put("port", "51414");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private DataBuffer(){}
}
