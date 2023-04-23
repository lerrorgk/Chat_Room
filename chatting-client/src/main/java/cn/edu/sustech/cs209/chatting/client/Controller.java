package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.Response;
import cn.edu.sustech.cs209.chatting.common.Type;
import cn.edu.sustech.cs209.chatting.common.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {

  @FXML
  ListView<Message> chatContentList;

  @FXML
  ListView<String> chatList;

  @FXML
  TextArea inputArea;

  User aUser = new User();

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    Dialog<String> dialog = new TextInputDialog();
    dialog.setTitle("Login");
    dialog.setHeaderText(null);
    dialog.setContentText("Username:");

    String ip = DataBuffer.configMap.get("ip");
    int port = Integer.parseInt(DataBuffer.configMap.get("port"));
    try {
      DataBuffer.clientSeocket = new Socket(ip, port);
      DataBuffer.oos = new ObjectOutputStream(DataBuffer.clientSeocket.getOutputStream());
      DataBuffer.ois = new ObjectInputStream(DataBuffer.clientSeocket.getInputStream());
    } catch (Exception e) {
      dialog.setHeaderText("Cannot connect to server");
    }

    Optional<String> input = dialog.showAndWait();
    if (input.isPresent() && !input.get().isEmpty()) {
            /*
               TODO: Check if there is a user with the same name among the currently logged-in users,
                     if so, ask the user to change the username
             */
      Request request = new Request();
      request.setType(Type.checkUserName);
      request.setAttribute("username", input.get());
      try {
        Response b = ClientUtil.sendRequest(request);
        if (b.isValid()) {
          aUser.setUsername(input.get());
          DataBuffer.currentUser = aUser;
        } else {
          dialog.setHeaderText("Username " + input.get() + " already exists, exiting");
          Platform.exit();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      dialog.setHeaderText("Invalid username " + input + ", exiting");
      Platform.exit();
    }

    ClientThread clientThread = new ClientThread();
    clientThread.start();
    chatContentList.setCellFactory(new MessageCellFactory());
    chatList.setCellFactory(new chatListCellFactory());
  }

  @FXML
  public void createPrivateChat() {
    AtomicReference<String> user = new AtomicReference<>();

    Stage stage = new Stage();
    ComboBox<String> userSel = new ComboBox<>();

    Request request = new Request();
    request.setType(Type.getUserList);
    try {
      ClientUtil.sendRequestNoResponse(request);
      Thread.sleep(1000);
      for (String u : DataBuffer.userList) {
        if (!u.equals(DataBuffer.currentUser.getUsername())) {
          userSel.getItems().add(u);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // FIXME: get the user list from server, the current user's name should be filtered out
//    userSel.getItems().addAll("Item 1", "Item 2", "Item 3");

    Button okBtn = new Button("OK");
    okBtn.setOnAction(e -> {
      user.set(userSel.getSelectionModel().getSelectedItem());
      stage.close();
    });

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(20, 20, 20, 20));
    box.getChildren().addAll(userSel, okBtn);
    stage.setScene(new Scene(box));
    stage.showAndWait();

    if (user.get() == null) {
      return;
    }

    // TODO: if the current user already chatted with the selected user, just open the chat with that user
    // TODO: otherwise, create a new chat item in the left panel, the title should be the selected user's name
    for (String s : chatList.getItems()) {
      if (s.equals(user.get())) {
        chatList.getSelectionModel().select(s);
        return;
      }
    }
    chatList.getItems().add(user.get());
    chatList.getSelectionModel().select(user.get());
  }

  /**
   * A new dialog should contain a multi-select list, showing all user's name. You can select
   * several users that will be joined in the group chat, including yourself.
   * <p>
   * The naming rule for group chats is similar to WeChat: If there are > 3 users: display the first
   * three usernames, sorted in lexicographic order, then use ellipsis with the number of users, for
   * example: UserA, UserB, UserC... (10) If there are <= 3 users: do not display the ellipsis, for
   * example: UserA, UserB (2)
   */
  @FXML
  public void createGroupChat() {
  }

  /**
   * Sends the message to the <b>currently selected</b> chat.
   * <p>
   * Blank messages are not allowed. After sending the message, you should clear the text input
   * field.
   */
  @FXML
  public void doSendMessage() {
    // TODO
    String msg = inputArea.getText();
    if (msg.equals("")) {
      return;
    }
    System.out.println("doSendMessage () :msg: " + msg);
    String receiver = chatList.getSelectionModel().getSelectedItem();
    Message message = new Message(aUser, receiver, msg);
    chatContentList.getItems().add(message);

    inputArea.setText("");
    Request request = new Request();
    request.setType(Type.chat);
    request.setAttribute("message", message);
    try {
      ClientUtil.sendRequestNoResponse(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private class chatListCellFactory implements Callback<ListView<String>, ListCell<String>> {

    @Override
    public ListCell<String> call(ListView<String> param) {
      return new ListCell<String>() {
        @Override
        public void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || Objects.isNull(item)) {
            setText(null);
            setGraphic(null);
            return;
          }

          HBox wrapper = new HBox();
          Label nameLabel = new Label(item);
          nameLabel.setPrefSize(50, 20);
          nameLabel.setWrapText(true);
          nameLabel.setAlignment(Pos.CENTER_LEFT);
          wrapper.getChildren().add(nameLabel);
          setGraphic(wrapper);
        }
      };
    }
  }

  /**
   * You may change the cell factory if you changed the design of {@code Message} model. Hint: you
   * may also define a cell factory for the chats displayed in the left panel, or simply override
   * the toString method.
   */
  private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {

    @Override
    public ListCell<Message> call(ListView<Message> param) {
      return new ListCell<Message>() {

        @Override
        public void updateItem(Message msg, boolean empty) {
          super.updateItem(msg, empty);
          if (empty || Objects.isNull(msg)) {
            return;
          }

          HBox wrapper = new HBox();
          Label nameLabel = new Label(msg.getSender().getUsername());
          Label msgLabel = new Label(msg.getContent());

          nameLabel.setPrefSize(50, 20);
          nameLabel.setWrapText(true);
          nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

          if (aUser.equals(msg.getSender())) {
            wrapper.setAlignment(Pos.TOP_RIGHT);
            wrapper.getChildren().addAll(msgLabel, nameLabel);
            msgLabel.setPadding(new Insets(0, 20, 0, 0));
          } else {
            wrapper.setAlignment(Pos.TOP_LEFT);
            wrapper.getChildren().addAll(nameLabel, msgLabel);
            msgLabel.setPadding(new Insets(0, 0, 0, 20));
          }

          setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
          setGraphic(wrapper);
        }
      };
    }
  }

}
