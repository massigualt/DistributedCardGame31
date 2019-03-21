package distributedLogic;

import java.io.Serializable;
import java.net.InetAddress;

public class Node implements Serializable, Comparable<Node> {
    private InetAddress inetAddress;
    private int port;
    private int addr;
    private int id;

    public Node(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.addr = inet2int(inetAddress);
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
        System.out.println("ADDr:" + addr);
        System.out.println("player" + player.addr);

        // confronto indirizzo
        if (addr < player.addr)
            return -1;
        if (addr > player.addr)
            return 1;
        // confronto porta
        if (port < player.port)
            return -1;
        if (port > player.port)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        return inetAddress.getHostAddress() + ":" + port;
    }

    /**
     * Converts a inet address into an integer. This semplifies comparisons.
     *
     * @param inetAddr The inet address to be translated into an integer.
     * @return The integer value of the inet address.
     */
    private int inet2int(InetAddress inetAddr) {
        byte[] bytes = inetAddr.getAddress();
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }
}
