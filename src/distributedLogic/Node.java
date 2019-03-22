package distributedLogic;

import java.io.Serializable;
import java.net.InetAddress;

public class Node implements Serializable, Comparable<Node> {
    private InetAddress inetAddress;
    private int port;
    private int id;

    public Node(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Node player) {
        int meAddrInt = inetAddress.hashCode();
        int playerAddrInt = player.getInetAddress().hashCode();
        if (meAddrInt == playerAddrInt) {
            // client con ip identico
            meAddrInt = port;
            playerAddrInt = player.getPort();
        }

        // confronto indirizzo o porte a secondo dei casi
        if (meAddrInt < playerAddrInt) {
            return -1;
        }

        if (meAddrInt > playerAddrInt) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return inetAddress.getHostAddress() + ":" + port;
    }

}
