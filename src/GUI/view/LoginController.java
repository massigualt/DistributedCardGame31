package GUI.view;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import distributedLogic.game.ClientLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


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
    private Alert alert;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.startButton.setDefaultButton(true);
        this.username.setText("Player");
        this.serverIP.setText("192.168.1.142");
        this.canContinue = false;
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.setTitle("Information Dialog");
        this.alert.setHeaderText(null);
    }


    @FXML
    private void buttonPlay(ActionEvent event) {
        if (this.username.getText().isEmpty() || this.serverIP.getText().isEmpty()) {
            this.alert.setContentText("You don't add a username or serverIP!");
            this.alert.showAndWait();

// TODO da inserire quando Vittoria, errore connessione, etc
//            Optional<ButtonType> result = alert.showAndWait();
//            if (!result.isPresent()) {
//                System.exit(0);
//            } else if (result.get() == ButtonType.OK) {
//                System.exit(0);
//            }
        } else {
            this.playerUsername = username.getText();
            this.serverAddress = serverIP.getText();
            this.clientLogic = new ClientLogic(this.playerUsername, this.serverAddress, this);

            setDisable();
            clientLogic.startClient(event);
        }
    }


    private void setDisable() {
        this.username.setDisable(true);
        this.serverIP.setDisable(true);
        this.startButton.setDisable(true);
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public void setCanContinue() {
        this.canContinue = true;
    }

    public Alert getAlert() {
        return alert;
    }

    public static Boolean getCanContinue() {
        return canContinue;
    }
}
