package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class RequestProcessor implements Runnable{
  private Socket currentClientSocket;  //当前正在请求服务器的客户端Socket

  public RequestProcessor(Socket currentClientSocket){
    this.currentClientSocket = currentClientSocket;
  }

  @Override
  public void run() {
    boolean flag = true; //是否不间断监听
    try{
      OnlineClientIOCache currentClientIOCache = new OnlineClientIOCache(
          new ObjectInputStream(currentClientSocket.getInputStream()),
          new ObjectOutputStream(currentClientSocket.getOutputStream()));
      while(flag){ //不停地读取客户端发过来的请求对象
        //从请求输入流中读取到客户端提交的请求对象
        Request request = (Request)currentClientIOCache.getOis().readObject();
        System.out.println("Server读取了客户端的请求:" + request.getType());

        Type typeName = request.getType();   //获取请求中的动作
        switch (typeName) {
          case checkUserName:
            checkUserName(currentClientIOCache, request);
            break;
          case getUserList:
            getUserList(currentClientIOCache);
            break;
          case chat:
            chat(request);
            break;
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  private void checkUserName(OnlineClientIOCache currentClientIOCache, Request request) throws IOException {
    String userName = (String)request.getAttribute("username");
    Response response = new Response(Type.checkUserName);
    response.setValid(false);
    User newUser = new User(userName);
    if(!DataBuffer.onlineUsersList.contains(newUser)) {
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
    String[] usersList = new String[DataBuffer.onlineUsersList.size()];
    for(User user : DataBuffer.onlineUsersList){
      usersList[DataBuffer.onlineUsersList.indexOf(user)] = user.getUsername();
    }
    response.setUsersList(usersList);
    ObjectOutputStream oos = currentClientIOCache.getOos();
    oos.writeObject(response);
    oos.flush();
  }

  public void chat(Request request) throws IOException{
    Message msg = (Message)request.getAttribute("msg");
    OnlineClientIOCache io = DataBuffer.onlineUserIOCacheMap.get(msg.getReceiver());
    io.getOos().writeObject(msg);
    io.getOos().flush();
  }
}
