package distributedLogic;

import java.net.InetAddress;

/**
 * La classe player estende la classe node per aggiungere funzionalità specifiche del giocatore
 */
public class Player extends Node {
    private String username;
    private int cardsNumber;
    private boolean busso;
    private int vite;


    public Player(String username, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.username = username;
        this.cardsNumber = 3;
        this.busso = false;
        this.vite = 3;
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

    public int getVite() {
        return vite;
    }

    public void setBusso() {
        this.busso = true;
    }

    public void decrementaVite() {
        vite--;
    }

    @Override
    public String toString() {
        return username + "@" + super.toString();
    }
}
