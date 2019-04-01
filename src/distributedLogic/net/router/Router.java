package distributedLogic.net.router;


import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.messages.GameMessage;

import java.rmi.ConnectException;
import java.rmi.RemoteException;


/**
 * Classe che estende la classe AbstractRouter (incaricata dei messaggi di gioco)
 */
public class Router extends AbstractRouter {

    public Router(Link link, GameMessage gameMessage, RouterFactory rmaker) {
        super(link, gameMessage);
    }

    @Override
    public void run() {
        super.run();
    }

    /**
     * Metodo che utilizza una chiamata rmi, come parametro di ingresso
     * è presente un riferimento al vicino destro di tipo ServiceBulk
     * chiamata da messageBroadcast per inviare un messaggio
     *
     * @param to
     */
    @Override
    protected synchronized void performCallHook(ServiceBulk to) {
        System.out.println("*** ROUTER ***");
        GameMessage cloneMsg = (GameMessage) gameMsg.clone();
        cloneMsg.setFromId(link.getMyId());
        try {
            to.getBroadcast().forward(cloneMsg); //chiamata rmi
        } catch (ConnectException e) {
            System.out.println("Router-0");
            System.out.println("ConnectException " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("Router-1");
            System.out.println("RemoteException " + e.getMessage());
        }
    }
}
