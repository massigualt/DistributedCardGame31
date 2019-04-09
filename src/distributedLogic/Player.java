package distributedLogic;

import distributedLogic.game.Hand;

import java.net.InetAddress;

/**
 * La classe player estende la classe node per aggiungere funzionalità specifiche del giocatore
 */
public class Player extends Node {
    private String username;
    private Hand hand;
    private boolean busso;
    private int numberMoves;


    public Player(String username, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.username = username;
        this.hand = null;
        this.busso = false;
        this.numberMoves = 0;
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
        this.numberMoves ++;
    }

    @Override
    public String toString() {
        return username + "@" + super.toString();
    }
}
