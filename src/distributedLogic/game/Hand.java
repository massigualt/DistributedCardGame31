package distributedLogic.game;

import java.io.Serializable;
import java.util.*;

public class Hand implements Serializable {

    private final LinkedList<Card> hand;

    public Hand() { this.hand = new LinkedList<>(); }


    public void takeCard(Card card) { this.hand.add(card); }


    public Card getCard(int index) {
        return hand.get(index);
    }

    public Card removeCard(Card card) {
        int index = this.hand.indexOf(card);
        return this.hand.remove(index);
    }

    public int getHandPoints() {
        int sumCuori = 0, sumFiori = 0, sumPicche = 0, sumQuadri = 0;
        int numCuori = 0, numFiori = 0, numPicche = 0, numQuadri = 0;
        int value, minValue = 12;

        for (Card card : this.hand) {
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
        Collections.sort(this.hand, Card.CardComparator);
    }

    public LinkedList<Card> getHand() { return this.hand; }

    @Override
    public String toString() {
        String s = "";
        for (Card card : hand) {
            s = s + card.toString() + "\t";
        }
        return s;
    }
}
