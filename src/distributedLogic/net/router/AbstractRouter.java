package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.Message;

public abstract class AbstractRouter implements Runnable {
    protected Link link;
    protected GameMessage gameMsg;

    /**
     * Metodo utilizzato per creare un AbstractRouter per gestire un GameMessage
     *
     * @param link
     * @param gameMsg
     */
    public AbstractRouter(Link link, GameMessage gameMsg) {
        this.link = link;
        this.gameMsg = gameMsg;
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
        System.out.println("RUN ABSTRACT ROUTER");
        ServiceBulk right = null;
        try {
            // Se non viene trovato il riferimento si imposta active = false nel node

            //Riferimento al vicino destro
            right = link.getRightNode();
            performCallHook(right);
            System.out.println("I got right reference");

        } catch (NullPointerException np) {
            // destinatario non raggiungibile
            System.out.println("Can't forward the message to neighbour.");
        }

    }

    protected abstract void performCallHook(ServiceBulk to);
}
