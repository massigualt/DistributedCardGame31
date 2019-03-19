package distributedLogic.remote;

import distributedLogic.Player;
import distributedLogic.game.Deck;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Partecipant extends UnicastRemoteObject implements IPartecipant {

    private Player[] players;
    private boolean gotPlayers = false;

    private Deck coveredDeck;

    public Partecipant() throws RemoteException {
    }

    @Override
    public synchronized void configure(Player[] players) throws RemoteException {
        this.players = players;
        this.gotPlayers = true;
        notifyAll();
        System.out.println("PARTECIPANT: Notify players list has been received!");
    }

    public synchronized Player[] getPlayers() {
        if (!gotPlayers)
            try {
                System.out.println("PARTECIPANT: " + "No players list available: waiting...");
                wait(); // XXX
                System.out.println("PARTECIPANT: " + "Timeout elapsed or object notified!");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return players;
    }
}
