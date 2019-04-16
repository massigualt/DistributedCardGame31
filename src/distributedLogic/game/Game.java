package distributedLogic.game;

import distributedLogic.Player;
import gui.view.GameController;
import gui.view.ScoreboardController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.LinkedList;

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


    public Game(Card uncoveredCard, Deck covered, Player[] players, int myId, GameController gameController, PlayerLogic playerLogic) {
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
        this.getGameController().initializeInterface(this, playerLogic);
    }


    public synchronized void updateMove(Move move) {
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
        this.currentPlayer = nextPlayerActive(this.currentPlayer);
        this.getGameController().updateListViewPlayers();
    }

    public void updateIdBusso() {
        this.idBusso = nextPlayerActive(this.idBusso);
        this.players[this.idBusso].sayBusso();
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

    public int nextPlayerActive(int currentPlayer) {
        int nextPlayer;
        int current = (currentPlayer + 1) % players.length;

        do {
            nextPlayer = current;
            current = (nextPlayer + 1) % players.length;
        } while (!players[nextPlayer].isActive());
        return nextPlayer;
    }

    public synchronized void putPlayerCardInUncoveredDeckAfterCrash(int idCrash) {
        LinkedList<Card> handPlayerCrashed = this.players[idCrash].getHandClass().getHand();
        int size = this.uncoveredDeck.getDeckSize();

        for (int i = handPlayerCrashed.size() - 1; i >= 0; i--) {
            this.uncoveredDeck.putCardOnBack(handPlayerCrashed.get(i));
        }
        this.players[idCrash].setHandScore(0);

        System.out.println("UNCOVERED DECK: " + this.uncoveredDeck.getPile().toString());
    }

    public void updateListPlayersGUI() {
        this.gameController.updateListViewPlayers();
    }

    public synchronized Card pickFromCoveredDeck(int id) {
        Card cartaPescata = this.coveredDeck.dealCardOnTop();
        this.players[id].getHandClass().takeCard(cartaPescata);
        this.players[id].setHandScore(this.players[id].getHandClass().getHandPoints());

        if (this.coveredDeck.getPile().size() == 0) {
            Card singolaCarta = this.uncoveredDeck.dealCardOnTop();
            this.coveredDeck.setPile((LinkedList<Card>) this.uncoveredDeck.getPile().clone());
            this.coveredDeck.shuffle();

            this.uncoveredDeck.cleanDeck();
            this.uncoveredDeck.putCardOnTop(singolaCarta);
        }

        return cartaPescata;
    }

    public synchronized Card pickFromUncoveredDeck(int id) {
        Card cartaPescata = this.uncoveredDeck.dealCardOnTop();
        this.players[id].getHandClass().takeCard(cartaPescata);
        this.players[id].setHandScore(this.players[id].getHandClass().getHandPoints());

        return cartaPescata;
    }

    public synchronized void discardCard(int position, int id) {
        Card cartaRimossa = this.players[id].getHandClass().removeCard(this.players[id].getHandClass().getCard(position));
        this.players[id].setHandScore(this.players[id].getHandClass().getHandPoints());

        this.uncoveredDeck.putCardOnTop(cartaRimossa);
    }

    public void saidBusso(int id) {
        this.players[id].sayBusso();
        this.idBusso = id;
        this.saidBusso = true;
    }

    public synchronized void declareWinner() {
        setConcluso();
        gameController.lockUnlockElementTable(0);

        Platform.runLater(() -> {
            changeScene();
        });
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


    private void changeScene() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(ScoreboardController.class.getResource("fxml/ScoreboardScreen.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage windows = (Stage) gameController.getUserLabel().getScene().getWindow();
            windows.setTitle("Distributed 31 - " + players[myId].getUsername());
            windows.setOnCloseRequest(windowsEvent -> {
                System.exit(0);
            });

            ScoreboardController scoreController = fxmlLoader.getController();
            scoreController.initializeScoreTable(players);

            windows.setScene(scene);
            windows.show();

        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }

    }

}
