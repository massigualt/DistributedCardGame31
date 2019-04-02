package distributedLogic;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Classe padre di Player,ogni player crea un istanza di questa con il proprio indirizzo di rete e la porta.
 * Quando viene creato un oggetto della classe link viene passato come parametro un oggetto node invece che player.
 * Viene fatto perchè a livello di rete servono solo le info di node e non tutte quelle contenute in player.
 */
public class Node implements Serializable, Comparable<Node> {
    private InetAddress inetAddress;
    private int port;
    private int id;
    private boolean active;

    public Node(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.active = true;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public void setNodeCrashed() {
        this.active = false;
    }

    @Override
    public String toString() {
        return inetAddress.getHostAddress() + ":" + port;
    }

}
