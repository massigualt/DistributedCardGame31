package GUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.client.Client;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.remote.Participant;
import distributedLogic.net.remote.RingBroadcast;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


public class LoginController implements Initializable, ControlledScreen {

    private static final int CLIENT_PORT = 2001;
    private static final int CONNECTION_PORT = 1099;
    private static final String BC_SERVICE = "game";
    private static RingBroadcast ringBroadcast;


    ScreensController myController;

    Timeline timeline;

    @FXML
    private TextField username;

    @FXML
    private TextField serverIP;

    @FXML
    private Label statusLabel;

    private String playerUsername, serverAddress;

    private static Boolean canContinue = false;

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
    private void buttonPlay(ActionEvent event) {
        playerUsername = username.getText();
        serverAddress = serverIP.getText();
        canContinue = false;
        startClient(event);
    }

    private synchronized void startClient(ActionEvent event) {
        String server = serverAddress;
        String playerName = playerUsername;

        InetAddress localHost = null;
        int port = CLIENT_PORT;

        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("CLIENT: " + "UnknownHostException " + e.getMessage());
        }

        Random random = new Random();

        boolean isCorrectPort = false;
        while (!isCorrectPort) {
            try {
                LocateRegistry.createRegistry(port);
                isCorrectPort = true;
            } catch (RemoteException e) {
                port += 1 + random.nextInt(50); // Se si verifica un errore, vuol dire che tale porta è occupata allora incremento e riprovo
                System.out.println("rmiregistry already started: " + e.getMessage());
            }
        }

        // TODO CLIENT start
        Player me = new Player(playerName, localHost, port);
        ringBroadcast = null;
        BlockingQueue<GameMessage> buffer = new LinkedBlockingQueue<GameMessage>();

        System.out.println("--------------------------- MY NAME IS: " + playerName + " " + localHost.getHostAddress() + " : " + port);
        String serviceURL = "rmi://" + localHost.getHostAddress() + ":" + port + "/" + BC_SERVICE;

        try {
            ringBroadcast = new RingBroadcast(buffer);
            System.out.println("\u001B[34mCLIENT: Registering Broadcast service at " + serviceURL + "\u001B[0m");
            Naming.rebind(serviceURL, ringBroadcast);
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException already started: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("RemoteException already started: " + e.getMessage());
        }

        boolean result = false;
        Participant participant = null;

        // establish connection with server
        // server = localHost.getHostAddress();
        String serverURL = "rmi://" + server + ":" + CONNECTION_PORT + "/Server";
        try {
            participant = new Participant();
            IConnection connection = (IConnection) Naming.lookup(serverURL);
            result = connection.subscribe(participant, me);
        } catch (NotBoundException | RemoteException e) {
            System.out.println("Connection ended, Service is down: " + e.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (result) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Waiting for other client");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("ScreenGame.fxml"));

            try {

                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                Stage windows = (Stage) ((Node) event.getSource()).getScene().getWindow();
                windows.setOnCloseRequest(windowsEvent -> {
                });
                Client client = new Client();
                client = fxmlLoader.getController();
                client.initGame(playerUsername, serverAddress, me, participant, ringBroadcast);

                timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);

                KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), event1 -> {
                    if (canContinue) {
                        windows.setScene(scene);
                        windows.show();
                    }
                });

                timeline.getKeyFrames().add(keyFrame);
                timeline.play();


            } catch (IOException e) {
                System.out.println("IOException " + e.getMessage());
            }

        } else {
            statusLabel.setText("Server not reachable");
            try {
                System.out.println("Game subscribe unsuccessful. Exit the game.");
                Thread.sleep(10000);
                System.exit(0);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException " + e.getMessage());
            }

        }

    }

    public void setCanContinue() {
        this.canContinue = true;
    }
}
