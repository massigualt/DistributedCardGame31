package distributedLogic;

import distributedLogic.remote.IPartecipant;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnection extends Remote {
    public boolean subscribe(IPartecipant partecipant, Player player) throws RemoteException;
}
