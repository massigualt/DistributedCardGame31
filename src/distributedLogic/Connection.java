package distributedLogic;

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
        hand = new Hand[playersMaxNumber];
        deck = initDeck();
        uncoveredCard = extractCard();
    }

    public Card extractCard() {
        Card card = deck.dealCardOnTop();
        return card;
    }

    public synchronized boolean subscribe(IParticipant participant, Player player) {
        if (playersNumber < playersMaxNumber && acceptParticipants) {
            if (isDuplicatedName(player, players, playersNumber)) {
                System.out.println("CONNECTION: " + "duplicated player " + player);
                return false;
            }
            System.out.println("CONNECTION: " + "new player " + player);

            Hand tmpHand = new Hand();

            participants[playersNumber] = participant;
            players[playersNumber] = player;

            for (int i = 0; i < CARDS_PER_PLAYER; i++)
                tmpHand.takeCard(deck.dealCardOnTop());


            hand[playersNumber] = tmpHand;

            playersNumber++;

            if (playersNumber == playersMaxNumber) {
                acceptParticipants = false;
                sendJoin();
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
            sendJoin();
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
            if (players[i].getName().equalsIgnoreCase(target.getName())) {
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

    private void sendJoin() {
        // Faccio in modo che i giocatori saranno i primi n

        final Player[] readyPlayers = new Player[playersNumber];
        System.arraycopy(players, 0, readyPlayers, 0, playersNumber);
        players = readyPlayers;


        for (int i = 0; i < playersNumber; i++) {
            final IParticipant p = participants[i];
            final int j = i;

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("CONNECTION: " + "Configuring participant " + j + ": " + readyPlayers[j] + "... ");
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

    @Override
    public void broadcastMessage(String username, String message) throws RemoteException {
        String msg = username + ": " + message;

        //TODO receive(message)
    }


}
