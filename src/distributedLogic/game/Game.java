package distributedLogic.game;

import GUI.view.GameController;
import distributedLogic.Node;
import distributedLogic.Player;
import distributedLogic.net.messages.GameMessage;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

public class Game {

    private Deck uncoveredDeck;
    private Deck coveredDeck;
    private Player me;
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
        this.me = players[myId];
        this.players = players;
        this.myId = myId;
        this.concluso = false;
        this.gameController = gameController;
        this.getGameController().initializeInterface(this, clientLogic);
        printInfo();
    }


    public void update(GameMessage m, int whoMystGoOn) {

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

    public Player getMe() {
        System.out.println("ME: " + myId);
        me.getHand().printHand();
        return me;
    }

    public void setMe(Player me) {
        this.me = me;
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

    public synchronized void updateMove(Move myMove) {
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
        this.players[myMove.getPlayerMove()].getHand().orderCard();
        this.gameController.updateTableCardHB();


        // Il giocatore pesca e scarta la carta, e puoi bussare
        // TODO logica turno

        setCurrentPlayer();
    }

    public Hand getHand(int id) {
        return this.players[id].getHand();
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
        this.players[id].getHand().takeCard(cartaPescata);

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
        this.players[id].getHand().takeCard(cartaPescata);

        return cartaPescata;
    }

    public void discardCard(int position, int id) {
        // TODO controllo inutile?
        //if (this.players[currentPlayer].getHand().getNumberOfCards() == 4) {
        Card cartaRimossa = this.players[id].getHand().removeCard(this.players[id].getHand().getCard(position));
        this.uncoveredDeck.putCardOnTop(cartaRimossa);
        this.gameController.setStatus(3);
        //}
    }

    public GameController getGameController() {
        return gameController;
    }

    public void updateMe() {
        this.me.setHand(this.players[myId].getHand());
    }

    public void printInfo() {
        for (Player p : players) {
            System.out.println(p.getUsername());
            System.out.println(p.getId());
            p.getHand().printHand();
            System.out.println("-------------------");
        }
    }

}
