package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.messages.AYARouter;
import distributedLogic.net.messages.GameMessage;
import distributedLogic.net.messages.Message;

public class RouterFactory {
    private Link link;

    public RouterFactory(Link link) {
        this.link = link;
    }

    /**
     * Crea un newRouter che può essere utilizzato per gestire msg di gioco oppure di crash (GameMessage)
     * @param gameMessage
     * @return
     */
    public Router newRouter(GameMessage gameMessage) {
        return new Router(link, gameMessage, this);
    }

    /**
     * Crea un AYARouter per gestire il controllo AYA sui vicini
     * @return
     */
    public AYARouter newAYARouter() {
        return new AYARouter(link,this);
    }
}
