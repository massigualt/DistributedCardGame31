package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.messages.Message;

public class RouterFactory {
    private Link link;

    public RouterFactory(Link link) {
        this.link = link;
    }

    public Router newRouter(Message message) {
        return new Router(link, message);
    }

}
