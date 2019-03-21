package distributedLogic.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Deck implements Serializable, Iterable<Card>{
    private LinkedList<Card> pile;
    private Random random;

    public Deck(Random random) {
        if (random == null)
            random = new Random();
        this.random = random;
        pile = new LinkedList<Card>();
    }

    public Deck() {
        this(null);
    }


    public void putCardOnTop(Card card) {
        pile.add(card);
    }

    public void shuffle() { Collections.shuffle(pile, random); }

    public Card dealCardOnTop() { return pile.removeLast(); }

    public LinkedList<Card> getPile() { return pile; }

    @Override
    public Iterator<Card> iterator() {
        return pile.iterator();
    }


}
