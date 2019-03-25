package distributedLogic.game;

import distributedLogic.Player;
import distributedLogic.net.Link;

public class Game {

    private Deck openDeck;
    private Deck coveredDeck;
    private Hand hand;
    private Player[] players;
    private int currentPlayer = 0;
    private int myId;
    private Move currentMove = null, myMove;
    // TODO other variables
    private boolean saidBusso = false;
    private boolean gameOver;

    public Game(Card uncoveredCard, Deck covered, Hand hand, Player[] players, int myId) {
        this.openDeck = new Deck();
        this.openDeck.putCardOnTop(uncoveredCard);
        this.coveredDeck = covered;
        this.hand = hand;
        this.players = players;
        this.myId = myId;
        this.gameOver = false;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Move myTurn() {

        myMove = new Move();

        // Il giocatore pesca e scarta la carta, e puoi bussare
        // TODO logica turno

        currentPlayer = nextPlayer(currentPlayer);

        if (myMove.isBusso()) {
            players[myId].saidBusso();
        }
        return myMove;
    }

    private int nextPlayer(int currentPlayer) {
        int nextPlayer = -1;
        int current = (currentPlayer + 1) % players.length;
        // TODO
        do {
            nextPlayer = current;
            current = (nextPlayer + 1) % players.length;
        } while (!players[nextPlayer].isActive());
        System.out.println("GAME: new current player is " + players[nextPlayer].getUsername());
        return nextPlayer;
    }
}
