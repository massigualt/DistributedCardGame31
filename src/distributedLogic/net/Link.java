package distributedLogic.net;

import distributedLogic.Node;
import distributedLogic.client.StartClient;
import distributedLogic.net.remote.IBroadcast;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;


public class Link {
    private Boolean[] aliveNodes;
    private Node[] nodes;
    private Node me;
    private int myId = 0;
    private int rightId = 0;
    private int leftId = 0;
    private IBroadcast left = null;
    private IBroadcast right = null;


    public Link(Node me, Node[] nodes) {
        this.nodes = nodes;
        this.me = me;
        this.aliveNodes = new Boolean[nodes.length];
        Arrays.fill(this.aliveNodes, true);
        configure();
        System.out.println(" L: " + leftId + " ME: " + myId + " R: " + rightId);
    }

    public int getMyId() {
        return myId;
    }

    public Boolean[] getAliveNodes() {
        return aliveNodes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public ServiceBulk getLeft() {
        boolean anyCrash = false;

        if (left == null) {
            boolean success = false;
            while (!success) {
                leftId = getLeftNeighbor(leftId, rightId);
                if (leftId == -1) {
                    System.out.println("LEFT-ID -1");// TODO exception
                }
                try {
                    left = lookuoNode(leftId);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    anyCrash = true;
                    setCrashed(leftId);
                }
            }
        }
        return new ServiceBulk(left, leftId, anyCrash);
    }

    public ServiceBulk getRight() {
        boolean anyCrash = false;

        if (right == null) {
            boolean success = false;
            while (!success) {
                rightId = getRightNeighbor(rightId, leftId);
                if (rightId == -1) {
                    System.out.println("RIGHT-ID -1");// TODO exception
                }
                try {
                    right = lookuoNode(rightId);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    anyCrash = true;
                    setCrashed(rightId);
                }
            }
        }
        return new ServiceBulk(right, rightId, anyCrash);
    }

    private IBroadcast lookuoNode(int id) throws Exception {
        IBroadcast broadcast = null;

        String url = "rmi://" + nodes[id].getInetAddress().getCanonicalHostName() + ":" + nodes[id].getPort() + "/" + StartClient.BC_SERVICE;

        boolean succes = false;
        try {
            broadcast = (IBroadcast) Naming.lookup(url);
            succes = true;
        } catch (NotBoundException e) {
            System.out.println("LINK: NotBoundException thrown while looking up " + url);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("LINK: MalformedURLException thrown while looking up " + url);
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("LINK: RemoteException thrown while looking up " + url);
            e.printStackTrace();
        }
        if (!succes) {
            throw new Exception();
        }
        return broadcast;
    }

    private void setCrashed(int index) {
        // TODO lock
        this.aliveNodes[index] = false;
    }


    private void configure() {
        System.out.println("IO: " + me.getPort());
        for (int i = 0; i < nodes.length; i++) {
            if (me.compareTo(nodes[i]) == 0) {
                myId = i;
                leftId = backward(i, nodes.length);
                rightId = forward(i, nodes.length);
            }
        }
    }

    private int getLeftNeighbor(int from, int to) {
        for (int i = from; i != to; i = backward(i, aliveNodes.length)) {
            if (aliveNodes[i]) {
                return i;
            }
        }
        if (aliveNodes[to])
            return to;
        return -1;
    }

    private int getRightNeighbor(int from, int to) {
        for (int i = from; i != to; i = forward(i, aliveNodes.length)) {
            if (aliveNodes[i]) {
                return i;
            }
        }
        if (aliveNodes[to])
            return to;
        return -1;
    }

    private int backward(int i, int size) {
        if (i - 1 < 0) {
            return size - 1;
        } else {
            return i - 1;
        }
    }

    private int forward(int i, int size) {
        return (i + 1) % size;
    }
}
