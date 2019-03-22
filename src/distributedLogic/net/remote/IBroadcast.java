package distributedLogic.net.remote;

import distributedLogic.net.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBroadcast extends Remote {
    void forward(Message message) throws RemoteException;
}
