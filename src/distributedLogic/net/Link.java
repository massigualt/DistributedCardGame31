package distributedLogic.net;

import distributedLogic.Node;

import java.util.List;

public class Link {
    private Node[] nodes;
    private Node me;
    private int myId = 0;
    private int rightId = 0;
    private int leftId = 0;

    public Link(Node me, Node[] nodes) {
        this.nodes = nodes;
        this.me = me;
        configure();
    }

    public int getMyId() {
        return myId;
    }

    public void printNodes() {
        for (Node node : nodes) {
            String string = " ";
            if (node.getId() == myId) {
                string = "IO -> " + node.toString();
            } else if (node.getId() == leftId) {
                string = "SX -> " + node.toString();
            } else if (node.getId() == rightId) {
                string = "DX -> " + node.toString();
            }
            System.out.println(string);
        }
    }

    private void configure() {
        for (int i = 0; i < nodes.length; i++) {
            if (me.compareTo(nodes[i]) == 0) {
                myId = i;
                leftId = backward(i, nodes.length);
                rightId = (i + 1) % nodes.length;
            }
        }
    }

    private int backward(int i, int size) {
        if (i - 1 < 0) {
            return size - 1;
        } else {
            return i - 1;
        }
    }
}
