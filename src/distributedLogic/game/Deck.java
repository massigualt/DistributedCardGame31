package distributedLogic.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Deck implements Serializable {
    private LinkedList<Card> pile;

    public Deck() {
        pile = new LinkedList<>();
    }

    public void putCardOnTop(Card card) { this.pile.add(card); // add in coda
    }

    public void putCardOnBack(Card card) { this.pile.addFirst(card); }

    public void shuffle() { Collections.shuffle(pile, new Random()); }

    public Card dealCardOnTop() { return this.pile.removeLast(); }

    public Card getFirstElement() { return this.pile.peekLast(); }

    public void cleanDeck() { this.pile.clear(); }

    public LinkedList<Card> getPile() { return this.pile; }

    public void setPile(LinkedList<Card> pile) {
        this.pile = pile;
    }

    public int getDeckSize() { return this.pile.size(); }
}
