package distributedLogic.remote;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPartecipant extends Remote {
    void configure(Player[] players, Hand hand, Card firstCard, Deck deck) throws RemoteException;
}
