package GUI;

import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import project.Utility;
import project.server.ChatServer;
import project.server.Server;



public class ServerInitController implements Initializable, ControlledScreen {

    ScreensController myController;

    private static final Integer STARTTIME = 35;
    private static final Integer ENDTIME = 0;
    private static final Integer MAXUSSERS = 8;
    private static final Integer MINUSERS = 2;

    private Timeline timeline;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME*100);
    private ChatServer server;
    private Utility utils;

    private int NumHost;
    private Boolean timerStopped = false;



    ObservableList<String> usersList = FXCollections.<String>observableArrayList();
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
        System.out.println("LISTA UTENTI: "+ this.server.getUsersList());

        System.out.println("PRIMA I SECONDI "+ NumHost);

        if (seconds==ENDTIME) {
            timerLabel.setTextFill(Color.TRANSPARENT);
            timeline.stop();
            usersList= this.server.getUsersList();
            listUsers.getItems().addAll(usersList);
            usersSize = usersList.size();
            if(usersSize<=MAXUSSERS && usersSize>=MINUSERS){
                startLabel.setText("Server chiuso, numero di utenti connessi: "+ usersSize);
                NumHost = usersSize;
            }else{
                if(usersSize<MINUSERS){
                    startLabel.setText("Numero di utenti minimo non raggiunto");

                }else{
                    startLabel.setText("Numero di utenti massimo raggiunto");
                    NumHost = MAXUSSERS;
                }
            }
            timerStopped = true;
            System.out.println("TIME OUT!");
            System.out.println("DOPO I SECONDI "+ NumHost);
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
    private void startServer(ActionEvent event) throws Exception {

        //myController.setScreen(ScreensFramework.screenTimer);

        timerLabel.setTextFill(Color.RED);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> updateTime()));
        timeline.setCycleCount(Animation.INDEFINITE); // repeat over and over again
        timeSeconds.set(STARTTIME);
        timeline.play();
        startButton.setDisable(true);

        serverDriver2();
        //listUsers.getItems().addAll(usersList);

    }

    public void serverDriver(){
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            this.server = new ChatServer();

            System.out.println("LISTA UTENTI: "+ server.printUsers());

            System.out.println("Starting Server");
            //registry.rebind("rmi://localhost/"+ Server.DEFAULT_NAME, server);
            registry.rebind(Server.DEFAULT_NAME, server);

            System.out.println("Server ready: "+ registry.list());
        } catch (RemoteException e) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Server start problem "+ e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Exception  "+ e.getMessage());
        }
    }

    public void serverDriver2() throws Exception {

        String IP = utils.TrovaIp();

        String user = System.getProperty("user.dir");
        System.setProperty("java.rmi.server.hostname", IP);

        try {
            LocateRegistry.createRegistry(1099);
            this.server = new ChatServer();
            Naming.rebind("prova", server);

        } catch (RemoteException e) {
            LocateRegistry.getRegistry(1099).list();
            System.out.println("rmiregistry already started");
        }

         //Avvio il server RMI
        if (timerStopped == true){
            try {
                System.out.println("Numero di host selezionati: "+ NumHost);
                /*//SStartRMI = new CSConn(NumHosts);

                //LocateRegistry.createRegistry(1999 + MyConnectionID);
                Naming.rebind("rmi://" + IP + "/SConn", SStartRMI);
                System.out.println("server ready");
                NumHost = SStartRMI.nsize;
                System.out.println("Numero di host selezionati: "+ NumHost);
                //List <String> prov = new ArrayList<String>();
                //serverRMI.Ricezione(prov, 1);*/

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

}


