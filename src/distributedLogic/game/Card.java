package distributedLogic.game;

public class Card {

    public enum Suit {

        CUORI("cuori"), QUADRI("quadri"), FIORI("fiori"), PICCHE("picche");

        private String suitName;
        private Suit(String suitName) {
            this.suitName = suitName;
        }

        public String toString() {
            return suitName;
        }
    }

    public enum Rank{

        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(10), QUEEN(10), KING(10), ACE(11);

        private int value;
        private Rank(int value) { this.value = value; }

        public String toString(){ return String.valueOf(value); }
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank){
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString() +" di " + suit.toString();
    }
}
