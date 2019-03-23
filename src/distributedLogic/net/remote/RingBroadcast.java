package distributedLogic.net.remote;

import distributedLogic.net.Link;
import distributedLogic.net.messages.AYARouter;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.MessageFactory;
import distributedLogic.net.router.Router;
import distributedLogic.net.router.RouterFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {
    private Link link = null;
    private RouterFactory routerMaker;
    private MessageFactory messageMaker;
    private int messageCounter;


    public RingBroadcast() throws RemoteException {
    }


    public void configure(Link link, RouterFactory routerMaker, MessageFactory messageMaker) {
        this.link = link;
        this.routerMaker = routerMaker;
        this.messageMaker = messageMaker;
    }

    public synchronized void send(GameMessage msg) {
        //quando r.run termina ho il link.Node[] aggiornato
        Router r = routerMaker.newRouter(msg);
        new Thread(r).start();
    }

    /**
     * Avvio controllo AYA
     */
    public synchronized void sendAYA() {
        AYARouter r = routerMaker.newAYARouter();
        new Thread(r).start();
    }


    @Override
    public void forward(GameMessage message) throws RemoteException {

        boolean anyCrash = false;
        boolean[] nodesCrashed = new boolean[link.getNodes().length];
        Arrays.fill(nodesCrashed, false);
        int initialMsgCrash = message.getHowManyCrash();


        // TODO update Link
        while (link.checkAliveNodes() == false) {
            message.incrementCrash();
            anyCrash = true;
            nodesCrashed[link.getRightId()] = true;
            System.out.println("Finding a new neighbour");
            link.incrementRightId();
            if (link.getRightId() == link.getMyId()) {
                System.out.println("Unico giocatore, partita conclusa");
                System.exit(0);
            }
        }

        // spedisco il messaggio arrivato dal nodo precedente
        send(message);

        if (anyCrash) {
            // TODO
        }
    }


    /**
     * Metodo utilizzato dal controllo AYA sul vicino
     */
    public synchronized void checkNode() {
        System.out.println("My neighbor is alive");
    }
}
