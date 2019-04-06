package distributedLogic.game;

import GUI.view.GameController;
import distributedLogic.Node;
import distributedLogic.Player;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

public class Game {

    private Deck uncoveredDeck;
    private Deck coveredDeck;
    private Player[] players;
    private int currentPlayer = 0;
    private int myId;
    private Move currentMove = null, myMove;
    // TODO other variables
    private boolean saidBusso = false;
    private boolean concluso;
    private GameController gameController;

    public Game(Card uncoveredCard, Deck covered, Player[] players, int myId, GameController gameController, ClientLogic clientLogic) {
        this.uncoveredDeck = new Deck();
        this.uncoveredDeck.putCardOnTop(uncoveredCard);
        this.coveredDeck = covered;
        this.players = players;
        this.myId = myId;
        this.concluso = false;
        this.gameController = gameController;
        this.getGameController().initializeInterface(this, clientLogic);
    }


    public void updateMove(Move myMove) {
        System.out.println("UPDATE-MOVE -> coveredPick: " + myMove.isCoveredPick() + " - discardCard # " + myMove.getDiscardedCard() + " - " + myMove.getStatus() + " " + myMove.isBusso());

        Card card;
        // update DECK pesca
        if (myMove.isCoveredPick()) {
            card = pickFromCoveredDeck(myMove.getPlayerMove());
        } else {
            card = pickFromUncoveredDeck(myMove.getPlayerMove());
        }
        System.out.println("CARTA PESCATA: " + card.toString());
        // update hand currentPlayer
        discardCard(myMove.getDiscardedCard(), myMove.getPlayerMove());
        this.players[myMove.getPlayerMove()].getHandClass().orderCard();
        this.gameController.updateTableCardAfterRemoteMove();


        // Il giocatore pesca e scarta la carta, e puoi bussare
        // TODO logica turno

        setCurrentPlayer();
    }

    public Player[] getPlayers() {
        return players;
    }

    public Deck getUncoveredDeck() {
        return uncoveredDeck;
    }

    public Deck getCoveredDeck() {
        return coveredDeck;
    }

    public int getMyId() {
        return myId;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer() {
        this.currentPlayer = nextPlayer(currentPlayer);
    }

    public void setCurrentPlayer(int id) {
        this.currentPlayer = id;
    }

    public boolean isConcluso() {
        return concluso;
    }

    public void setConcluso() {
        this.concluso = true;
    }

    public Hand getHand(int id) {
        return this.players[id].getHandClass();
    }

    public Hand getMyHand() {
        return this.players[myId].getHandClass();
    }

    private int nextPlayer(int currentPlayer) {
        int nextPlayer = -1;
        int current = (currentPlayer + 1) % players.length;
        // TODO
        do {
            nextPlayer = current;
            current = (nextPlayer + 1) % players.length;
        } while (!players[nextPlayer].isActive());
        return nextPlayer;
    }

    public void updateAlivePlayers(List<Boolean> newAlivePlayers) {
        List<Boolean> opponents = ((List) ((ArrayList) newAlivePlayers).clone());
        opponents.set(myId, false);

        if (!opponents.contains(true)) {
            concluso = true;
            // TODO gui update
        } else {
            // at least one opponent is alive => find changes
            List<Boolean> tmpmap = ((List) ((ArrayList) newAlivePlayers).clone());
            if (!tmpmap.contains(true)) {
                // conti i giocatori che hanno fatto crash
                int numberOfCrash = 0;
                for (int i = 0; i < players.length; i++) {
                    if (tmpmap.get(i)) {
                        numberOfCrash++;
                    }
                }
                System.out.println("Giocatori crash: " + numberOfCrash);
                // TODO update gui (rimuovere i giocatori che hanno fatto crash)
            }
        }
        //alivePlayers = newAlivePlayers;
        System.out.println("GAME: Alive players map is " + newAlivePlayers);
    }

    public void updateCrash(int nodeCrashed) {
        //alivePlayers.set(nodeCrashed, false);
    }

    public void updateAnyCrash(Node[] nodes, int myId) {
        boolean crash = true;
        int i = (myId + 1) % nodes.length;

        while (crash) {
            if (!nodes[i].isActive()) {
                //this.alivePlayers.set(i, false);
                i = (myId + 1) % nodes.length;
            } else {
                crash = false;
            }
        }
    }

    public Card pickFromCoveredDeck(int id) {
        Card cartaPescata = this.coveredDeck.dealCardOnTop();
        this.players[id].getHandClass().takeCard(cartaPescata);

        if (this.coveredDeck.getPile().size() == 0) {
            Card singolaCarta = this.uncoveredDeck.dealCardOnTop();
            this.coveredDeck.setPile((LinkedList<Card>) this.uncoveredDeck.getPile().clone());
            this.coveredDeck.shuffle();

            this.uncoveredDeck.cleanDeck();
            this.uncoveredDeck.putCardOnTop(singolaCarta);
        }

        return cartaPescata;
    }

    public Card pickFromUncoveredDeck(int id) {
        Card cartaPescata = this.uncoveredDeck.dealCardOnTop();
        this.players[id].getHandClass().takeCard(cartaPescata);

        return cartaPescata;
    }

    public void discardCard(int position, int id) {
        Card cartaRimossa = this.players[id].getHandClass().removeCard(this.players[id].getHandClass().getCard(position));
        this.uncoveredDeck.putCardOnTop(cartaRimossa);
        System.out.println("CARTA RIMOSSA: " + cartaRimossa.toString());
    }

    public GameController getGameController() {
        return gameController;
    }
}
