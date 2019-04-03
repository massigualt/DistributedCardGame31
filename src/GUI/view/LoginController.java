package GUI.view;

import java.net.URL;
import java.util.ResourceBundle;

import distributedLogic.game.ClientLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class LoginController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private TextField serverIP;

    @FXML
    private Label statusLabel;

    @FXML
    private Button startButton;

    private String playerUsername, serverAddress;

    private static Boolean canContinue = false;
    private ClientLogic clientLogic;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.startButton.setDefaultButton(true);
        this.username.setText("Emilio");
        this.serverIP.setText("192.168.1.142");
        this.canContinue = false;
    }


    @FXML
    private void buttonPlay(ActionEvent event) {
        if (username.getText().equals(null) || username.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "You don't add a username!");
            a.show();
        } else if (serverIP.getText().equals(null) || serverIP.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "You don't add IpServer!");
            a.show();
        } else {
            playerUsername = username.getText();
            serverAddress = serverIP.getText();
            clientLogic = new ClientLogic(playerUsername, serverAddress, this);

            setDisable();
            clientLogic.startClient(event);
        }
    }


    private void setDisable() {
        this.username.setDisable(true);
        this.serverIP.setDisable(true);
        this.startButton.setDisable(true);
    }

    public void setCanContinue() {
        this.canContinue = true;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public static Boolean getCanContinue() {
        return canContinue;
    }
}
