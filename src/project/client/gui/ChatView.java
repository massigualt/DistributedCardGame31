package project.client.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import project.server.ChatInterface;
import project.server.Message;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatView implements Initializable {

    private String username;
    private ChatInterface chat;
    private List<Message> msgs = new ArrayList<>();
    private List<String> users = new ArrayList<>();

    @FXML
    private Label usernameLabel;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextArea usersTextArea;
    @FXML
    private Button sendMessageBtn;

    public void initData(ChatInterface chat, String username) throws RemoteException {
        this.chat = chat;
        this.username = username;
        this.chat.login(username);
        this.usernameLabel.setText("Ciao: " + username);
        startChat();
    }

    private void startChat() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    refresh();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        });
        t1.start();
    }

    private void refresh() {
        try {
            displayChatList();
            displayUserList();
        } catch (RemoteException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayChatList() throws RemoteException {
        chatTextArea.setText("");
        msgs = chat.getAllMesage();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (Message m : msgs) {
            chatTextArea.appendText(" " + dateFormat.format(m.getDate()) + " " + m.getUsername() + m.getMsg() + "\n");
        }
    }

    private void displayUserList() throws RemoteException {
        usersTextArea.setText("");
        users = chat.getAllUsers();
        for (String u : users) {
            usersTextArea.appendText(" " + u + "\n");
        }
    }

    @FXML
    public void sendNewMessage() {
        String inputMessage = messageTextField.getText();
        messageTextField.setText("");
        Message message = new Message(this.username, " " + inputMessage, "client", new Date());

        try {
            chat.sendMessage(message);
        } catch (RemoteException e) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usersTextArea.setText("");
        usersTextArea.setEditable(false);
        chatTextArea.setText("");
        chatTextArea.setEditable(false);
    }
}
