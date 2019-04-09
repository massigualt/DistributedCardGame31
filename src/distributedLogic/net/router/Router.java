package distributedLogic.net.router;


import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.messages.GameMessage;

import java.rmi.ConnectException;
import java.rmi.RemoteException;


/**
 * Classe incaricata ad inoltrare i messaggi di gioco
 */
public class Router implements Runnable {
    private Link link;
    private GameMessage gameMessage;

    public Router(Link link, GameMessage gameMessage, RouterFactory rmaker) {
        this.link = link;
        this.gameMessage = gameMessage;
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

    /**
     * Metodo che utilizza una chiamata rmi, come parametro di ingresso
     * è presente un riferimento al vicino destro di tipo ServiceBulk
     * chiamata da messageBroadcast per inviare un messaggio
     *
     * @param to
     */
    protected void performCallHook(ServiceBulk to) {
        GameMessage cloneMsg = (GameMessage) this.gameMessage.clone();
        cloneMsg.setFromId(link.getMyId());
        try {
            to.getBroadcast().forward(cloneMsg); //chiamata rmi
        } catch (ConnectException e) {
            System.out.println("ConnectException " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("RemoteException " + e.getMessage());
        }
    }
}
