package distributedLogic.game;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {

    private boolean coveredPick; // Pesca la carta dal mazzo coperto
    private int discardedCard;
    private String status; // Pesca - Scarta - Busso
    private int playerMove;
    private boolean busso;


    public Move(String status) {
        this.coveredPick = false;
        this.discardedCard = -1;
        this.status = status;
        this.playerMove = -1;
        this.busso = false;
    }


    public Move(boolean coveredPick, int discardedCard, String status, int playerMove, boolean busso) {
        this.coveredPick = coveredPick;
        this.discardedCard = discardedCard;
        this.status = status;
        this.playerMove = playerMove;
        this.busso = busso;
    }

    public int getDiscardedCard() {
        return discardedCard;
    }

    public void setDiscardedCard(int discardedCard) {
        this.discardedCard = discardedCard;
    }

    public boolean isBusso() {
        return busso;
    }

    public void setBusso(boolean busso) {
        this.busso = busso;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCoveredPick() {
        return coveredPick;
    }

    public void setCoveredPick(boolean coveredPick) {
        this.coveredPick = coveredPick;
    }

    public int getPlayerMove() {
        return playerMove;
    }
}


