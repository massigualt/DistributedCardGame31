package distributedLogic;

import java.net.InetAddress;

public class Player extends Node {
    private String name;
    private int cardsNumber = 0;
    private boolean busso = false;


    public Player(String name, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCardsNumber() {
        return cardsNumber;
    }

    public boolean isBusso() {
        return busso;
    }

    @Override
    public String toString() {
        return name + "@" + super.toString();
    }
}
