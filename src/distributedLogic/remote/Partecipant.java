package distributedLogic.remote;

import distributedLogic.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Partecipant extends UnicastRemoteObject implements IPartecipant {
    private Player[] players;
    private boolean gotPlayers = false;

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
