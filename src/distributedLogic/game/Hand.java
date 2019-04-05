package distributedLogic.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Hand implements Serializable, Iterable<Card> {

    private final List<Card> hand;

    public Hand() {
        hand = new ArrayList<>(3);
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
            System.out.println(card.toString());
        }

    }

    public int getHandPoints() {
        int sumCuori = 0, sumFiori = 0, sumPicche = 0, sumQuadri = 0;
        int numCuori = 0, numFiori = 0, numPicche = 0, numQuadri = 0;
        int value, minValue = 12;

        for (Card card : hand) {
            value = card.getRankValue();
            switch (card.getSeme().toString()) {
                case "cuori":
                    numCuori += 1;
                    sumCuori += value;
                    break;
                case "fiori":
                    numFiori += 1;
                    sumFiori += value;
                    break;
                case "picche":
                    numPicche += 1;
                    sumPicche += value;
                    break;
                case "quadri":
                    numQuadri += 1;
                    sumQuadri += value;
                    break;
            }
            if (minValue > value)
                minValue = value;
        }


        int sum1 = Math.max(sumCuori, sumFiori);
        int sum2 = Math.max(sumPicche, sumQuadri);
        int sum = Math.max(sum1, sum2);

        if (numCuori == 4 || numFiori == 4 || numPicche == 4 || numQuadri == 4) {
            sum = sum - minValue;
        }

        return sum;
    }

    public void orderCard() {
        Collections.sort(this.hand, Card.CardComparatorSeme);
    }

    @Override
    public String toString() {
        String str = "";
        for (Card card : hand) {
            str.concat(card.toString() + "\t");
        }
        return str;
    }

    @Override
    public Iterator<Card> iterator() {
        return hand.iterator();
    }

}
