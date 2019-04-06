package distributedLogic.net.remote;

import distributedLogic.game.Move;
import distributedLogic.net.Link;
import distributedLogic.net.messages.AYARouter;
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
    public synchronized void forward(GameMessage message) throws RemoteException {
        Move move = message.getMove();
        System.out.println("FORWARD -> coveredPick: " + move.isCoveredPick() + " - discardCard # " + move.getDiscardedCard() + " - " + move.getStatus() + " " + move.isBusso());
        if (enqueue(message)) {
            boolean[] nodesCrashed = new boolean[link.getNodes().length];
            Arrays.fill(nodesCrashed, false);
            int currentRightID = link.getRightId();

            while (link.checkAYANode(currentRightID) == false) {
                System.out.println("RING : checkAliveNodes");
                nodesCrashed[link.getRightId()] = true;
                currentRightID = link.getRightNeighbor(currentRightID, link.getLeftId());
                System.out.println("\u001B[101m Finding a new neighbour to send last mex received \u001B[0m . New RIGHT: " + currentRightID);

                if (link.getRightId() == link.getMyId()) {
                    System.out.println("Unico giocatore, partita conclusa");
                    System.exit(0);
                }
            }

            if (message.getNodeCrashed() == -1) {
                System.out.println("\u001B[100m FORWARD: crashMsg " + message.getId() + " org# " + message.getOriginId() + " - rcv# " + message.getFromId() + " send to: " + link.getRightId() + " { crashNode: " + message.getNodeCrashed() + " }\u001B[0m");
            } else {
                System.out.println("\u001B[102m FORWARD: gameMsg  " + message.getId() + " org# " + message.getOriginId() + " - rcv# " + message.getFromId() + " send to: " + link.getRightId() + " {" + message.getMove().getStatus() + " }\u001B[0m");
            }
            // spedisco il messaggio arrivato dal nodo precedente
            send(message);

        } else {
            System.out.println("Message discarded. " + message.toString());
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
        System.out.println("messageCounter-> " + messageCounter);
        System.out.println("messageId -> " + msg.getId());

        if (msg.getOriginId() != link.getMyId()) {
            if ((msg.getId() > messageCounter) && (pendingMessage.containsKey(msg.getId()) == false)) {
                if (msg.getId() == messageCounter + 1) {
                    try {
                        buffer.put(msg);
                        if (msg.getNodeCrashed() != -1) {
                            System.out.println("\u001B[44m [CrashMessage] put into the queue # " + msg.getId() + " { " + msg.getNodeCrashed() + " } \u001B[0m");
                        } else {
                            System.out.println("\u001B[44m [GameMessage] put into the queue # " + msg.getId() + " { " + msg.getMove().getStatus() + " } \u001B[0m");
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Error! Can't put message in the queue.");
                    }

                    incrementMessageCounter();
                    System.out.println("PENDING2: " + pendingMessage.size() + " contatore# " + retrieveMsgCounter() + " --> " + pendingMessage.toString());
                    while (pendingMessage.containsKey(messageCounter + 1)) {
                        GameMessage pendMessage = pendingMessage.remove(messageCounter + 1);
                        try {
                            buffer.put(pendMessage);
                            if (pendMessage.getNodeCrashed() != -1) {
                                System.out.println("\u001B[44m PendingMex put in buffer and remove from pendingMessage [CrashMessage] # " + pendMessage.getId() + " { " + pendMessage.getNodeCrashed() + " } \u001B[0m");
                            } else {
                                System.out.println("\u001B[44m PendingMex put in buffer and remove from pendingMessage [GameMessage] #  " + pendMessage.getId() + " { " + pendMessage.getMove().getStatus() + " } \u001B[0m");
                            }
                        } catch (InterruptedException e) {
                            System.out.println("error!");
                        }
                        incrementMessageCounter();
                    }
                } else {
                    pendingMessage.put(msg.getId(), (GameMessage) msg.clone());
                    if (msg.getNodeCrashed() != -1) {
                        System.out.println("\u001B[44m [CrashMessage] put into pendingMessage # " + msg.getId() + " { " + msg.getNodeCrashed() + " } \u001B[0m");
                    } else {
                        System.out.println("\u001B[44m [GameMessage] put into pendingMessage # " + msg.getId() + " { " + msg.getMove().getStatus() + " } \u001B[0m");
                    }
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
