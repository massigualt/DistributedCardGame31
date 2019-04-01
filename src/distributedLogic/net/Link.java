package distributedLogic.net;

import distributedLogic.Node;
import distributedLogic.client.Client;
import distributedLogic.net.remote.IBroadcast;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Link {
    private Node[] nodes;
    private Node me;
    private int myId = 0;
    private int rightId = 0;
    private int leftId = 0;
    private IBroadcast rightNode = null;

    public Link(Node me, Node[] nodes) {
        this.me = me;
        this.nodes = nodes;
        configure();
        //System.out.println(" L: " + leftId + " ME: " + myId + " R: " + rightId);
    }

    private void configure() {
        this.myId = me.getId();
        this.leftId = backward(this.myId);
        this.rightId = forward(this.myId);

        // TODO superfluo poichè l'id lo prendiamo dall'array
//        System.out.println("IO: " + me.toString());
//        for (int i = 0; i < nodes.length; i++) {
//            if (me.compareTo(nodes[i]) == 0) {
//                myId = i;
//                leftId = backward(i);
//                rightId = forward(i);
//            }
//        }
    }

    public int getMyId() {
        return myId;
    }

    public int getRightId() {
        return rightId;
    }

    public int getLeftId() {
        return leftId;
    }

    public Node[] getNodes() {
        return nodes;
    }

    /* Metodo che recupera il riferimento all'oggetto RemoteBroadcast del nodo vicino destro
tramite il metodo lookupnode per poi potergli inviare i messaggi durante il gioco, successivamente
crea un oggetto di tipo ServiceBulk.*/
    public ServiceBulk getRightNode() {
        rightNode = lookupNode(rightId);
        return new ServiceBulk(rightNode, rightId);
    }

    private IBroadcast lookupNode(int id) {
        IBroadcast broadcast = null;

        String url = "rmi://" + nodes[id].getInetAddress().getHostAddress() + ":" + nodes[id].getPort() + "/" + Client.BC_SERVICE;

        boolean success = false;
        try {
            System.out.println("\u001B[92m Looking up (# " + id + ") " + url + " \u001B[0m");
            broadcast = (IBroadcast) Naming.lookup(url);
            success = true;
        } catch (NotBoundException e) {
            System.err.println("LINK: {lookupNode} NotBoundException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (MalformedURLException e) {
            System.err.println("LINK: {lookupNode} MalformedURLException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (ConnectException e) {
            System.err.println("LINK: {lookupNode} ConnectException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (RemoteException e) {
            System.err.println("LINK: {lookupNode} RemoteException thrown while looking up " + url + "\n" + e.getMessage());
        }

        if (!success) {
            nodes[id].setNodeCrashed();
        }
        return broadcast;
    }


    public boolean checkAliveNodes() {
        int id = getRightId();
        boolean success = false;
        String url = "rmi://" + nodes[id].getInetAddress().getHostAddress() + ":" + nodes[id].getPort() + "/" + Client.BC_SERVICE;

        try {
            System.out.println("\u001B[95m {checkAliveNodes} \u001B[0m: Looking up (# " + id + ")" + url);
            Naming.lookup(url);
            success = true;
        } catch (NotBoundException e) {
            System.err.println("LINK: {checkAliveNodes} NotBoundException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (MalformedURLException e) {
            System.err.println("LINK: {checkAliveNodes} MalformedURLException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (ConnectException e) {
            System.err.println("LINK: {checkAliveNodes} ConnectException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (RemoteException e) {
            System.err.println("LINK: {checkAliveNodes} RemoteException thrown while looking up " + url + "\n" + e.getMessage());
        }

        if (!success) {
            nodes[id].setNodeCrashed();
        }
        return success;
    }

    public boolean checkAYANode(int rightId) {
        boolean success = false;
        String url = "rmi://" + nodes[rightId].getInetAddress().getHostAddress() + ":" + nodes[rightId].getPort() + "/Broadcast";

        try {
            System.out.println("\u001B[92m {checkAYANode} \u001B[0m: looking up (# " + rightId + ") " + url);
            Naming.lookup(url);
            success = true;
        } catch (NotBoundException e) {
            System.err.println("LINK: {checkAYANode} NotBoundException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (MalformedURLException e) {
            System.err.println("LINK: {checkAYANode} MalformedURLException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (ConnectException e) {
            System.err.println("LINK: {checkAYANode} ConnectException thrown while looking up " + url + "\n" + e.getMessage());
        } catch (RemoteException e) {
            System.err.println("LINK: {checkAYANode} RemoteException thrown while looking up " + url + "\n" + e.getMessage());
        }

        return success;
    }

    private int getLeftNeighbor(int from, int to) {
        // TODO sistemare sia destra che sinsistra
        for (int i = from; i != to; i = backward(i)) {
            if (nodes[i].isActive()) {
                return i;
            }
        }
        if (nodes[to].isActive()) {
            return to;
        }

        return myId;
    }

    public int getRightNeighbor(int from, int to) {
        for (int i = from; i != to; i = forward(i)) {
            if (nodes[i].isActive()) {
                return i;
            }
        }
        if (nodes[to].isActive()) {
            return to;
        }
        return myId;
    }



    public void setNewNeighbor() {
        if (!nodes[leftId].isActive()) {
            // update left id
            leftId = getLeftNeighbor(leftId, rightId);
            System.out.println("\u001B[46m Il mio nuovo vicino SX: " + leftId + "\u001B[0m");
        }
        if (!nodes[rightId].isActive()) {
            // update right node
            rightId = getRightNeighbor(rightId, leftId);
            System.out.println("\u001B[46m Il mio nuovo vicino DX: " + rightId + "\u001B[0m");
        }
        System.out.println("\u001B[96m ---------------" + leftId + " - " + me.getId() + " - " + rightId + " --------------- \u001B[0m");
    }

    private int backward(int i) {
        int size = nodes.length;
        if (i - 1 < 0) {
            return size - 1;
        } else {
            return i - 1;
        }
    }

    private int forward(int i) {
        int size = nodes.length;
        return (i + 1) % size;
    }
}
