package distributedLogic.net.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RingBroadcast extends UnicastRemoteObject implements IBroadcast {

    protected RingBroadcast() throws RemoteException {
    }

    public void receive(String message) throws RemoteException {
        System.out.println(message);
    }
}
