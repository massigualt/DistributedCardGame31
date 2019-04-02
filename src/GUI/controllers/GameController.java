package GUI.controllers;

import GUI.controllers.LoginController;
import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Game;
import distributedLogic.game.Hand;
import distributedLogic.net.Link;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.MessageFactory;
import distributedLogic.net.remote.Participant;
import distributedLogic.net.remote.RingBroadcast;
import distributedLogic.net.router.RouterFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameController {
    public static final int CONNECTION_PORT = 1099;
    public static final int CLIENT_PORT = 2001;
    public static final String BC_SERVICE = "Broadcast";
    private static RingBroadcast ringBroadcast;
    private static Hand hand;
    private static Deck coveredDeck;
    private static Card firstUncovered;
    private static Link link;
    private static RouterFactory routerMaker;
    private static MessageFactory messageMaker;
    private static Player[] players;
    private static Player me;
    private static Participant participant;
    private static Game game;
    private static int myId;


    public static LoginController loginController;


    String playerName = "";
    String server = "";

    @FXML
    private Label userLabel, handPoints;

    @FXML
    private Button coveredDeckButton, uncoveredDeckButton, card1Button, card2Button, card3Button, card4Button;

    @FXML
    private ListView partecipantList;
    ObservableList<String> userList = FXCollections.observableArrayList ();
    Timeline timeline;



    public void initGame(String user, String serv, Player me, Participant participant, RingBroadcast ringBroad) throws RemoteException {

        this.playerName = user;
        this.server = serv;
        this.me = me;
        this.participant = participant;
        this.ringBroadcast = ringBroad;



        userLabel.setText(playerName);

        Thread thread = new Thread() {
            public void run() {

                        System.out.println("CLIENT: " + "I've been accepted.");
                        players = participant.getPlayers();

                        // TODO Assegno id corretto a me
                        for (int i = 0; i < players.length; i++) {
                            if (players[i].getUsername().equals(playerName)) {
                                me.setId(i);
                                break;
                            }
                        }

                        if (players.length > 0) {
                            hand = participant.getHand();
                            System.out.println("CLIENT: Hand contains " + hand.getNumberOfCards());
                            System.out.println("Mano: ");
                            hand.printHand();

                            firstUncovered = participant.getFirstCard();
                            System.out.println("CLIENT: First uncovered : " + firstUncovered.toString());

                            coveredDeck = participant.getCoveredDeck();
                            /*for (Card card : coveredDeck.getPile()) {
                                System.out.println("Carte restanti: " + card.toString());
                            }
                            System.out.println("Numero carte restanti: " + coveredDeck.getPile().size());*/


                            // ############################# GRAPHICS #########################################
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    switch(hand.getNumberOfCards())
                                    {
                                        case 3:
                                            card1Button.setText(String.valueOf(hand.getCard(0)));
                                            card2Button.setText(String.valueOf(hand.getCard(1)));
                                            card3Button.setText(String.valueOf(hand.getCard(2)));
                                            //handPoints.setText(String.valueOf(hand.handValue()));
                                        case 4:
                                            card1Button.setText(String.valueOf(hand.getCard(0)));
                                            card2Button.setText(String.valueOf(hand.getCard(1)));
                                            card3Button.setText(String.valueOf(hand.getCard(2)));
                                            card4Button.setText(String.valueOf(coveredDeck.getPile().removeLast()));
                                            //handPoints.setText(String.valueOf(hand.handValue()));
                                        default:
                                    }

                                    uncoveredDeckButton.setText(firstUncovered.toString());
                                    coveredDeckButton.setText(String.valueOf(coveredDeck.getPile().size()));

                                    for (int i = 0; i < players.length; i++) {
                                        userList.add(players[i].getUsername());
                                    }
                                    System.out.println("\n\nLISTA UTENTI\n");
                                    System.out.println(userList);

                                    partecipantList.setItems(userList);

                                    timeline = new Timeline();
                                    timeline.setCycleCount(Timeline.INDEFINITE);

                                    KeyFrame keyFrame = new KeyFrame(Duration.millis(500), event1 -> {
                                        handPoints.setText(String.valueOf(hand.handValue()));
                                    });
                                    timeline.getKeyFrames().add(keyFrame);
                                    timeline.play();
                                }
                            });



                            // ############################# END GRAPHICS #########################################

                            // TODO
                            link = new Link(me, players);
                            myId = link.getMyId();

                            System.out.println("CLIENT: " + myId);

                            routerMaker = new RouterFactory(link);
                            messageMaker = new MessageFactory(myId);

                            ringBroadcast.configure(link, routerMaker, messageMaker);


                            System.out.println("My id is " + myId + " and my name is " + players[myId].getUsername());
                            System.out.println("My left neighbour is " + players[link.getLeftId()].getUsername());
                            System.out.println("My right neighbour is " + players[link.getRightId()].getUsername());


                            game = new Game(firstUncovered, coveredDeck, hand, players, myId);

                            loginController = new LoginController();
                            loginController.setCanContinue();

                            startGame();

                        } else {
                            System.out.println("Not enough players to start the game. :(");
                            System.exit(0);
                        }
                    }

        };
        thread.start();

    }


    private synchronized void startGame() {
        int index = 0;

        // TODO gui start
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
                    // move = m.getMove();

                    // Controlla se è un mex di crash o di gioco
                    if (m.getNodeCrashed() != -1) { // -1 in node crashed identifica un messaggio di gioco
                        System.out.println("Received Crash Message");
                        link.getNodes()[m.getNodeCrashed()].setNodeCrashed();
                        link.setNewNeighbor();
                        game.updateCrash(m.getNodeCrashed());
                        retrieveNextPlayerCrash();
                        // TODO update gui
                    } else {
                        System.out.println("Received Game Message");
                        game.setCurrentPlayer();
                    }
                    System.out.println("CLIENT: Next player is: " + game.getCurrentPlayer());
                    tryMyTurn();
                } else {
                    // BUFFER vuoto
                    // Timeout -> Avvio controllo AYA sui nodi vicini
                    checkRight();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Metodo che restituisce un tot di secondi in base all'id del nodo
     *
     * @return
     */
    private static long getWaitSeconds() {
        return (8L + myId) / 4;
    }

    /**
     * metodo che restituisce il prossimo giocatore nel momento in cui si verifica un crash
     */
    private static void retrieveNextPlayerCrash() {
        if (link.getNodes()[game.getCurrentPlayer()].isActive()) {
            System.out.println("Player Active");
        } else {
            game.setCurrentPlayer();
        }
    }

    private static void tryMyTurn() {
        System.out.println("\u001B[32m --------- MY TURN START ---------- \u001B[0m");

        int currentPlayer = game.getCurrentPlayer();
        while (currentPlayer == myId && !game.isConcluso()) {
            //Quando è il mio turno sblocco la board e rimango in attesa della mossa
            //L oggetto GameController si blocca un attimo ma la classe remota RMI MessageBroadcast può ancora
            // ricevere messaggi, appena il client si riattiva può ritornare in ascolto sul buffer per vedere
            // se ci sono messaggi.Se ce ne sono va ad aggiornare l interfaccia locale.
            // TODO set current player GUI
            System.out.println("\u001B[32mCLIENT: current player: # " + currentPlayer + " -> " + game.getPlayers()[currentPlayer].getUsername() + "\u001B[0m");

            // TODO MEX UTENTE
            System.out.println("\n\n \u001B[44m *** INSERISCI IL MEX: *** \u001B[0m \n\n");
            String msg = new Scanner(System.in).nextLine();



            // Move moveToPlay = game.myTurn();

            Boolean[] nodesCrashed = new Boolean[players.length];
            Arrays.fill(nodesCrashed, false);
            boolean anyCrash = false;

            // recupera il prossimo nodo attivo
            while (link.checkAliveNodes() == false) {
                System.out.println("\u001B[92m MyTurn: si è verificato un crash del nodo: " + link.getRightId() + "\u001B[0m");
                anyCrash = true;
                nodesCrashed[link.getRightId()] = true;

                System.out.println("Finding a new neighbour");
                link.setNewNeighbor();
                game.setCurrentPlayer(); // Spostato da riga 379 (prima di currentPlayer = game.getCurrentPlayer();)
                checkLastNode();
            }


            ringBroadcast.incrementMessageCounter();
            int messageCounter = ringBroadcast.retrieveMsgCounter();
            boolean success = false;
            while (!success) {
                System.out.println("Sending message # " + messageCounter);
                // ringBroadcast.send(messageMaker.newGameMessage(moveToPlay, ringBroadcast.retrieveMsgCounter(), howManyCrash));
                ringBroadcast.send(messageMaker.newGameMessage(msg, messageCounter));
                success = true;
            }

            //game.updateAnyCrash(link.getNodes(), link.getMyId());
            // TODO mosse player - - - logica gioco


            // invio CrashMessage se si sono verificati crash
            if (anyCrash) {
                for (int i = 0; i < nodesCrashed.length; i++) {
                    if (nodesCrashed[i]) {
                        ringBroadcast.incrementMessageCounter();
                        int messageCounterCrash = ringBroadcast.retrieveMsgCounter();
                        ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash));
                        System.out.println("Sending CrashMessage: " + messageCounterCrash);
                    }
                }
            } else {
                game.setCurrentPlayer(); // Spostato da riga 379 (prima di currentPlayer = game.getCurrentPlayer();)
            }
            currentPlayer = game.getCurrentPlayer();
            System.out.println("Next Player is " + players[currentPlayer].getUsername() + " id " + currentPlayer);
        }
        System.out.println("\u001B[32m --------- MY TURN END ---------- \u001B[0m");
    }

    private synchronized static void checkLastNode() {
        if (link.getRightId() == link.getMyId()) {
            game.updateCrash(link.getRightId());
            game.setCurrentPlayer(link.getMyId());
            game.setConcluso();
            System.out.println("Unico giocatore, partita conclusa. Vittoria");
            System.exit(0);
        }
    }

    private synchronized static void checkRight() {
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

            while (link.checkAliveNodes() == false) {
                // entro quando anche 2 nodi hanno fatto crash contemporaneamente
                nodesCrashed[link.getRightId()] = true;
                System.out.println("\u001B[92m MEX NULL: Finding a new neighbour : Crash anche il nodo: " + players[link.getRightId()].getUsername() + " # " + link.getRightId() + " \u001B[0m");
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
                    game.updateCrash(i);
                    ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash));
                    System.out.println("Sending a CrashMessage id " + messageCounterCrash + " crash nodo # " + i + " to: " + link.getRightId());
                }
            }
        }
    }

}
