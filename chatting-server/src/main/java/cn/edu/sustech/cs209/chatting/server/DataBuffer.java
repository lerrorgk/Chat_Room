package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.User;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


public class DataBuffer {

  // 服务器端套接字
  public static ServerSocket serverSocket;
  //在线用户的IO Map
  public static Map<User, OnlineClientIOCache> onlineUserIOCacheMap;
  //在线用户List
  public static List<User> onlineUsersList;

  public static Map<String, String> configMap;

  static {
    // 初始化
    onlineUserIOCacheMap = new ConcurrentSkipListMap<>();
    onlineUsersList = new ArrayList<>();
    configMap = new HashMap<>();
    configMap.put("data", "user.txt");
    configMap.put("port", "51414");
  }

}
