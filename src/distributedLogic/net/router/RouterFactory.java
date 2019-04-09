package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.messages.GameMessage;

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

}
