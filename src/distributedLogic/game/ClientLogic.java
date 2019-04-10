package distributedLogic.game;

import gui.view.GameController;
import gui.view.LoginController;
import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.net.Link;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.MessageFactory;
import distributedLogic.net.remote.Participant;
import distributedLogic.net.remote.RingBroadcast;
import distributedLogic.net.router.RouterFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientLogic {
    public static final String BC_SERVICE = "Broadcast";
    private static final int CLIENT_PORT = 2001;
    private static final int CONNECTION_PORT = 1099;
    private RingBroadcast ringBroadcast;
    private Deck coveredDeck;
    private Card firstUncovered;
    private Link link;
    private RouterFactory routerMaker;
    private MessageFactory messageMaker;
    private Player[] players;
    private Player me;
    private Participant participant;
    private Game game;
    private int myId;
    private Move moveToPlay;

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

        me = new Player(playerUsername, localHost, port);
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
        participant = null;

        // establish connection with server
        String serverURL = "rmi://" + serverAddress + ":" + CONNECTION_PORT + "/Server";
        String errorMex = "Duplicated username.";
        try {
            participant = new Participant();
            IConnection connection = (IConnection) Naming.lookup(serverURL);
            result = connection.subscribe(participant, me);
        } catch (NotBoundException | RemoteException e) {
            System.out.println("Connection ended, Service is down: " + e.getMessage());
            errorMex = "Service is down.";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (result) {
            System.out.println("CLIENT: " + "I've been accepted.");
            loginController.getStatusLabel().setTextFill(Color.RED);
            loginController.getStatusLabel().setText("Waiting for other client");

            Thread thread = new Thread() {
                @Override
                public void run() {
                    players = participant.getPlayers();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            // TODO verifico numero giocatori
                            if (players.length > 1) {
                                firstUncovered = participant.getFirstCard();
                                coveredDeck = participant.getCoveredDeck();

                                link = new Link(me, players);
                                myId = link.getMyId();

                                System.out.println("CLIENT: " + myId);
                                System.out.println("Mano: [ " + players[myId].getHandClass().toString() + " ]");

                                routerMaker = new RouterFactory(link);
                                messageMaker = new MessageFactory(myId);

                                ringBroadcast.configure(link, routerMaker, messageMaker);

                                System.out.println("My id is " + myId + " and my name is " + players[myId].getUsername());
                                System.out.println("My left neighbour is " + players[link.getLeftId()].getUsername());
                                System.out.println("My right neighbour is " + players[link.getRightId()].getUsername());

                                changeScene(event);
                            } else {
                                System.out.println("Not enough players to start the game. :(");
                                alertMessage("Not enough players to start the game. :(", true);
                            }
                        }
                    });
                }
            };

            thread.start();
        } else {
            loginController.getStatusLabel().setText("Server not reachable");
            alertMessage("Game subscribe unsuccessful. " + errorMex + " Exit the game.", true);
        }

    }

    private void alertMessage(String string, boolean errorMex) {
        // TODO da inserire quando Vittoria, errore connessione, etc
        if (!errorMex)
            loginController.getAlert().setAlertType(Alert.AlertType.INFORMATION);
        else
            loginController.getAlert().setAlertType(Alert.AlertType.ERROR);

        loginController.getAlert().setContentText(string);
        Optional<ButtonType> resultAlert = loginController.getAlert().showAndWait();
        if (!resultAlert.isPresent() || resultAlert.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void changeScene(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(GameController.class.getResource("fxml/ScreenGame.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(GameController.class.getResource("fxml/style.css").toExternalForm());
            Stage windows = (Stage) ((Node) event.getSource()).getScene().getWindow();
            windows.setOnCloseRequest(windowsEvent -> {
                System.exit(0);
            });

            GameController gameController = fxmlLoader.getController();
            game = new Game(firstUncovered, coveredDeck, players, myId, gameController, this);
            ringBroadcast.gameReference(game);

            windows.setScene(scene);
            windows.show();

            doClientThread();

        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }
    }

    //creo Thread Client, durerà fino alla fine della partita.
    public void doClientThread() {
        Thread t2 = new Thread() {
            public synchronized void run() {
                startGame();
            }
        };
        t2.start();
    }

    private synchronized void startGame() {
        int index = 0;
        tryMyTurn();

        while (!game.isConcluso()) {
            System.out.println("----------------------------------------------------------------------------------------  \u001B[94m" + (++index) + "\u001B[0m  ------------------------------");
            Player sx = players[link.getLeftId()], me = players[myId], dx = players[link.getRightId()];
            System.out.println("nodo sx: # " + sx.getId() + " " + sx.getUsername() + " " + sx.getInetAddress().getHostAddress() + ":" + sx.getPort());
            System.out.println("nodo me: # " + me.getId() + " " + me.getUsername() + " " + me.getInetAddress().getHostAddress() + ":" + me.getPort());
            System.out.println("nodo dx: # " + dx.getId() + " " + dx.getUsername() + " " + dx.getInetAddress().getHostAddress() + ":" + dx.getPort());

            try {
                // TODO Eseguo quando non è il mio turno, sto in ascolto di messaggi sul buffer.
                System.out.println("CLIENT: Waiting up to " + getWaitSeconds() + " seconds for a message..");
                System.err.println("\u001B[94mCLIENT: current player # " + game.getCurrentPlayer() + " -> " + players[game.getCurrentPlayer()].getUsername() + "\u001B[0m");

                GameMessage m = ringBroadcast.getBuffer().poll(getWaitSeconds(), TimeUnit.SECONDS);

                if (m != null) {
                    System.out.println("CLIENT: Processing message " + m.toString());

                    // recupero la mossa dal messaggio che mi è arrivato
                    moveToPlay = m.getMove();

                    // Controlla se è un mex di crash o di gioco
                    if (m.getNodeCrashed() != -1) { // -1 in node crashed identifica un messaggio di gioco
                        System.out.println("Received Crash Message");
                        link.getNodes()[m.getNodeCrashed()].setNodeCrashed();

                        link.setNewNeighbor();
                        retrieveNextPlayerAfterCrash();

                        game.updateListPlayersGUI();
                        game.putPlayerCardInUncoveredDeckAfterCrash(m.getNodeCrashed());
                    } else {
                        System.out.println("Received Game Message");
                        game.updateMove(m.getMove());
                    }
                    System.out.println("CLIENT: Next player is: " + game.getCurrentPlayer());

                    tryMyTurn();
                } else {
                    // BUFFER vuoto -> Avvio controllo AYA sui nodi vicini
                    checkRightAYA();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // TODO gui partita conclusa
        System.out.println("PARTITA CONCLUSA");

    }

    /**
     * Metodo che restituisce un tot di secondi in base all'id del nodo
     *
     * @return
     */
    private long getWaitSeconds() {
        return (8L + myId) / 4;
    }

    /**
     * metodo che restituisce il prossimo giocatore nel momento in cui si verifica un crash
     */
    private void retrieveNextPlayerAfterCrash() {
        if (link.getNodes()[game.getCurrentPlayer()].isActive()) {
            System.out.println("CurrentPlayer is Active");
        } else {
            game.setCurrentPlayer();
        }

        if (game.isSaidBusso()) {
            if (link.getNodes()[game.getIdBusso()].isActive()) {
                System.out.println("BussoPlayer is Active");
            } else {
                game.updateIdBusso();
            }
        }
    }

    private synchronized void tryMyTurn() {
        System.out.println("\u001B[32m --------- MY TURN START ---------- \u001B[0m");


        int currentPlayer = game.getCurrentPlayer();
        int cycle = 0;
        while (currentPlayer == myId && !game.isConcluso()) {
            if (game.isSaidBusso() && game.getIdBusso() == myId) {
                System.out.println("\u001B[40m HO DETTO IO BUSSO \u001B[0m");
                stopGame();
            }

            if (cycle == 0 && !game.isConcluso()) {
                this.game.getPlayers()[currentPlayer].incrementNumberMoves();
                this.game.getGameController().lockUnlockElementTable(1);
            }


            //Quando è il mio turno sblocco la board e rimango in attesa della mossa
            //L oggetto GameController si blocca un attimo ma la classe remota RMI MessageBroadcast può ancora
            // ricevere messaggi, appena il client si riattiva può ritornare in ascolto sul buffer per vedere
            // se ci sono messaggi.Se ce ne sono va ad aggiornare l interfaccia locale.
            // TODO set current player gui
            System.out.println("\u001B[32mCLIENT: current player: # " + currentPlayer + " -> " + game.getPlayers()[currentPlayer].getUsername() + "\u001B[0m");

            // TODO MEX UTENTE
            if (!game.isConcluso()) {
                try {
                    System.out.println("Wait move");
                    wait();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }

            Boolean[] nodesCrashed = new Boolean[players.length];
            Arrays.fill(nodesCrashed, false);
            boolean anyCrash = false;

            // recupera il prossimo nodo attivo
            while (link.checkAliveNodes() == false) {
                int idCrashnode = link.getRightId();
                System.out.println("\u001B[92m MyTurn: si è verificato un crash del nodo: " + idCrashnode + "\u001B[0m");
                anyCrash = true;
                nodesCrashed[idCrashnode] = true;

                System.out.println("Finding a new neighbour");
                link.setNewNeighbor();

                changeCurrentPlayer();
                changeIdBusso(idCrashnode);
                checkLastNode();
            }


            ringBroadcast.incrementMessageCounter();
            int messageCounter = ringBroadcast.retrieveMsgCounter();
            boolean success = false;
            while (!success) {
                System.out.println("Sending message # " + messageCounter);
                ringBroadcast.send(messageMaker.newGameMessage(moveToPlay, messageCounter));
                success = true;
            }

            //game.updateAnyCrash(link.getNodes(), link.getMyId());
            // TODO mosse player - - - logica gioco


            // invio CrashMessage se si sono verificati crash
            if (anyCrash) {
                // update interface after cecking crash
                game.updateListPlayersGUI();

                for (int i = 0; i < nodesCrashed.length; i++) {
                    if (nodesCrashed[i]) {
                        ringBroadcast.incrementMessageCounter();
                        int messageCounterCrash = ringBroadcast.retrieveMsgCounter();
                        ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash));
                        System.out.println("Sending CrashMessage: " + messageCounterCrash);
                    }
                }
            } else {
                changeCurrentPlayer();
            }
            currentPlayer = game.getCurrentPlayer();
            cycle++;
            System.out.println("Next Player is " + players[currentPlayer].getUsername() + " id " + currentPlayer);
        }
        System.out.println("\u001B[32m --------- MY TURN END ---------- \u001B[0m");
    }

    private synchronized void checkLastNode() {
        if (link.getRightId() == link.getMyId()) {
            game.setCurrentPlayer(link.getMyId());
            game.updateListPlayersGUI();
            game.setConcluso();
            Platform.runLater(() -> {
                        alertMessage("Sei l'unico giocatore rimasto in partita, vittoria!", false);
                    }
            );
            System.out.println("Unico giocatore, partita conclusa. Vittoria");

        }
    }

    private synchronized void checkRightAYA() {
        System.out.println("CLIENT: *** timeout ***");

        int playeId = game.getCurrentPlayer();
        int rightId = link.getRightId();
        int index2 = 0;

        if (!link.checkAYANode(rightId)) {
            System.out.println("-------------------  \u001B[95m" + (++index2) + "\u001B[0m  -----------------");

            if (rightId == playeId) {
                System.out.println("\u001B[31m CLIENT: Current Player has crashed " + players[playeId].getUsername() + " (# " + playeId + "). Sending crash Msg\u001B[0m");
            } else {
                System.out.println("\u001B[35m CLIENT: My right Neighboard has crashed " + players[rightId].getUsername() + "(# " + rightId + "). Sending crash Msg\u001B[0m");
            }

            link.getNodes()[rightId].setNodeCrashed();
            link.setNewNeighbor();

            Boolean[] nodesCrashed = new Boolean[players.length];
            Arrays.fill(nodesCrashed, false);
            nodesCrashed[rightId] = true;

            checkLastNode();

            if (nodesCrashed[playeId])
                game.putPlayerCardInUncoveredDeckAfterCrash(playeId);

            while (link.checkAliveNodes() == false) {
                // entro quando  2 nodi hanno fatto crash contemporaneamente
                nodesCrashed[link.getRightId()] = true;
                System.out.println("\u001B[92m MEX NULL: Finding a new neighbour : Crash anche il nodo: " + players[link.getRightId()].getUsername() + " # " + link.getRightId() + " \u001B[0m");
                game.putPlayerCardInUncoveredDeckAfterCrash(link.getRightId());

                link.setNewNeighbor();
                checkLastNode();
            }

            if (nodesCrashed[playeId]) {
                game.setCurrentPlayer(link.getRightId());
                System.out.println("New CurrentPlayer is " + players[game.getCurrentPlayer()].getUsername() + " id  " + game.getCurrentPlayer());
            }

            // Spedisce CrashMessage se sono stati rilevati
            for (int i = 0; i < nodesCrashed.length; i++) {
                if (nodesCrashed[i]) {
                    ringBroadcast.incrementMessageCounter();
                    int messageCounterCrash = ringBroadcast.retrieveMsgCounter();
                    ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash));
                    System.out.println("Sending a CrashMessage id " + messageCounterCrash + " crash nodo # " + i + " to: " + link.getRightId());
                }
            }
            changeIdBusso(rightId);
            game.updateListPlayersGUI();
        }
    }

    //Quando il giocatore ha fatto la sua mossa, la board lo notifica al client
    //che la deve impacchettare in un messaggio da spedire.
    public synchronized void notifyMove(Move move) {
        this.moveToPlay = move;
        System.out.println("Notify move");
        notifyAll();
    }

    private void changeCurrentPlayer() {
        if (this.moveToPlay.getStatus().matches("discard|busso")) {
            game.setCurrentPlayer();
        }
    }

    private void changeIdBusso(int idCrash) {
        if (game.isSaidBusso() && game.getIdBusso() == idCrash) {
            System.out.println("UPDATE BUSSO - old: " + game.getIdBusso());
            game.updateIdBusso();
            System.out.println("UPDATE BUSSO - new: " + game.getIdBusso());
        }
    }

    private void stopGame() {
        game.declareWinner();
        this.moveToPlay = (new Move("winner"));
    }
}
