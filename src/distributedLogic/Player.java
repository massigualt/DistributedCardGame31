package distributedLogic;

import distributedLogic.game.Hand;

import java.net.InetAddress;

/**
 * La classe player estende la classe node per aggiungere funzionalità specifiche del giocatore
 */
public class Player extends Node {
    private String username;
    private Hand hand;
    private int cardsNumber;
    private boolean busso;


    public Player(String username, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.username = username;
        this.cardsNumber = 3;
        this.hand = null;
        this.busso = false;

    }

    public String getUsername() {
        return username;
    }

    public int getCardsNumber() {
        return cardsNumber;
    }

    public boolean isBusso() {
        return busso;
    }


    public void saidBusso() {
        this.busso = true;
    }


    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    @Override
    public String toString() {
        return username + "@" + super.toString();
    }
}
