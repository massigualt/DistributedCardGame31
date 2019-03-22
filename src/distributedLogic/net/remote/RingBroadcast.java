package distributedLogic.net.remote;

import distributedLogic.net.Link;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {
    Link link = null;

    public RingBroadcast() throws RemoteException {
    }

    public void receive(String message) throws RemoteException {
        System.out.println(message);
    }

    public void configure(Link link) {

    }
}
