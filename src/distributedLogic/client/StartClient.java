package distributedLogic.client;

import distributedLogic.IConnection;
import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;
import distributedLogic.net.Link;
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
    private static RingBroadcast ringBroadcast;
    private static Player[] players;
    private static int myId;

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
        String serviceURL = "rmi://" + localHost.getCanonicalHostName() + ":" + port + "/" + BC_SERVICE;

        try {
            LocateRegistry.createRegistry(port);
            ringBroadcast = new RingBroadcast();
            System.out.println("CLIENT: Registering Broadcast service at " + serviceURL);
            Naming.rebind(serviceURL, ringBroadcast);
        } catch (RemoteException e) {
            LocateRegistry.getRegistry(CONNECTION_PORT).list();
            System.out.println("rmiregistry already started: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException already started: " + e.getMessage());
        }

        // TODO SERVER connection
        boolean result = false;
        Participant participant = null;
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

        // TODO RESULT from SERVER
        if (result) {
            System.out.println("CLIENT: " + "I've been accepted, I'll never be alone :-)");
            players = participant.getPlayers();

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



        } else {
            System.out.println("EROREEEEEEEEEE");


        }

    }
}
