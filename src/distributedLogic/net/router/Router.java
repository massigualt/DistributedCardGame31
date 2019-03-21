package distributedLogic.net.router;

import distributedLogic.net.Link;
import distributedLogic.net.messages.Message;


public class Router {
    private Link link;
    private Message message;

    public Router(Link link, Message message) {
        this.link = link;
        this.message = message;
    }

}
