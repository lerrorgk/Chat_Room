package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.Response;
import java.io.IOException;

public class ClientUtil {


  public static Response sendRequest(Request request) throws IOException {
    Response response = null;
    try {
      // 发送请求
      DataBuffer.oos.writeObject(request);
      DataBuffer.oos.flush();
      response = (Response) DataBuffer.ois.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return response;
  }


  public static void sendRequestNoResponse(Request request) throws IOException {
    try {
      DataBuffer.oos.writeObject(request); // 发送请求
      DataBuffer.oos.flush();
//      System.out.println("客户端发送了请求对象:" + request.getType());
    } catch (IOException e) {
      throw e;
    }
  }

}
