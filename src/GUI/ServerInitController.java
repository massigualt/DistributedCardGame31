package GUI;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class ServerInitController implements Initializable, ControlledScreen {

    ScreensController myController;

    private static final Integer STARTTIME = 15;
    private static final Integer ENDTIME = 0;
    private static final Integer MAXUSSERS = 8;
    private static final Integer MINUSERS = 2;

    private Timeline timeline;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME*100);


    ObservableList<String> usersList = FXCollections.<String>observableArrayList("Apple", "Banana", "Orange", "Mango", "Banana", "Orange", "Mango", "Banana", "Orange", "Mango", "Banana", "Orange", "Mango", "Banana", "Orange", "Mango");
    private int usersSize;

    @FXML
    private Label timerLabel, startLabel;

    @FXML
    private Button startButton;

    @FXML
    private ListView listUsers;

    private void updateTime() {
        // increment seconds
        int seconds = timeSeconds.get();
        timeSeconds.set(seconds-1);

        if (seconds==ENDTIME) {
            timerLabel.setTextFill(Color.TRANSPARENT);
            timeline.stop();
            usersSize = usersList.size();
            if(usersSize<=MAXUSSERS && usersSize>=MINUSERS){
                startLabel.setText("Server chiuso, numero di utenti connessi: "+ usersSize);
            }else{
                if(usersSize<MINUSERS){
                    startLabel.setText("Numero di utenti minimo non raggiunto");
                }else{
                    startLabel.setText("Numero di utenti massimo raggiunto");
                }
            }

            System.out.println("TIME OUT!");
        }
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        timerLabel.textProperty().bind(timeSeconds.asString());
        timerLabel.setTextFill(Color.TRANSPARENT);
    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML
    private void startServer(ActionEvent event){

        //myController.setScreen(ScreensFramework.screenTimer);

        timerLabel.setTextFill(Color.RED);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> updateTime()));
        timeline.setCycleCount(Animation.INDEFINITE); // repeat over and over again
        timeSeconds.set(STARTTIME);
        timeline.play();
        startButton.setDisable(true);

        listUsers.getItems().addAll(usersList);

    }

}


