package distributedLogic.game;

import GUI.view.GameController;
import GUI.view.LoginController;
import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.remote.Participant;
import distributedLogic.net.remote.RingBroadcast;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientLogic {
    private static final int CLIENT_PORT = 2001;
    private static final int CONNECTION_PORT = 1099;
    private static final String BC_SERVICE = "Broadcast";
    private static RingBroadcast ringBroadcast;

    private Timeline timeline;


    private LoginController loginController;
    private String playerUsername, serverAddress;

    public ClientLogic(String playerUsername, String serverAddress, LoginController loginController) {
        this.playerUsername = playerUsername;
        this.serverAddress = serverAddress;
        this.loginController = loginController;
    }

    public synchronized void startClient(ActionEvent event) {
        InetAddress localHost = null;
        int port = CLIENT_PORT;

        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("CLIENT: " + "UnknownHostException " + e.getMessage());
        }

        boolean isCorrectPort = false;
        while (!isCorrectPort) {
            try {
                LocateRegistry.createRegistry(port);
                isCorrectPort = true;
            } catch (RemoteException e) {
                port = port + 1; // Se si verifica un errore, vuol dire che tale porta è occupata allora incremento e riprovo
                System.out.println("rmiregistry already started: " + e.getMessage());
            }
        }

        Player me = new Player(playerUsername, localHost, port);
        ringBroadcast = null;
        BlockingQueue<GameMessage> buffer = new LinkedBlockingQueue<GameMessage>();

        System.out.println("--------------------------- MY NAME IS: " + playerUsername + " " + localHost.getHostAddress() + " : " + port);
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
        String serverURL = "rmi://" + serverAddress + ":" + CONNECTION_PORT + "/Server";
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
            loginController.getStatusLabel().setTextFill(Color.RED);
            loginController.getStatusLabel().setText("Waiting for other client");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(GameController.class.getResource("fxml/ScreenGame.fxml"));

            try {

                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                Stage windows = (Stage) ((Node) event.getSource()).getScene().getWindow();
                windows.setOnCloseRequest(windowsEvent -> {
                    System.exit(0);
                });
                GameController gameController = new GameController();
                gameController = fxmlLoader.getController();
                gameController.initGame(playerUsername, serverAddress, me, participant, ringBroadcast);

                timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);

                KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), event1 -> {
                    if (loginController.getCanContinue()) {
                        windows.setScene(scene);
                        windows.show();
                    }
                });

                timeline.getKeyFrames().add(keyFrame);
                timeline.play();


            } catch (IOException e) {
                System.out.println("IOException " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            loginController.getStatusLabel().setText("Server not reachable");
            try {
                System.out.println("Game subscribe unsuccessful. Exit the game.");
                Thread.sleep(10000);
                System.exit(0);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException " + e.getMessage());
            }

        }

    }


}
