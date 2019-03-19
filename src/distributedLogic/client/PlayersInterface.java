package distributedLogic.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayersInterface extends Remote {

    void receive(String message) throws RemoteException;
}
