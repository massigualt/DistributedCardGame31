package GUI;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import distributedLogic.client.StartClient;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Angie
 */
public class LoginController implements Initializable, ControlledScreen {

    ScreensController myController;
    StartClient client = new StartClient();


    @FXML
    private TextField username;

    @FXML
    private TextField serverIP;

    @FXML
    private Label statusLabel;

    private String playerUsername, serverAddress;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    @FXML
    private void goToScreen2(ActionEvent event) {
        myController.setScreen(ScreensFramework.screenServerInit);
    }

    @FXML
    private void buttonPlay(ActionEvent event){
        playerUsername = username.getText();
        serverAddress = serverIP.getText();
        startClient();
    }

    private void startClient(){

        statusLabel.setTextFill(Color.RED);
        statusLabel.setText("Waiting for other client");
        /*try {
            client.setPlayerName(playerUsername);
            client.setServer(serverAddress);
            client.initGame();
        } catch (RemoteException e) {
            System.out.println("Errore" + e.getMessage());
        }*/

        myController.setScreen(ScreensFramework.screenGameInit);

    }
}
