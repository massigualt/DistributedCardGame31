package distributedLogic.game;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {

    private int selectedCard;
    private Card discardedCard;
    private boolean busso;
    private String status;


    public Move() {
        this.selectedCard = 0;
        this.discardedCard = null;
        this.busso = false;
        this.status = "";
    }


    /**
     * Creates a canonic player's move.
     *
     * @param discardedCard
     * @param busso
     */
    public Move(Card discardedCard, boolean busso) {
        this.discardedCard = discardedCard;
        this.busso = busso;
    }

    public int getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(int selectedCard) {
        this.selectedCard = selectedCard;
    }

    public Card getDiscardedCard() {
        return discardedCard;
    }

    public void setDiscardedCard(Card discardedCard) {
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
}


