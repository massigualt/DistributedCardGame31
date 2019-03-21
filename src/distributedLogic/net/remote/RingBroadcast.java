package distributedLogic.net.remote;


import distributedLogic.net.Link;
import distributedLogic.net.messages.Message;
import distributedLogic.net.router.Router;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {
    private Link link = null;

    public RingBroadcast() throws RemoteException {
    }

    public void configure(Link link) {
        this.link = link;
    }

    public void send(Message message){

    }

    @Override
    public void forward(Message msg) throws RemoteException {
        // TODO
        Router router = new Router(link, msg);
    }
}
