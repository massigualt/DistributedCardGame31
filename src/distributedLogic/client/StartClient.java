package distributedLogic.client;

import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.Utils;
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
import java.util.*;
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

        //System.out.println("Player Name: ... ");
        //playerName = new java.util.Scanner(System.in).nextLine();
        playerName = args[0];
        System.out.println("------------------------------------------ MY NAME IS: " + playerName);

        server = "192.168.1.142"; //EMILIO IP
//        try {
//            server = InetAddress.getLocalHost().getHostAddress(); //EMILIO IP
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

        // System.out.println("Port: ... ");
        // port = new java.util.Scanner(System.in).nextInt();
        port = new Random().nextInt(100) + 2001;

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
            System.err.println("CLIENT: Registering Broadcast service at " + serviceURL);
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
            System.out.println("Connection ended, Service is down: " + e.getMessage());
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
        int index = 0;
        // TODO gui start
        tryMyTurn();

        while (!game.isGameOver()) {
            System.out.println("----------------------------------------------------------------------------------------  \u001B[94m" + (++index) + "\u001B[0m  ------------------------------");
            int x = link.getLeftId(), y = link.getRightId();
            System.out.println("nodo sx: # " + players[x].getId() + " " + players[x].getUsername());
            System.out.println("nodo dx: # " + players[y].getId() + " " + players[y].getUsername());
            int currentPlayerLocal = game.getCurrentPlayer();
            try {
                // TODO Eseguo quando non è il mio turno, sto in ascolto di messaggi sul buffer.
                System.out.println("CLIENT: Waiting up to " + getWaitSeconds() + " seconds for a message..");
                System.err.println("\u001B[96mCLIENT: current player # " + currentPlayerLocal + " -> " + players[currentPlayerLocal].getUsername() + "\u001B[0m");
                GameMessage m = buffer.poll(getWaitSeconds(), TimeUnit.SECONDS);

                if (m != null) {
                    System.out.println("CLIENT: Processing message " + m.toString());

                    // recupero la mossa dal messaggio che mi è arrivato
                    // move = m.getMove();


                    // Controlla se è un mex di crash o di gioco
                    if (m.getNodeCrashed() != -1) {
                        System.out.println("Crash Message");
                        link.getNodes()[m.getNodeCrashed()].setNodeCrashed();
                        game.updateCrash(m.getNodeCrashed());
                        retrieveNextPlayerCrash();
                        // TODO update gui
                    } else {
                        System.out.println("Game Message");
                        game.setCurrentPlayer(); // equivalente a retrieveNextPlayer
                    }
                    System.out.println("CLIENT: Next player is: " + game.getCurrentPlayer());
                    tryMyTurn();
                } else {
                    // BUFFER vuoto
                    // Timeout -> Avvio controllo AYA sui nodi vicini
                    System.out.println("CLIENT: *** timeout ***");

                    int playeId = game.getCurrentPlayer();
                    int rightId = link.getRightId();
                    boolean currentPlayerFirst = true;
                    int rightIdSave = rightId;

                    while (!link.checkAYANode(rightId)) {

                        if (rightId == playeId) {
                            System.out.println("CLIENT: Current Player has crashed (# " + playeId + "). Sending crash Msg");
                            link.getNodes()[rightId].setNodeCrashed();
                            link.incrementRightId();

                            List<Boolean> nodesCrashed = Utils.setArraylist(players.length, false);
                            boolean anyCrash = false;
                            int howManyCrash = 0;
                            int messageCounter = 0;

                            checkLastNode();

                            while (link.checkAliveNodes() == false) {
                                anyCrash = true;
                                howManyCrash += 1;
                                nodesCrashed.set(link.getRightId(), true);
                                System.out.println("Finding a new neighbour");
                                link.incrementRightId();
                                checkLastNode();
                            }

                            //invio dei crash dei nodi precedenti al giocatore attuale
                            if (currentPlayerFirst == false) {
                                System.out.println("Sono crashati nodi precedenti al current player");
                                System.out.println("RightidSave ->" + rightIdSave);
                                System.out.println("Playeid ->" + playeId);

                                while (((rightIdSave + 1) % players.length) <= playeId) {

                                    link.getNodes()[rightIdSave].setNodeCrashed();
                                    howManyCrash += 1;
                                    //nodesCrashed[i] = true;
                                    System.out.println("Node before current player");

                                    ringBroadcast.incrementMessageCounter();
                                    messageCounter = ringBroadcast.retrieveMsgCounter();

                                    game.updateCrash(rightIdSave);
                                    System.out.println("Im sending a crash message with id " + messageCounter);
                                    ringBroadcast.send(messageMaker.newCrashMessage(rightIdSave, messageCounter, howManyCrash));

                                    rightIdSave = rightIdSave + 1;
                                }
                            }

                            // Invio del crash del giocatore attuale
                            boolean success = false;
                            while (!success) {
                                ringBroadcast.incrementMessageCounter();
                                messageCounter = ringBroadcast.retrieveMsgCounter();
                                game.updateCrash(rightId);
                                game.setCurrentPlayer(link.getRightId());

                                System.out.println("Im sending a crash message with id " + messageCounter);
                                ringBroadcast.send(messageMaker.newCrashMessage(rightId, messageCounter, howManyCrash));
                                success = true;
                            }

                            System.out.println("Next Player is " + players[game.getCurrentPlayer()].getUsername() + " id " + game.getCurrentPlayer());

                            // Spedisce CrashMessage se sono stati rilevati crash dopo il giocatore attuale
                            if (anyCrash) {
                                // TODO forse da sistemare --> provare a fare crashare 2 giocatori dopo quello attuale
                                howManyCrash += 1;
                                for (int i = 0; i < nodesCrashed.size(); i++) {
                                    if (nodesCrashed.get(i)) {
                                        ringBroadcast.incrementMessageCounter();
                                        int messageCounterCrash = ringBroadcast.retrieveMsgCounter();
                                        System.out.println("Sending a CrashMessage id " + messageCounterCrash);
                                        //Invio msg di crash senza gestione dell'errore
                                        game.updateCrash(i);
                                        ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash, howManyCrash));
                                    }
                                }
                            }
                            break;
                        } else {
                            currentPlayerFirst = false;
                            System.out.println("CLIENT: Non è il giocatore corrente ad avere fatto crash.");
                        }
                        rightId = (rightId + 1) % players.length;
                    }
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

    private static void tryMyTurn() {

        int currentPlayer = game.getCurrentPlayer();
        while (currentPlayer == myId && !game.isGameOver()) {
            //Quando è il mio turno sblocco la board e rimango in attesa della mossa
            //L oggetto Client si blocca un attimo ma la classe remota RMI MessageBroadcast può ancora
            // ricevere messaggi, appena il client si riattiva può ritornare in ascolto sul buffer per vedere
            // se ci sono messaggi.Se ce ne sono va ad aggiornare l interfaccia locale.
            // TODO set current player GUI
            System.out.println("CLIENT: current player: # " + currentPlayer + " -> " + game.getPlayers()[currentPlayer].getUsername());

            // TODO MEX UTENTE
            System.out.println("\u001B[104mINSERISCI IL MEX: \u001B[0m");
            String msg = new Scanner(System.in).nextLine();

            List<Boolean> nodesCrashed = Utils.setArraylist(players.length, false);
            boolean anyCrash = false;
            int howManyCrash = 0;

            // recupera il prossimo nodo attivo
            while (link.checkAliveNodes() == false) {
                System.out.println("\u001B[92mCLIENT: si è verificato un crash del nodo: " + link.getRightId() + "\u001B[0m");
                anyCrash = true;
                howManyCrash += 1;
                nodesCrashed.set(link.getRightId(), true);

                System.out.println("Finding a new neighbour");
                link.incrementRightId();
                game.setCurrentPlayer(); // Spostato da riga 379 (prima di currentPlayer = game.getCurrentPlayer();)
                if (link.getRightId() == link.getMyId()) {
                    System.out.println("Unico giocatore, partita conclusa! Vittoria");
                    // TODO update gui
                    System.exit(0);
                }
            }

            // Move moveToPlay = game.myTurn();

            ringBroadcast.incrementMessageCounter();
            int messageCounter = ringBroadcast.retrieveMsgCounter();
            boolean success = false;
            while (!success) {
                System.out.println("Sending message # " + messageCounter);
                // ringBroadcast.send(messageMaker.newGameMessage(moveToPlay, ringBroadcast.retrieveMsgCounter(), howManyCrash));
                ringBroadcast.send(messageMaker.newGameMessage(msg, messageCounter, howManyCrash));
                success = true;
            }

            //game.updateAnyCrash(link.getNodes(), link.getMyId());
            // TODO mosse player - - - logica gioco


            // invio CrashMessage se si sono verificati crash
            if (anyCrash) {
                howManyCrash += 1;
                for (int i = 0; i < nodesCrashed.size(); i++) {
                    if (nodesCrashed.get(i)) {
                        ringBroadcast.incrementMessageCounter();
                        int messageCounterCrash = ringBroadcast.retrieveMsgCounter();
                        System.out.println("Sending CrashMessage: " + messageCounterCrash);
                        ringBroadcast.send(messageMaker.newCrashMessage(i, messageCounterCrash, howManyCrash));
                    }
                }
            } else {
                game.setCurrentPlayer(); // Spostato da riga 379 (prima di currentPlayer = game.getCurrentPlayer();)
                System.out.println("Next Player is " + players[currentPlayer].getUsername() + " id " + currentPlayer);
            }
            currentPlayer = game.getCurrentPlayer();
        }
    }

    private static void checkLastNode() {
        if (link.getRightId() == link.getMyId()) {
            game.updateCrash(link.getRightId());
            game.setCurrentPlayer(link.getMyId());
            System.out.println("Unico giocatore, partita conclusa. Vittoria");
            System.exit(0);
        }
    }

}
