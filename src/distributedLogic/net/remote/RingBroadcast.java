package distributedLogic.net.remote;

import distributedLogic.net.Link;
import distributedLogic.net.router.Router;
import distributedLogic.net.router.RouterFactory;
import distributedLogic.net.messages.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {
    private Link link = null;
    private RouterFactory rmaker;

    public RingBroadcast() throws RemoteException {
    }


    public void configure(Link link, RouterFactory rmaker) {
        this.link = link;
        this.rmaker = rmaker;
    }

    @Override
    public void forward(Message message) throws RemoteException {
        System.out.println("Received msg: " + message.toString());

        // TODO waitCOnfig

        // TODO updateLinkMessage


        Router router = rmaker.newRouter(message);
        router.run();
    }
}
