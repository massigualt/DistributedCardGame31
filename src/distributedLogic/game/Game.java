package distributedLogic.game;

import GUI.view.GameController;
import GUI.view.ScoreboardController;
import distributedLogic.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

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
    private HashMap<String, Integer> playersScoreBoard = new HashMap<String, Integer>();

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

    private int nextPlayerActive(int currentPlayer) {
        int nextPlayer;
        int current = (currentPlayer + 1) % players.length;

        do {
            nextPlayer = current;
            current = (nextPlayer + 1) % players.length;
        } while (!players[nextPlayer].isActive());
        return nextPlayer;
    }

    public void putPlayerCardInUncoveredDeckAfterCrash(int idCrash) {
        LinkedList<Card> handPlayerCrashed = this.players[idCrash].getHandClass().getHand();

        for (int i = handPlayerCrashed.size() - 1; i >= 0; i--) {
            this.uncoveredDeck.putCardOnBack(handPlayerCrashed.get(i));
        }

        System.out.println("UNCOVERED DECK: " + this.uncoveredDeck.getPile().toString());
        this.gameController.updateTableCardAfterRemoteMove("discard");
    }

    public void updateListPlayersGUI() {
        this.gameController.updateListViewPlayers();
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
        this.players[id].sayBusso();
        this.idBusso = id;
        this.saidBusso = true;
    }

    public void declareWinner() {
        setConcluso();
        gameController.lockUnlockElementTable(0);
        for (Player p : players) {
            if (p.isActive()) {
                playersScoreBoard.put(p.getUsername(), p.getHandClass().getHandPoints());
            }
        }

        playersScoreBoard = sortByValue(playersScoreBoard);

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

    public HashMap<String, Integer> getPlayersScoreBoard() {
        return playersScoreBoard;
    }

    /**
     * method to sort hashmap by values
     * @param scoreboard
     * @return
     */
    private HashMap<String, Integer> sortByValue(HashMap<String, Integer> scoreboard) {
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        scoreboard.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        return reverseSortedMap;
    }

    private void changeScene() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(ScoreboardController.class.getResource("fxml/ScoreboardScreen.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage windows = (Stage) gameController.getUserLabel().getScene().getWindow();
            windows.setOnCloseRequest(windowsEvent -> {
                System.exit(0);
            });

            ScoreboardController scoreController = fxmlLoader.getController();

            scoreController.winnerScoreBoard(this);

            windows.setScene(scene);
            windows.show();


        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }

    }

}
