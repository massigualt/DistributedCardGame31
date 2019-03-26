package distributedLogic.net.router;


import distributedLogic.net.Link;
import distributedLogic.net.ServiceBulk;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.Message;

import java.rmi.RemoteException;


/**
 * Classe che estende la classe AbstractRouter (incaricata dei messaggi di gioco)
 */
public class Router extends AbstractRouter {

    private GameMessage gameMessage;


    public Router(Link link, GameMessage gameMessage, RouterFactory rmaker) {

        super(link, gameMessage);
        this.gameMessage = gameMessage;
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
    protected void performCallHook(ServiceBulk to) {
        GameMessage cloneMsg = (GameMessage) gameMsg.clone();
        cloneMsg.setFromId(link.getMyId());
        try {
            to.getBroadcast().forward(cloneMsg); //chiamata rmi
        } catch (RemoteException rE) {
            rE.printStackTrace();
            System.out.println("RemoteException");
        }
    }
}
