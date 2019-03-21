package distributedLogic;

import distributedLogic.net.remote.IParticipant;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnection extends Remote {
    public boolean subscribe(IParticipant partecipant, Player player) throws RemoteException;

    void broadcastMessage(String username, String message) throws RemoteException;

}
