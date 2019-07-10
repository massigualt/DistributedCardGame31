package distributedLogic.net.remote;

import distributedLogic.game.Game;
import distributedLogic.net.Link;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.MessageFactory;
import distributedLogic.net.router.Router;
import distributedLogic.net.router.RouterFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe utilizzata per le chiamate remote RMi, implementa la classe remota IBroadcast,
 * per questo possono essere chiamati dei metodi in remoto di questa classe.
 * Gestisce l'arrivo dei messaggi, li riordina, li può scartare o inserire nel buffer.
 */
public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {
    private Link link = null;
    private RouterFactory routerMaker;
    private MessageFactory messageMaker;
    private BlockingQueue<GameMessage> buffer;
    private int messageCounter;
    private TreeMap<Integer, GameMessage> pendingMessage;
    private ReentrantLock msgCounterLock;
    private Game game;


    public RingBroadcast(BlockingQueue<GameMessage> buffer) throws RemoteException {
        this.buffer = buffer;
        this.messageCounter = 0;
        this.pendingMessage = new TreeMap<Integer, GameMessage>();
        this.msgCounterLock = new ReentrantLock();
    }


    public void configure(Link link, RouterFactory routerMaker, MessageFactory messageMaker) {
        this.link = link;
        this.routerMaker = routerMaker;
        this.messageMaker = messageMaker;
    }

    public void gameReference(Game game) {
        this.game = game;
    }

    public synchronized void send(GameMessage msg) {
        //quando r.run termina ho il link.Node[] aggiornato
        Router r = routerMaker.newRouter(msg);
        new Thread(r).start();
    }

    @Override
    public synchronized void forward(GameMessage message) throws RemoteException {
        if (enqueue(message)) {
            boolean[] nodesCrashed = new boolean[link.getNodes().length];
            Arrays.fill(nodesCrashed, false);
            int currentRightID = link.getRightId();

            while (link.checkAYANode(currentRightID) == false) {
                System.out.println("RING : checkAliveNodes -> " + currentRightID);
                currentRightID = link.getRightNeighbor(currentRightID);
                System.out.println("\u001B[101m Finding a new neighbour to send last mex received \u001B[0m . New RIGHT: " + currentRightID);
            }

            if (message.getNodeCrashed() == -1) {
                System.out.println("\u001B[34m\t\t FORWARD: GameMessage # " + message.getMessageId() + " [org# " + message.getOriginId() + " - rcv# " + message.getFromId() + " send to: " + link.getRightId() + "] MEX " + message.getMove().toString() + " \u001B[0m");
            } else {
                System.out.println("\u001B[35m\t\t FORWARD: CRASH " + message.getMessageId() + " org# " + message.getOriginId() + " - rcv# " + message.getFromId() + " send to: " + link.getRightId() + " MEX { crashNode: " + message.getNodeCrashed() + " } \u001B[0m");
            }

            // spedisco il messaggio arrivato dal nodo precedente
            send(message);

            if ((message.getNodeCrashed() != -1) && (this.game.getMyId() == this.game.getCurrentPlayer())) {
                game.getGameController().updateListDuringMove(message.getNodeCrashed());

            }

        } else {
            System.out.println("\t\tMessage discarded. " + message.toString());
        }

    }


    /**
     * Metodo che inserisce i messaggi nella coda se devono essere processati
     *
     * @param msg
     * @return
     */
    private synchronized boolean enqueue(GameMessage msg) {
        boolean doForward = false;

        if (msg.getOriginId() != link.getMyId()) {
            if ((msg.getMessageId() > messageCounter) && (pendingMessage.containsKey(msg.getMessageId()) == false)) {
                if (msg.getMessageId() == messageCounter + 1) {
                    try {
                        buffer.put(msg);
                    } catch (InterruptedException e) {
                        System.out.println("Error! Can't put message in the queue.");
                    }

                    incrementMessageCounter();

                    while (pendingMessage.containsKey(messageCounter + 1)) {
                        GameMessage pendMessage = pendingMessage.remove(messageCounter + 1);
                        try {
                            buffer.put(pendMessage);
                        } catch (InterruptedException e) {
                            System.out.println("error!");
                        }
                        incrementMessageCounter();
                    }
                } else {
                    pendingMessage.put(msg.getMessageId(), (GameMessage) msg.clone());
                }
                doForward = true;
            }
        }
        return doForward;
    }


    /**
     * Metodo che incrementa il msgcounter,viene utilizzato un lock per accedere alla variabile
     * in mutua esclusione
     */
    public void incrementMessageCounter() {
        msgCounterLock.lock();
        try {
            messageCounter++;
        } finally {
            msgCounterLock.unlock();
        }
    }


    /**
     * Metodo utilizzato dal controllo AYA sul vicino
     */
    public synchronized void checkNode() {
        System.out.println("\u001B[47m ????????? My neighbor is alive ??????? \u001B[0m");
    }

    public BlockingQueue<GameMessage> getBuffer() {
        return buffer;
    }

    public int retrieveMsgCounter() {
        return messageCounter;
    }

}
