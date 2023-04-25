package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class RequestProcessor implements Runnable {

  private Socket currentClientSocket;  //当前正在请求服务器的客户端Socket
  private User currentUser;  //当前正在请求服务器的客户端用户

  public RequestProcessor(Socket currentClientSocket) {
    this.currentClientSocket = currentClientSocket;
  }

  @Override
  public void run() {
    boolean flag = true; //是否不间断监听
    try {
      OnlineClientIOCache currentClientIOCache = new OnlineClientIOCache(
          new ObjectInputStream(currentClientSocket.getInputStream()),
          new ObjectOutputStream(currentClientSocket.getOutputStream()));
      while (flag) { //不停地读取客户端发过来的请求对象
        //从请求输入流中读取到客户端提交的请求对象
        Request request = (Request) currentClientIOCache.getOis().readObject();
        System.out.println("Server读取了客户端的请求:" + request.getType());

        Type typeName = request.getType();   //获取请求中的动作
        switch (typeName) {
          case register:
            register(currentClientIOCache, request);
            break;
          case login:
            login(currentClientIOCache, request);
            break;
          case chatList:
            chatList(currentClientIOCache, request);
            break;
          case getUserList:
            getUserList(currentClientIOCache);
            break;
          case privateChat:
            privateChat(request);
            break;
          case groupChat:
            groupChat(request);
            break;
        }
      }
    } catch (Exception e) {
      DataBuffer.onlineUsersList.remove(currentUser);
      DataBuffer.onlineUserIOCacheMap.remove(currentUser);
      logout(currentUser.getUsername());
      System.out.println("客户端:" + currentClientSocket.getLocalSocketAddress() + "已经断开连接");
    }
  }

  private void chatList(OnlineClientIOCache currentClientIOCache, Request request) {
    String username = request.getUsername();
    List <Message> messages = UserService.getChatList(username);
    System.out.println(messages);
    Message[] messageArray = new Message[messages.size()];
    messages.toArray(messageArray);
    Response response = new Response(Type.chatList);
    response.setValid(true);
    response.setMessages(messageArray);
    try {
      currentClientIOCache.getOos().writeObject(response);
      currentClientIOCache.getOos().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void register(OnlineClientIOCache currentClientIOCache,Request request) throws IOException {
    String username = request.getUsername();
    String password = request.getPassword();
    User newUser = new User(username, password);
    Response response = new Response(Type.register);
    if(UserService.checkUserName(username)) {
      response.setValid(false);
    } else {
      response.setValid(true);
      UserService.addUser(newUser);
    }
    ObjectOutputStream oos = currentClientIOCache.getOos();
    oos.writeObject(response);
    oos.flush();
  }

  private void logout(String username) {
    Response response = new Response(Type.haveLogout);
    response.setSender(username);
    for (User user : DataBuffer.onlineUsersList) {
      OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(user);
      try {
        io.getOos().writeObject(response);
        io.getOos().flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void login(OnlineClientIOCache currentClientIOCache, Request request)
      throws IOException {
    String userName = request.getUsername();
    String password = request.getPassword();
    Response response = new Response(Type.login);
    response.setValid(false);
    User newUser = new User(userName);
    if (!DataBuffer.onlineUsersList.contains(newUser) && UserService.login(userName, password)) {
      currentUser = newUser;
      DataBuffer.onlineUsersList.add(newUser);
      DataBuffer.onlineUserIOCacheMap.put(newUser, currentClientIOCache);
      response.setValid(true);
    }
    ObjectOutputStream oos = currentClientIOCache.getOos();
    oos.writeObject(response);
    oos.flush();
  }

  void getUserList(OnlineClientIOCache currentClientIOCache) throws IOException {
    Response response = new Response(Type.getUserList);
    response.setCurrentOnlineCnt(DataBuffer.onlineUsersList.size());
    String[] usersList = new String[DataBuffer.onlineUsersList.size()];
    for (User user : DataBuffer.onlineUsersList) {
      usersList[DataBuffer.onlineUsersList.indexOf(user)] = user.getUsername();
    }
    response.setUsersList(usersList);
    ObjectOutputStream oos = currentClientIOCache.getOos();
    oos.writeObject(response);
    oos.flush();
  }

  public void privateChat(Request request) throws IOException {
    Response response = new Response(Type.privateChat);
    response.setContent(request.getContent());
    response.setSender(request.getSender());
    response.setReceiver(request.getReceiver());
    UserService.addMessage(request.getSender(), request.getReceiver(), request.getContent());
    User receiver = new User(request.getReceiver());
    OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(receiver);
    io.getOos().writeObject(response);
    io.getOos().flush();
  }

  private void groupChat(Request request) {
    Response response = new Response(Type.groupChat);
    response.setContent(request.getContent());
    response.setSender(request.getSender());
    response.setGroupMembers(request.getGroupMembers());
    response.setGroupName(request.getGroupName());
    for(String mem: request.getGroupMembers()){
      if(mem.equals(request.getSender())){
        continue;
      }
      if(!DataBuffer.onlineUserIOCacheMap.containsKey(new User(mem))){
        continue;
      }
      UserService.addMessage(request.getSender(), mem, request.getContent(), request.getGroupName(), String.join(",", request.getGroupMembers()));
      System.out.println(request.getSender() + "把群聊消息发送给:" + mem);
      User receiver = new User(mem);
      OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(receiver);
      try {
        io.getOos().writeObject(response);
        io.getOos().flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
