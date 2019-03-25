package distributedLogic.client;

import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.game.*;
import distributedLogic.net.Link;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.MessageFactory;
import distributedLogic.net.remote.Participant;
import distributedLogic.net.remote.RingBroadcast;
import distributedLogic.net.router.RouterFactory;


import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StartClient {
    public static final int CONNECTION_PORT = 1099;
    public static final int BC_PORT = 1099;
    public static final String BC_SERVICE = "Broadcast";
    private static Hand hand;
    private static Deck coveredDeck;
    private static Card firstUncovered;
    private static Link link;
    private static RouterFactory routerMaker;
    private static MessageFactory messageMaker;
    private static BlockingQueue<GameMessage> buffer;
    private static RingBroadcast ringBroadcast;
    private static Player[] players;
    private static Game game;
    private static int myId;
    public int[] processedMsg;
    private Move move;
    private int rightId;

    public static void main(String[] args) throws RemoteException {
        InetAddress localHost = null;
        String playerName = "playerName";
        String server = "serverIP";
        int port = BC_PORT;

        System.out.println("Player Name: ... ");
        playerName = new java.util.Scanner(System.in).nextLine();

//        System.out.println("Server IP: ... ");
//        server = new java.util.Scanner(System.in).nextLine();
        server = "192.168.1.142"; //EMILIO IP
        try {
            server = InetAddress.getLocalHost().getHostAddress(); //EMILIO IP
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("Port: ... ");
        port = new java.util.Scanner(System.in).nextInt();

        /*try {
            System.out.println("IP Client: ... ");
            localHost = InetAddress.getByName(new java.util.Scanner(System.in).nextLine());
        } catch (UnknownHostException e) {
            System.out.println("CLIENT: " + "Invalid local host " + e.getMessage());
        }*/


        if (localHost == null) {
            try {
                localHost = InetAddress.getLocalHost();
                System.out.println("CLIENT: " + "Local host is " + localHost);
            } catch (UnknownHostException e) {
                System.out.println("CLIENT: " + "UnknownHostException " + e.getMessage());
            }
        }

        // TODO CLIENT start
        Player me = new Player(playerName, localHost, port);
        ringBroadcast = null;
        buffer = new LinkedBlockingQueue<GameMessage>();

        String serviceURL = "rmi://" + localHost.getCanonicalHostName() + ":" + port + "/" + BC_SERVICE;

        try {
            LocateRegistry.createRegistry(port);
            ringBroadcast = new RingBroadcast(buffer);
            System.out.println("CLIENT: Registering Broadcast service at " + serviceURL);
            Naming.rebind(serviceURL, ringBroadcast);
        } catch (RemoteException e) {
            LocateRegistry.getRegistry(CONNECTION_PORT).list();
            System.out.println("rmiregistry already started: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException already started: " + e.getMessage());
        }


        boolean result = false;
        Participant participant = null;

        // establish connection with server
        String serverURL = "rmi://" + server + ":" + CONNECTION_PORT + "/Server";
        try {
            participant = new Participant();
            IConnection connection = (IConnection) Naming.lookup(serverURL);
            result = connection.subscribe(participant, me);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        if (result) {
            System.out.println("CLIENT: " + "I've been accepted.");
            players = participant.getPlayers();

            if (players.length > 1) {

                hand = participant.getHand();

                System.out.println("CLIENT: Hand contains " + hand.getNumberOfCards());
                System.out.println("Mano: ");
                hand.printHand();

                firstUncovered = participant.getFirstCard();
                System.out.println("CLIENT: First uncovered : " + firstUncovered.toString());

                coveredDeck = participant.getCoveredDeck();
/*            for (Card card : coveredDeck.getPile()) {
                System.out.println("Carte restanti: "+ card.toString());
            }
            System.out.println("Numero carte restanti: "+coveredDeck.getPile().size());*/


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
                startGame();
            } else {
                System.out.println("Not enough players to start the game. :(");
                System.exit(0);
            }
        } else {
            System.out.println("EROREEEEEEEEEE");
            System.out.println("Game subscribe unsuccessful. Exit the game.");
            System.exit(0);
        }
    }

    private static void startGame() {
        // TODO gui start
        tryToPlay();

        while (!game.isGameOver()) {
            // TODO Eseguo quando non è il mio turno, sto in ascolto di messaggi sul buffer.
            try {
                System.out.println("CLIENT: Waiting up to " + getWaitSeconds() + " seconds for a message..");
                GameMessage m = buffer.poll(getWaitSeconds(), TimeUnit.SECONDS);

                if (m != null) {
                    System.out.println("CLIENT: Processing message " + m.toString());

                    // recupero la mossa dal messaggio che mi è arrivato
                    // move = m.getMove();
                    System.out.println("CLIENT: Message from Node " + m.toString());

                    // Controlla se è un mex di crash o di gioco
                    if (m.getNodeCrashed() != -1) {
                        System.out.println("Crash Message");
                        link.getNodes()[m.getNodeCrashed()].setNodeCrashed();
                        // TODO update gui
                        retrieveNextPlayerCrash();
                    } else {
                        System.out.println("Game Message");
                        game.setCurrentPlayer();
                    }
                    System.out.println("CLIENT: Giocatore corrente: " + game.getCurrentPlayer());
                    tryToPlay();
                } else {
                    // Timeout -> Avvio controllo AYA sui nodi vicini
                    System.out.println("CLIENT: *** timeout ***");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void tryToPlay() {
        int currentPlayer = game.getCurrentPlayer();
        while (currentPlayer == myId && !game.isGameOver()) {
            System.out.println("ISERISCI IL MEX:");
            String msg = new Scanner(System.in).nextLine();

            // recupero il prossimo nodo attivo
            boolean[] nodesCrashed = new boolean[players.length];
            Arrays.fill(nodesCrashed, false);
            boolean anyCrash = false;
            int howManyCrash = 0;

            while (link.checkAliveNodes() == false) {
                anyCrash = true;
                howManyCrash += 1;
                nodesCrashed[link.getRightId()] = true;
                System.out.println("Finding a new neighbour");
                link.incrementRightId();
                if (link.getRightId() == link.getMyId()) {
                    System.out.println("Unico giocatore, partita conclusa");
                    // TODO update interface
                }
            }
            //

            Move moveToPlay = game.myTurn();

            ringBroadcast.incrementMessageCounter();
            System.out.println("I'm sending a message with id: " + ringBroadcast.retrieveMsgCounter());
            // ringBroadcast.send(messageMaker.newGameMessage(moveToPlay, ringBroadcast.retrieveMsgCounter(), howManyCrash));
            ringBroadcast.send(messageMaker.newGameMessage(msg, ringBroadcast.retrieveMsgCounter(), howManyCrash));


            currentPlayer = game.getCurrentPlayer();
            System.out.println("CLIENT: Next player is " + players[currentPlayer].getUsername());
            if (anyCrash) {
                // TODO send CrashMessage
            }
        }
    }

    /**
     * Metodo che restituisce un tot di secondi in base all'id del nodo
     *
     * @return
     */
    private static long getWaitSeconds() {
        return 10L + myId * 2;
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

}
