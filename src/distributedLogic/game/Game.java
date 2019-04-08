package distributedLogic.game;

import GUI.view.GameController;
import distributedLogic.Node;
import distributedLogic.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Game {

    private Deck uncoveredDeck;
    private Deck coveredDeck;
    private Player[] players;
    private int currentPlayer;
    private int myId;

    private boolean saidBusso;
    private int idBusso;
    private boolean concluso;
    private GameController gameController;

    public Game(Card uncoveredCard, Deck covered, Player[] players, int myId, GameController gameController, ClientLogic clientLogic) {
        this.currentPlayer = 0;
        this.uncoveredDeck = new Deck();
        this.uncoveredDeck.putCardOnTop(uncoveredCard);
        this.coveredDeck = covered;
        this.players = players;
        this.myId = myId;
        this.concluso = false;
        this.saidBusso = false;
        this.idBusso = -1;
        this.gameController = gameController;
        this.getGameController().initializeInterface(this, clientLogic);
    }


    public void updateMove(Move move) {
        System.out.println("UPDATE-MOVE [coveredPick: " + move.isCoveredPick() + " - discardCard # " + move.getDiscardedCard() + " - " + move.getStatus() + " " + move.isBusso() + "]");


        switch (move.getStatus()) {
            case "pick":
                Card card;
                if (move.isCoveredPick()) {
                    card = pickFromCoveredDeck(move.getPlayerMove());
                } else {
                    card = pickFromUncoveredDeck(move.getPlayerMove());
                }
                System.out.println("CARTA PESCATA: " + card.toString());
                break;
            case "discard":
                discardCard(move.getDiscardedCard(), move.getPlayerMove());
                this.players[move.getPlayerMove()].getHandClass().orderCard();
                break;
            case "busso":
                this.saidBusso(move.getPlayerMove());
                break;
            case "winner":
                declareWinner();
                break;
        }


        if (move.getStatus().matches("pick|discard")) {
            this.gameController.updateTableCardAfterRemoteMove(move.getStatus());
        }

        // Il giocatore pesca e scarta la carta, e può bussare
        // TODO logica turno


        if (move.getStatus().matches("discard|busso")) {
            setCurrentPlayer();
        }
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
        this.getGameController().updateCurrentPlayerGUI(this.currentPlayer);
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
        // TODO update gui after crash
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

    public void saidBusso(int id) {
        this.players[id].saidBusso();
        this.idBusso = id;
        this.saidBusso = true;
    }

    public void declareWinner() {
        setConcluso();
        gameController.lockUnlockElementTable(0);
        int max = 0;
        String name = null;
        for (Player p : players) {
            if (p.getHandClass().getHandPoints() > max && p.isActive()) {
                max = p.getHandClass().getHandPoints();
                name = p.getUsername();
            }
            System.out.println(p.getUsername() + " - " + p.getHandClass().getHandPoints());
        }
        String string = "Winner: " + name + " con un punteggio di " + max;
        // TODO necessaria interfaccia che riporta tutti i giocatori, con i punteggi e le proprie carte

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                winnerMex(string);
            }
        });
    }

    public void winnerMex(String string) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner");
        alert.setHeaderText(null);
        alert.setContentText(string);
        Optional<ButtonType> resultAlert = alert.showAndWait();
        if (!resultAlert.isPresent() || resultAlert.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    public boolean isSaidBusso() {
        return saidBusso;
    }

    public int getIdBusso() {
        return idBusso;
    }

    public GameController getGameController() {
        return gameController;
    }
}
