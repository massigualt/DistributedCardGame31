package GUI;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import distributedLogic.client.StartClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


public class Screen2Controller implements Initializable , ControlledScreen {

    ScreensController myController;
    StartClient client = new StartClient();

    String username, serverIP;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML
    private void goToScreen1(ActionEvent event){
       myController.setScreen(ScreensFramework.screenLoginID);
    }
    
    @FXML
    private void goToScreen3(ActionEvent event){
       myController.setScreen(ScreensFramework.screen3ID);
    }

}
