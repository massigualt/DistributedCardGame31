package distributedLogic.net.remote;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe Partecipant utilizzata per notificare le info di gioco a tutti i giocatori
 */
public class Participant extends UnicastRemoteObject implements IParticipant {

    private Player[] players;
    private boolean gotPlayers = false;

    private Deck coveredDeck;
    private Hand hand;
    private Card firstCard;

    public Participant() throws RemoteException {
    }


    @Override
    public synchronized void configure(Player[] players, Hand hand, Card firstCard, Deck coveredDeck) throws RemoteException {
        this.players = players;
        this.hand = hand;
        this.firstCard = firstCard;
        this.coveredDeck = coveredDeck;
        this.gotPlayers = true;

        notifyAll();
        System.out.println("PARTICIPANT: Notify players list has been received!");
    }

    public synchronized Player[] getPlayers() {
        if (!gotPlayers) {
            try {
                System.out.println("PARTICIPANT: No players list unavailable: waiting...");
                wait();
                System.out.println("PARTICIPANT: Timeout elapsed or object notified!");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        return players;
    }

    public Hand getHand() {
        if (!gotPlayers)
            try {
                System.out.println("PARTICIPANT: No players list available: waiting for hand...");
                wait();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return hand;
    }


    public Card getFirstCard() {
        if (!gotPlayers)
            try {
                System.out.println("PARTICIPANT: No players list available: waiting for first card...");
                wait();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return firstCard;
    }


    public Deck getCoveredDeck() {
        if (!gotPlayers)
            try {
                System.out.println("PARTICIPANT: No players list available: waiting for covered deck...");
                wait();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        return coveredDeck;
    }


}
