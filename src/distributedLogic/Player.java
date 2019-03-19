package distributedLogic;

import java.net.InetAddress;

public class Player extends Node {
    private String name;


    public Player(String name, InetAddress inetAddr, int port) {
        super(inetAddr, port);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "@" + super.toString();
    }
}
