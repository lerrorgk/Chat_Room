package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBuffer {
  /** 当前客户端的用户信息 */
  public static User currentUser;
  /** 在线用户列表 */
  public static List<String> userList;
  /** 当前客户端连接到服务器的套节字 */
  public static Socket clientSeocket;
  /** 当前客户端连接到服务器的输出流 */
  public static ObjectOutputStream oos;
  /** 当前客户端连接到服务器的输入流 */
  public static ObjectInputStream ois;
  /** 服务器配置参数属性集 */
  public static Map<String, String> configMap;
  /** 本客户端的IP地址 */
  public static String ip ;
  /** 用来接收文件的端口 */
  public static final int RECEIVE_FILE_PORT = 56667;


  static{
    // 初始化
    userList = new ArrayList<>();
    //加载服务器配置文件
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
