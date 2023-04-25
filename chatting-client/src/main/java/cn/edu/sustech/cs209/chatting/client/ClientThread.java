package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import com.vdurmont.emoji.EmojiParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class ClientThread extends Thread {

  Controller controller;

  public ClientThread(Controller controller) {
    this.controller = controller;
  }

  public void run() {
    while (DataBuffer.clientSeocket.isConnected()) {
      try {
        Response r = (Response) DataBuffer.ois.readObject();
        switch (r.getType()) {
          case getUserList:
            DataBuffer.userList = Arrays.asList(r.getUsersList());
            break;
          case privateChat:
            updatePrivateMessage(r.getSender(), r.getReceiver(), r.getContent());
            break;
          case groupChat:
            updateGroupMessage(r.getSender(), r.getReceiver(), r.getGroupName(),
                r.getGroupMembers(), r.getContent());
            break;
          case haveLogout:
            haveLogout(r.getSender());
            break;
          default:
            break;
        }

      } catch (IOException | ClassNotFoundException e) {
        notConnected();
        throw new RuntimeException(e);
      }
    }
  }

  private void haveLogout(String sender) {
    Platform.runLater(() -> {
      controller.haveLogout(sender);
    });
  }


  public void notConnected() {
    Platform.runLater(() -> {
      try {
        controller.notConnected();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void updatePrivateMessage(String sender, String receiver, String content) {
    Platform.runLater(() -> {

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("New Message");
      alert.setHeaderText("You have a new message from " + sender);
      alert.setContentText(content);
      alert.showAndWait();

      String result = EmojiParser.parseToUnicode(content);
      try {
        controller.addPrivateMessage(sender, receiver, result);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void updateGroupMessage(String sender, String receiver, String groupName,
      String[] groupMembers, String content) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("New Message");
      alert.setHeaderText("You have a new message from " + sender + " in group " + groupName);
      alert.setContentText(content);
      alert.showAndWait();

      String result = EmojiParser.parseToUnicode(content);
      controller.addGroupMessage(sender, receiver, groupName, groupMembers, result);
    });
  }
}
