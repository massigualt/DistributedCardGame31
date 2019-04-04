package distributedLogic.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hand implements Serializable, Iterable<Card> {

    private List<Card> hand;

    public Hand() {
        hand = new ArrayList<Card>();
    }


    public void takeCard(Card card) {
        hand.add(card);
    }


    public Card getCard(int index) {
        if (index >= 0 && index <= hand.size()) {
            return hand.get(index);
        }
        return null;
    }

    public Card removeCard(Card card) {
        int index = hand.indexOf(card);
        if (index < 0)
            return null;
        return hand.remove(index);
    }

    public int getNumberOfCards() {
        return hand.size();
    }

    public void printHand() {
        for (Card card : hand) {
            System.out.println(card.toString() + "\n");
        }

    }

    public int handValue() {
        int sum = 0;
        for (Card card : hand) {
            sum += card.getRankValue();
        }
        return sum;
    }

    @Override
    public Iterator<Card> iterator() {
        return hand.iterator();
    }

}
