package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.Message;

public abstract class AbstractRouter implements Runnable {
    protected Link link;

    protected GameMessage gameMsg;
    protected Message crashMsg;


    /**
     * Metodo utilizzato per creare un AbstractRouter per gestire un GameMessage
     *
     * @param link
     * @param gameMsg
     */
    public AbstractRouter(Link link, GameMessage gameMsg) {
        this.link = link;
        this.gameMsg = gameMsg;
        this.crashMsg = null;
    }


    /**
     * Metodo utilizzato per creare un AbstractRouter per gestire un CrashMessage
     *
     * @param link
     * @param crashMsg
     */
    public AbstractRouter(Link link, Message crashMsg) {
        this.link = link;
        this.gameMsg = null;
        this.crashMsg = crashMsg;
    }

    /**
     * Metodo utilizzato per creare un Abstract Router per gestire un AYA request
     *
     * @param link
     */
    public AbstractRouter(Link link) {
        this.link = link;
    }


    @Override
    public void run() {

        try {
            // Se non viene trovato il riferimento si imposta active = false nel node
            ServiceBulk right = link.getRightNode();
            performCallHook(right);
            System.out.println("I got right reference");
        } catch (NullPointerException e) {
            // destinatario non raggiungibile
            System.out.println("Can't forward the message to neighbour.");
            e.printStackTrace();
        }

    }

    protected abstract void performCallHook(ServiceBulk to);
}
