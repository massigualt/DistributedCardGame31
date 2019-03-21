package distributedLogic.game;

import java.io.Serializable;

public class Card implements Serializable {

    public enum Seme {

        CUORI("cuori"), QUADRI("quadri"), FIORI("fiori"), PICCHE("picche");

        private String semeString;
        private Seme(String semeString) {

            this.semeString = semeString;
        }

        public String toString() {
            return semeString;
        }
    }

    public enum Rank{

        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(10), QUEEN(10), KING(10), ACE(11);

        private int value;
        private Rank(int value) { this.value = value; }

        public String toString(){ return String.valueOf(value); }
    }

    private Seme seme;
    private Rank rank;

    public Card(Seme seme, Rank rank){
        this.seme = seme;
        this.rank = rank;
    }

    public Seme getSeme() {
        return seme;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.name() +" di " + seme.toString();
    }
}
