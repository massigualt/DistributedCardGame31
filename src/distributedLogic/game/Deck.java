package distributedLogic.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Deck implements Serializable, Iterable<Card> {
    private LinkedList<Card> pile;
    private Random random;

    public Deck() {
        this.random = new Random();
        pile = new LinkedList<>();
    }

    public void putCardOnTop(Card card) {
        this.pile.add(card);
    }

    public void shuffle() {

        Collections.shuffle(pile, random);
    }

    public Card dealCardOnTop() {

        return this.pile.removeLast();
    }

    public LinkedList<Card> getPile() {

        return this.pile;
    }

    @Override
    public Iterator<Card> iterator() {

        return this.pile.iterator();
    }


}
