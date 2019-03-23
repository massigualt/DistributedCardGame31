package distributedLogic.net;

import distributedLogic.Node;
import distributedLogic.client.StartClient;
import distributedLogic.net.remote.IBroadcast;

import java.net.MalformedURLException;
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
    private IBroadcast leftNode = null;


    public Link(Node me, Node[] nodes) {
        this.me = me;
        this.nodes = nodes;

        configure();
        //System.out.println(" L: " + leftId + " ME: " + myId + " R: " + rightId);
    }

    private void configure() {
        System.out.println("IO: " + me.toString());
        for (int i = 0; i < nodes.length; i++) {
            if (me.compareTo(nodes[i]) == 0) {
                myId = i;
                leftId = backward(i);
                rightId = forward(i);
            }
        }
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

    public ServiceBulk getLeftNode() {
        boolean anyCrash = false;

        if (leftNode == null) {
            boolean success = false;
            while (!success) {
                leftId = getLeftNeighbor(leftId, rightId);
                if (leftId == -1) {
                    System.out.println("LEFT-ID -1");// TODO exception
                }
                try {
                    leftNode = lookupNode(leftId);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    anyCrash = true;
                }
            }
        }
        return new ServiceBulk(leftNode, leftId, anyCrash);
    }

    public ServiceBulk getRightNode() {
        boolean anyCrash = false;

        if (rightNode == null) {
            boolean success = false;
            while (!success) {
                rightId = getRightNeighbor(rightId, leftId);
                if (rightId == -1) {
                    System.out.println("RIGHT-ID -1");// TODO exception
                }
                try {
                    rightNode = lookupNode(rightId);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    anyCrash = true;
                }
            }
        }
        return new ServiceBulk(rightNode, rightId, anyCrash);
    }

    private IBroadcast lookupNode(int id) throws Exception {
        IBroadcast broadcast = null;

        String url = "rmi://" + nodes[id].getInetAddress().getCanonicalHostName() + ":" + nodes[id].getPort() + "/" + StartClient.BC_SERVICE;

        boolean success = false;
        try {
            System.out.println("Looking up " + url);
            broadcast = (IBroadcast) Naming.lookup(url);
            success = true;
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

        if (!success) {
            nodes[id].setNodeCrashed();
            throw new Exception();
        }
        return broadcast;
    }


    public boolean checkAliveNodes() {
        int id = getRightId();
        boolean success = false;
        String url = "rmi://" + nodes[id].getInetAddress().getCanonicalHostName() + ":" + nodes[id].getPort() + "/" + StartClient.BC_SERVICE;

        try {
            System.out.println("looking up " + url);
            IBroadcast broadcast = (IBroadcast) Naming.lookup(url);
            success = true;
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

        if (!success) {
            nodes[id].setNodeCrashed();
        }
        return success;
    }

    // TODO checkAYANode

    public void incrementRightId() {
        rightId = forward(rightId);
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

        return -1;
    }

    private int getRightNeighbor(int from, int to) {
        for (int i = from; i != to; i = forward(i)) {
            if (nodes[i].isActive()) {
                return i;
            }
        }
        if (nodes[to].isActive()) {
            return to;
        }
        return -1;
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
