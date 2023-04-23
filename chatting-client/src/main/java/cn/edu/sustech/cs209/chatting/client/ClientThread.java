package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClientThread extends Thread {

  Controller controller;

  public void run() {
    while (DataBuffer.clientSeocket.isConnected()) {
      try {
        Response r = (Response) DataBuffer.ois.readObject();
        switch (r.getType()) {
          case getUserList:
            DataBuffer.userList = Arrays.asList(r.getUsersList());
            break;
          case chat:
//            controller.addMessage(r.getSender(), r.getMessage());
            break;
          default:
            break;
        }

      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
