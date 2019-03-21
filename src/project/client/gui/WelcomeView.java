package project.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.client.main.ServerConnector;
import project.server.ChatInterface;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WelcomeView implements Initializable {
    private String username;
    private ChatInterface chat;
    List<String> users = new ArrayList<>();

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button btnLogin;


    @FXML
    public void eventLogin(ActionEvent event) {
        if (isValidTextField()) {
            String username = this.usernameTextField.getText();
            try {
                users = chat.getAllUsers();
            } catch (RemoteException e) {
                Logger.getLogger(WelcomeView.class.getName()).log(Level.SEVERE, null, e);
            }
            if (users.contains(username)) {
                alertMessage(Alert.AlertType.ERROR, "Erorre Username", "l'username inserito è già presente!");
            } else {
                // passo nella snuova sezione
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    String path = "/project/client/gui/chat_view.fxml";
                    fxmlLoader.setLocation(getClass().getResource(path));
                    Parent viewParent = fxmlLoader.load();
                    Scene viewScene = new Scene(viewParent);

                    ChatView chatController = fxmlLoader.getController();
                    chatController.initData(chat, username);

                    //This line gets the Stage information
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(viewScene);
                    window.show();

                } catch (RemoteException e) {
                    System.out.println("RemoteException: " + e.getMessage());
                    Logger.getLogger(WelcomeView.class.getName()).log(Level.SEVERE, null, e);

                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage());
                }
            }
        }

    }

    private boolean isValidTextField() {
        if (usernameTextField.getText() == null || usernameTextField.getText().trim().isEmpty()) {
            alertMessage(Alert.AlertType.WARNING, "ControllerUsername", "Compilare il campo Username!");
            return false;
        }
        return true;
    }

    private void alertMessage(Alert.AlertType type, String title, String info) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chat = ServerConnector.getServerConnector().getChat();
    }
}
