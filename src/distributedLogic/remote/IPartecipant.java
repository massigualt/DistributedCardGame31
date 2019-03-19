package distributedLogic.remote;

import distributedLogic.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPartecipant extends Remote {
    void configure(Player[] players) throws RemoteException;
}
