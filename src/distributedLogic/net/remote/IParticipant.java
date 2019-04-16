package distributedLogic.net.remote;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IParticipant extends Remote {
    void configure(Player[] players, Card firstCard, Deck deck) throws RemoteException;
}
