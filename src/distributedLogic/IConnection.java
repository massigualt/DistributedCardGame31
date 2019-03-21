package distributedLogic;

import distributedLogic.net.remote.IParticipant;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConnection extends Remote {
    boolean subscribe(IParticipant participant, Player player) throws RemoteException;
}
