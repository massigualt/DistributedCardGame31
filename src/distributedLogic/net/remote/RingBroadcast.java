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
    //TODO ?? Client clientBoard;


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
    public void forward(GameMessage message) throws RemoteException {

        if (enqueue(message)) {

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

                // 1 per il gamemessage del nodo
                int nextIdMsg = initialMsgCrash + messageCounter + 1;


                for (int i = 0; i < nodesCrashed.length; i++) {

                    if (nodesCrashed[i] == true) {


                        System.out.println("Sending a CrashMessage id " + nextIdMsg + " for node " + i);

                        //Invio msg di crash senza gestione dell'errore
                        GameMessage msgProv = messageMaker.newCrashMessage(i, nextIdMsg, 0);

                        if (initialMsgCrash == 0) {
                            incrementMessageCounter();
                        } else {
                            pendingMessage.put(nextIdMsg, (GameMessage) msgProv.clone());
                        }

                        send(msgProv);
                        nextIdMsg = nextIdMsg + 1;
                        System.out.println("Update Board crash");


                    }
                }
            }
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
        System.out.println("initialMsgCrash -> " + msg.getHowManyCrash());
        System.out.println("messageCounter-> " + messageCounter);
        System.out.println("messageId -> " + msg.getId());

        if (msg.getOriginId() != link.getMyId()) {
            if ((msg.getId() > messageCounter) && (pendingMessage.containsKey(msg.getId()) == false)) {
                if (msg.getId() == messageCounter + 1) {
                    try {
                        buffer.put(msg);
                        System.out.println("message put into the queue");
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
                    pendingMessage.put(msg.getId(), (GameMessage) msg.clone());
                }
                doForward = true;
            }
        }
        return doForward;

    }


    /**
     * Metodo utilizzato dal controllo AYA sul vicino
     */
    public synchronized void checkNode() {
        System.out.println("My neighbor is alive");
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

    public int retrieveMsgCounter() {
        return messageCounter;
    }

}
