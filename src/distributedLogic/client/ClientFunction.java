package distributedLogic.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientFunction extends UnicastRemoteObject implements PlayersInterface {

    protected ClientFunction() throws RemoteException {
    }

    public void receive(String message) throws RemoteException { System.out.println(message); }


}
