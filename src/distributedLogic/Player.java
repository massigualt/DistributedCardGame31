package distributedLogic;

import distributedLogic.game.Hand;

import java.net.InetAddress;
import java.util.Comparator;

/**
 * La classe player estende la classe node per aggiungere funzionalità specifiche del giocatore
 */
public class Player extends Node {
    private String username;
    private Hand hand;
    private boolean busso;
    private int numberMoves;
    private int handScore;


    public Player(String username, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.username = username;
        this.hand = null;
        this.busso = false;
        this.numberMoves = 0;
        this.handScore = 0;
    }

    public String getUsername() {
        return username;
    }

    public boolean isBusso() {
        return busso;
    }


    public void sayBusso() {
        this.busso = true;
    }


    public Hand getHandClass() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public int getNumberMoves() {
        return numberMoves;
    }

    public void incrementNumberMoves() {
        this.numberMoves++;
    }

    public int getHandScore() {
        return handScore;
    }

    public void setHandScore(int handScore) {
        this.handScore = handScore;
    }

    public static Comparator<Player> playerComparator = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            int compare = o1.getHandScore() - o2.getHandScore();
            if (compare == 0)
                compare = Boolean.compare(o1.isBusso(), o2.isBusso());
            return compare;
        }
    };

    @Override
    public String toString() {
        return username + "@" + super.toString();
    }
}
