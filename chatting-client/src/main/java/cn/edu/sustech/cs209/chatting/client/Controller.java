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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

  @FXML
  Label currentUsername;


  User aUser = new User();

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Login");
    dialog.setHeaderText(null);
    dialog.setContentText("Username:");

    VBox vBox = new VBox();
    vBox.getChildren().add(dialog.getEditor());
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Password:");

    vBox.getChildren().add(passwordField);

    String ip = DataBuffer.configMap.get("ip");
    int port = Integer.parseInt(DataBuffer.configMap.get("port"));
    try {
      DataBuffer.clientSeocket = new Socket(ip, port);
      DataBuffer.oos = new ObjectOutputStream(DataBuffer.clientSeocket.getOutputStream());
      DataBuffer.ois = new ObjectInputStream(DataBuffer.clientSeocket.getInputStream());
    } catch (Exception e) {
      dialog.setHeaderText("Cannot connect to server");
    }

    Button register = new Button();
    register.setText("Register");
    vBox.getChildren().add(register);
    register.setOnAction((ActionEvent e) -> {

      String username = dialog.getEditor().getText();
      String password = passwordField.getText();

      Request request = new Request(Type.register);
      request.setUsername(username);
      request.setPassword(password);
      try {
        Response r = ClientUtil.sendRequest(request);
        if (r.isValid()) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Success");
          alert.setHeaderText("Register success");
          alert.setContentText("Please login");
          alert.showAndWait();
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("Register failed");
          alert.setContentText("Username already exists");
          alert.showAndWait();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }

    });

    dialog.getDialogPane().setContent(vBox);

    Optional<String> input = dialog.showAndWait();
    Optional<String> password = Optional.of(passwordField.getText());

    while (!input.isPresent() || input.get().isEmpty()) {
      dialog.setHeaderText("Username cannot be empty");
      input = dialog.showAndWait();
    }
    do {
      Request request = new Request(Type.register);
      request.setType(Type.login);
      request.setUsername(input.get());
      request.setPassword(password.get());
      try {
        Response b = ClientUtil.sendRequest(request);
        if (b.isValid()) {
          aUser.setUsername(input.get());
          currentUsername.setText(aUser.getUsername());
          DataBuffer.currentUser = aUser;
          break;
        } else {
          dialog.setHeaderText("Invalid username");
          input = dialog.showAndWait();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } while (true);
    /*
       TODO: Check if there is a user with the same name among the currently logged-in users,
             if so, ask the user to change the username
     */

    chatContentList.setCellFactory(new MessageCellFactory());
    chatList.setCellFactory(new chatListCellFactory());

//    Request request2 = new Request(Type.chatList);
//    request2.setUsername(input.get());
//    try {
//      Response b = ClientUtil.sendRequest(request2);
//      System.out.println(b.isValid());
//      if (b.isValid()) {
//        Message[] messages = b.getMessages();
//        System.out.println("b.getMessages():" + messages.length);
//        for(Message m : messages){
//          System.out.println(m.getSender().getUsername() + " " + m.getReceiver().getUsername() + " " + m.getContent());
//        }
//        for (Message m : messages) {
////          if (m.getGroupName() != null) {
////            addGroupMessage(m.getSender().getUsername(), m.getReceiver().getUsername(),
////                m.getGroupName(),
////                m.getGroupMembers(), m.getContent());
////          }
//          System.out.println(m.getSender().getUsername() + " " + m.getReceiver().getUsername() + " "
//              + m.getContent());
//          addPrivateMessage(m.getSender().getUsername(), m.getReceiver().getUsername(),
//              m.getContent());
//        }
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (InterruptedException e) {
//      throw new RuntimeException(e);
//    }

    ClientThread clientThread = new ClientThread(this);
    clientThread.start();
  }

  @FXML
  public void createPrivateChat() {
    AtomicReference<String> user = new AtomicReference<>();

    Stage stage = new Stage();
    ComboBox<String> userSel = new ComboBox<>();

    Request request = new Request(Type.register);
    request.setType(Type.getUserList);
    try {
      ClientUtil.sendRequestNoResponse(request);
      Thread.sleep(500);
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
        updateMessages(aUser.getUsername(), user.get());
        return;
      }
    }
    chatList.getItems().add(user.get());
    chatList.getSelectionModel().select(user.get());
    updateMessages(aUser.getUsername(), user.get());
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
    Vector<String> users = new Vector<>();
    Stage stage = new Stage();
    ListView<String> userSel = new ListView<>();
    userSel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    Request request = new Request(Type.register);
    request.setType(Type.getUserList);
    try {
      ClientUtil.sendRequestNoResponse(request);
      Thread.sleep(500);
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
//    System.out.println("userSel.getItems()" + userSel.getItems());

    Button okBtn = new Button("OK");
    okBtn.setOnAction(e -> {
      users.addAll(userSel.getSelectionModel().getSelectedItems());
      stage.close();
    });

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(20, 20, 20, 20));
    box.getChildren().addAll(userSel, okBtn);
    stage.setScene(new Scene(box));
    stage.showAndWait();

    if (users.isEmpty()) {
      return;
    }
    String groupName = "";
    users.add(aUser.getUsername());
    List<String> distinctList = users.stream()
        .distinct()
        .sorted()
        .collect(Collectors.toList());
    if (distinctList.size() > 3) {
      Collections.sort(distinctList);
      groupName =
          distinctList.get(0) + ", " + distinctList.get(1) + ", " + distinctList.get(2) + "... ("
              + distinctList.size() + ")";
    } else {
      groupName =
          distinctList.get(0) + ", " + distinctList.get(1) + " (" + distinctList.size() + ")";
    }
    for (String s : chatList.getItems()) {
      if (s.equals(groupName)) {
        chatList.getSelectionModel().select(s);
        return;
      }
    }
    chatList.getItems().add(groupName);
    chatList.getSelectionModel().select(groupName);
    DataBuffer.groupMap.put(groupName, String.join(",", distinctList));
    updateMessages(groupName);
  }

  /**
   * Sends the message to the <b>currently selected</b> chat.
   * <p>
   * Blank messages are not allowed. After sending the message, you should clear the text input
   * field.
   */
  @FXML
  public void doSendMessage() throws InterruptedException {
    // TODO
    String msg = inputArea.getText();
    if (msg.equals("")) {
      return;
    }

    String receiver = chatList.getSelectionModel().getSelectedItem();
//    System.out.println("receiver:" + receiver);
//    System.out.println("DataBuffer.groupMap:" + DataBuffer.groupMap);
    if (DataBuffer.groupMap.containsKey(receiver)) {
      inputArea.setText("");
      Request request = new Request(Type.register);
      request.setType(Type.groupChat);
      request.setSender(aUser.getUsername());
      request.setGroupMessage(true);
      request.setGroupName(receiver);
      String[] members = DataBuffer.groupMap.get(receiver).split(",");
      String[] uniqueMembers = Arrays.stream(members)
          .distinct()
          .toArray(String[]::new);
      request.setGroupMembers(uniqueMembers);
      request.setContent(msg);
      try {
        ClientUtil.sendRequestNoResponse(request);
      } catch (IOException e) {
        e.printStackTrace();
      }

      Message message = new Message(aUser, msg, true, receiver, uniqueMembers);
//      System.out.println("uniqueMembers:" + Arrays.toString(uniqueMembers));
      DataBuffer.messageList.add(message);
      updateMessages(receiver);

      return;
    }
    Message message = new Message(aUser, receiver, msg);
    DataBuffer.messageList.add(message);
    updateMessages(aUser.getUsername(), receiver);

    inputArea.setText("");
    Request request = new Request(Type.register);
    request.setType(Type.privateChat);
    request.setSender(aUser.getUsername());
    request.setReceiver(receiver);
    request.setContent(msg);
    try {
      ClientUtil.sendRequestNoResponse(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void addPrivateMessage(String sender, String receiver, String message)
      throws InterruptedException {

    boolean isChoose = false;
    for (String s : chatList.getItems()) {
      if (s.equals(sender)) {
        chatList.getSelectionModel().select(s);
        isChoose = true;
        break;
      }
    }
    if (!isChoose) {
      chatList.getItems().add(sender);
      chatList.getSelectionModel().select(sender);
    }
    Message msg = new Message(sender, receiver, message);
    DataBuffer.messageList.add(msg);
    updateMessages(sender, receiver);
  }

  public void updateMessages(String sender, String receiver) {
    List<Message> messages = new ArrayList<>();
//    System.out.println("p before");
//    for(Message s : chatContentList.getItems()) {
//      System.out.println(s);
//    }
    for (Message m : DataBuffer.messageList) {
      if (m.isGroupMessage()) {
        continue;
      }
      if (m.getSender().getUsername().equals(sender) && m.getReceiver().getUsername()
          .equals(receiver)) {
        messages.add(m);
      } else if (m.getSender().getUsername().equals(receiver) && m.getReceiver().getUsername()
          .equals(sender)) {
        messages.add(m);
      }
    }
    chatContentList.getItems().clear();
    chatContentList.getItems().addAll(messages);
//    System.out.println("p after");
//    for(Message s : chatContentList.getItems()) {
//      System.out.println(s);
//    }
  }

  public void addGroupMessage(String sender, String receiver, String groupName,
      String[] groupMembers, String content) {
    boolean isChoose = false;
    for (String s : chatList.getItems()) {
      if (s.equals(groupName)) {
        chatList.getSelectionModel().select(s);
        isChoose = true;
        break;
      }
    }
    if (!isChoose) {
      chatList.getItems().add(groupName);
      chatList.getSelectionModel().select(groupName);
    }
    DataBuffer.groupMap.put(groupName, String.join(",", groupMembers));
    Message msg = new Message(sender, receiver, content, true, groupName, groupMembers);
    DataBuffer.messageList.add(msg);
    updateMessages(groupName);
  }

  public void updateMessages(String groupName) {
//    System.out.println("g after");
//    for(Message s : chatContentList.getItems()) {
//      System.out.println(s);
//    }
//    System.out.println("g DataBuffer.messageList:");
//    for(Message s : DataBuffer.messageList) {
//      System.out.println(s);
//    }
    List<Message> messages = new ArrayList<>();
    for (Message m : DataBuffer.messageList) {
      if (!m.isGroupMessage()) {
        continue;
      }
      if (m.getGroupName().equals(groupName)) {
        messages.add(m);
      }
    }
    chatContentList.getItems().clear();
    chatContentList.getItems().addAll(messages);
//    System.out.println("g after");
//    for(Message s : chatContentList.getItems()) {
//      System.out.println(s);
//    }
  }

  public void notConnected() throws InterruptedException {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Not connected");
    alert.setContentText("Please connect to the server first");
    alert.showAndWait();
    Platform.exit();
  }

  public void getGroupChat(ActionEvent actionEvent) {
    Stage stage = new Stage();
    ListView<String> list = new ListView<>();

    String groupName = chatList.getSelectionModel().getSelectedItem();
    if (groupName == null) {
      return;
    }
    if (DataBuffer.groupMap.containsKey(groupName)) {
      String[] members =
          DataBuffer.groupMap.get(groupName).split(",");
      list.getItems().addAll(members);
    }

    Button okBtn = new Button("OK");
    okBtn.setOnAction(e -> {
      stage.close();
    });

    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(20, 20, 20, 20));
    box.getChildren().addAll(list, okBtn);
    stage.setScene(new Scene(box));
    stage.showAndWait();
  }

  public void haveLogout(String sender) {
    if (sender.equals(chatList.getSelectionModel().getSelectedItem())) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("User logout");
      alert.setContentText("The user you are chatting with has logged out");
      alert.showAndWait();
    }

    if (DataBuffer.groupMap.containsKey(chatList.getSelectionModel().getSelectedItem())) {
      String groupName = chatList.getSelectionModel().getSelectedItem();
      String[] members = DataBuffer.groupMap.get(groupName).split(",");
      for (String s : members) {
        if (s.equals(sender)) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("User logout");
          alert.setContentText("The user " + sender + " has logged out");
          alert.showAndWait();
          break;
        }
      }
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

          nameLabel.setOnMouseClicked(
              e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                  if (DataBuffer.groupMap.containsKey(item)) {
                    updateMessages(item);
                  } else {
                    updateMessages(aUser.getUsername(), item);
                  }
                }
              }
          );
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
            setText(null);
            setGraphic(null);
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
