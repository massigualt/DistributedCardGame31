package distributedLogic.game;

import java.io.Serializable;

public class Move implements Cloneable, Serializable {

    private int selectedCard=0;
    private Card discardedCard = null;
    private boolean busso;


    public Move() {}


    /**
     * Creates a canonic player's move.
     * @param discardedCard
     * @param busso
     */
    public Move(Card discardedCard, boolean busso) {
        this.discardedCard = discardedCard;
        this.busso = busso;
    }

    public int getSelectedCard() { return selectedCard; }

    public void setSelectedCard(int selectedCard) { this.selectedCard = selectedCard; }

    public Card getDiscardedCard() { return discardedCard; }

    public void setDiscardedCard(Card discardedCard) { this.discardedCard = discardedCard; }

    public boolean isBusso() { return busso; }

    public void setBusso(boolean busso) { this.busso = busso; }

    //TODO clone??
}


