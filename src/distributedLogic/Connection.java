package distributedLogic;

import GUI.LoginController;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;
import distributedLogic.net.remote.IParticipant;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static distributedLogic.game.Card.*;

public class Connection extends UnicastRemoteObject implements IConnection {

    private Player[] players;
    private IParticipant[] participants;
    private int playersMaxNumber;
    private int playersNumber = 0;
    private boolean acceptParticipants = true;
    public static final int CARDS_PER_PLAYER = 3;

    private Deck deck;
    private Hand[] hand;
    private Card uncoveredCard;


    public Connection(int playersMaxNumber) throws RemoteException {
        this.playersMaxNumber = playersMaxNumber;
        this.players = new Player[playersMaxNumber];
        this.participants = new IParticipant[playersMaxNumber];

        this.hand = new Hand[playersMaxNumber];
        this.deck = initDeck();
        this.uncoveredCard = extractCard();
    }

    public Card extractCard() {
        Card card = deck.dealCardOnTop();
        return card;
    }

    public synchronized boolean subscribe(IParticipant participant, Player player) {

        if (playersNumber < playersMaxNumber && acceptParticipants) {

            if (isDuplicatedName(player, players, playersNumber)) { // TODO serve effettivamente
                System.out.println("CONNECTION: duplicated username -> " + player.toString());
                return false;
            }
            System.out.println("CONNECTION: new player -> " + player.toString());

            Hand tmpHand = new Hand();

            participants[playersNumber] = participant;
            players[playersNumber] = player;

            // TODO possiamo dare le carte al giocatore solo quando si avvia il gioco?
            for (int i = 0; i < CARDS_PER_PLAYER; i++)
                tmpHand.takeCard(deck.dealCardOnTop());
            hand[playersNumber] = tmpHand;

            playersNumber++;

            if (playersNumber == playersMaxNumber) {
                acceptParticipants = false;
                replyClients();
                notify();
            }
            return true;
        }
        System.out.println("The participant reached the maximum number!");
        return false;
    }

    public synchronized void endSigning() {
        if (acceptParticipants) {
            acceptParticipants = false;
            replyClients();
            notify();
        }
    }

    private boolean isDuplicated(Player target, Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (target.compareTo(players[i]) == 0)
                return true;
        }
        return false;
    }

    private boolean isDuplicatedName(Player target, Player[] players, int playersNumber) {
        for (int i = 0; i < playersNumber; i++) {
            if (players[i].getUsername().equalsIgnoreCase(target.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public synchronized Player[] getPlayers() {
        if (acceptParticipants)
            try {
                wait();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return players;
    }

    public synchronized int getPlayersNumber() {
        if (acceptParticipants)
            try {
                wait();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return playersNumber;
    }

    private void replyClients() {
        // Faccio in modo che i giocatori saranno i primi n
        final Player[] readyPlayers = new Player[playersNumber];
        System.arraycopy(players, 0, readyPlayers, 0, playersNumber);
        players = readyPlayers;

        // TODO gestione carte

        // configure partecipants
        for (int i = 0; i < playersNumber; i++) {
            final IParticipant p = participants[i];
            final int j = i;

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("CONNECTION: " + "Configuring participant " + j + ": " + readyPlayers[j] + "... ");

                        //TODO non lo so, da vedere per il render della gui
                        /*LoginController loginController = new LoginController();
                        loginController.setCanContinue();*/
                        p.configure(players, hand[j], uncoveredCard, deck);
                        System.out.println("CONNECTION: " + "Configuring participant " + j + ": " + readyPlayers[j] + "... done.");
                    } catch (RemoteException e) {
                        System.out.println("REMOTE EXCEPTION: " + e.getMessage());
                    }
                }
            };
            t.start();
        }
    }

    private Deck initDeck() {
        // genera e mescola il mazzo di carte

        Deck deck = new Deck();
        for (Seme seme : Seme.values()) {
            for (Rank rank : Rank.values()) {
                deck.putCardOnTop(new Card(seme, rank));
                //secondo mazzo
                //deck.putCardOnTop(new Card(seme, rank));
            }
        }
        deck.shuffle();
        return deck;
    }

}
