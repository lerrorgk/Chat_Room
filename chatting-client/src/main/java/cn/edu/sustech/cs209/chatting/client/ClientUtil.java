package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientUtil {

  /** 发送请求对象,主动接收响应 */
  public static Response sendRequest(Request request) throws IOException {
    Response response = null;
    try {
      // 发送请求
      DataBuffer.oos.writeObject(request);
      DataBuffer.oos.flush();
      System.out.println("客户端发送了请求对象:" + request.getType());

      if(!"exit".equals(request)){
        // 获取响应
        response = (Response) DataBuffer.ois.readObject();
        System.out.println("客户端获取到了响应对象:" + response.getUsersList());
      }else{
        System.out.println("客户端断开连接了");
      }
    } catch (IOException e) {
      throw e;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return response;
  }

  /** 发送请求对象,不主动接收响应 */
  public static void sendRequestNoResponse(Request request) throws IOException {
    try {
      DataBuffer.oos.writeObject(request); // 发送请求
      DataBuffer.oos.flush();
      System.out.println("客户端发送了请求对象:" + request.getType());
    } catch (IOException e) {
      throw e;
    }
  }

}
