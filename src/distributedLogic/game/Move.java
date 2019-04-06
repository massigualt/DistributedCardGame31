package distributedLogic.game;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {

    private int selectedCard;
    private boolean busso;

    private boolean coveredPick; // Pesca la carta dal mazzo coperto
    private String status; // Pesca - Scarta - Busso
    private int discardedCard;


    public Move() {
        this.coveredPick = false;
        this.discardedCard = -1;
        this.status = "";
        this.busso = false;
    }


    public Move(boolean coveredPick, int discardedCard, String status, boolean busso) {
        this.coveredPick = coveredPick;
        this.discardedCard = discardedCard;
        this.status = status;
        this.busso = busso;
    }


    public int getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(int selectedCard) {
        this.selectedCard = selectedCard;
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

    //TODO clone??


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
}


