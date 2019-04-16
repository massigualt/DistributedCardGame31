package distributedLogic.net.remote;

import distributedLogic.net.messages.GameMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBroadcast extends Remote {

    public void forward(GameMessage message) throws RemoteException;

}
