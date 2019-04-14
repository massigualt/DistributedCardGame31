package gui.view;

import distributedLogic.Player;
import distributedLogic.game.Card;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class ScoreboardController {

    @FXML
    private VBox highScore;

    public void initializeScoreTable(Player[] players) {

        this.highScore.setSpacing(8);
        this.highScore.getChildren().clear();
        this.highScore.setAlignment(Pos.TOP_CENTER);

        Platform.runLater(
                () -> {
                    Arrays.sort(players, Player.playerComparator.reversed());
                    int number = 1;
                    for (Player p : players) {
                        this.highScore.getChildren().add(createBoxPlayer(p, number));
                        number++;
                    }

//                    this.highScore.getChildren().add(createStub("Emilio", 3));
//                    this.highScore.getChildren().add(createStub("Giorgio", 4));
//                    this.highScore.getChildren().add(createStub("Emilio", 5));
//                    this.highScore.getChildren().add(createStub("Giorgio", 6));
//                    this.highScore.getChildren().add(createStub("Massi", 7));
//                    this.highScore.getChildren().add(createStub("Carmine", 8));

                });
    }

    private Group createStub(String user, int number) {
        Rectangle rectangle = new Rectangle(280, 34);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setFill(Color.web("80c23f"));
        rectangle.setStroke(Color.web("4f7926"));

        Text text = new Text(number + ": ");
        text.setX(10);
        text.setY(21);
        text.setStyle("-fx-font-size: 20px;");
        text.setStyle("-fx-font-weight: bold");
        text.setFill(Color.BLACK);

        Text text1 = new Text(user);
        text1.setX(25);
        text1.setY(21);
        text1.setStyle("-fx-font-size: 25px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setFill(Color.WHITE);

        HBox hBox = new HBox();
        hBox.setLayoutX(150);
        hBox.setLayoutY(1);

        Text text2 = new Text("Player Out");
        text2.setX(280 - text2.getLayoutBounds().getWidth() - 20);
        text2.setY(21);
        text2.setStyle("-fx-font-size: 25px;");
        text2.setStyle("-fx-font-weight: bold");
        text2.setFill(Color.web("e04c2e"));

        return new Group(rectangle, text, hBox, text1, text2);
    }

    private Group createBoxPlayer(Player player, int number) {
        Rectangle rectangle = new Rectangle(280, 34);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        if (number == 1) {
            rectangle.setFill(Color.web("ffc200"));
            rectangle.setStroke(Color.web("ff9800"));
        } else {
            rectangle.setFill(Color.web("80c23f"));
            rectangle.setStroke(Color.web("4f7926"));
        }

        Text text = new Text(String.valueOf(number));
        text.setX(10);
        text.setY(21);
        text.setStyle("-fx-font-size: 20px;");
        text.setStyle("-fx-font-weight: bold");
        text.setFill(Color.BLACK);

        Text text1 = new Text(player.getUsername());
        text1.setX(25);
        text1.setY(21);
        text1.setStyle("-fx-font-size: 25px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setFill(Color.WHITE);

        HBox hBox = new HBox();
        if (player.isActive()) {
            hBox = createSmallHand(score(player.getHandClass().getHand(), player.getHandScore()));
        }
        hBox.setLayoutX(160);
        hBox.setLayoutY(1.5);

        Text text2 = new Text(String.valueOf(player.getHandScore()));
        if (!player.isActive())
            text2.setText("Player Out");

        text2.setX(280 - text2.getLayoutBounds().getWidth() - 20);
        text2.setY(21);
        text2.setStyle("-fx-font-size: 25px;");
        text2.setStyle("-fx-font-weight: bold");
        text2.setFill(Color.web("e04c2e"));

        return new Group(rectangle, text, hBox, text1, text2);
    }

    private HBox createSmallHand(LinkedList<Card> hand) {
        HBox hBox = new HBox();
        hBox.setSpacing(4);
        hBox.setMinWidth(68);
        hBox.setMaxWidth(68);
        hBox.setAlignment(Pos.CENTER_RIGHT);


        for (Card card : hand) {
            Node c = createSmallCard(card);
            hBox.getChildren().add(c);
        }

        return hBox;
    }

    private Node createSmallCard(Card carta) {
        Rectangle cardRectangle = new Rectangle(20, 30);
        cardRectangle.setArcWidth(5);
        cardRectangle.setArcHeight(5);
        cardRectangle.setFill(Color.WHITE);
        cardRectangle.setStroke(Color.LIGHTSLATEGRAY);

        String cardText = carta.getRank().name();
        if (!cardText.matches("J|Q|K|A")) {
            cardText = String.valueOf(carta.getRankValue());
        }
        Text text1 = new Text(cardText);
        text1.setFont(Font.font(7));
        text1.setX((20 - text1.getLayoutBounds().getWidth()) / 2);
        text1.setY(18);


        String seedPath = "img/" + carta.getSeme().toString() + ".png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), 9, 9, true, true);
        ImageView image1 = new ImageView(image);
        image1.setX(1);
        image1.setY(1);

        ImageView oppositeImage = new ImageView(image);
        oppositeImage.setRotate(180);
        oppositeImage.setX(20 - 10);
        oppositeImage.setY(30 - 10);

        return new Group(cardRectangle, image1, oppositeImage, text1);
    }

    private static Comparator<Card> CardComparatorToShow = new Comparator<Card>() {
        @Override
        public int compare(Card o1, Card o2) {
            return o1.getSeme().compareTo(o2.getSeme());
        }
    };

    private LinkedList<Card> score(LinkedList<Card> card, int score) {
        LinkedList<Card> cardLinkedList = new LinkedList<>();
        if (card.get(0).getRankValue() == score) {
            cardLinkedList.add(card.get(0));
            return cardLinkedList;
        }
        if (card.get(1).getRankValue() == score) {
            cardLinkedList.add(card.get(1));
            return cardLinkedList;
        }
        if (card.get(2).getRankValue() == score) {
            cardLinkedList.add(card.get(2));
            return cardLinkedList;
        }

        if ((card.get(0).getRankValue() + card.get(1).getRankValue()) == score) {
            if (CardComparatorToShow.compare(card.get(0), card.get(1)) == 0) {
                cardLinkedList.add(card.get(0));
                cardLinkedList.add(card.get(1));
                return cardLinkedList;
            }
        }
        if ((card.get(1).getRankValue() + card.get(2).getRankValue()) == score) {
            if (CardComparatorToShow.compare(card.get(1), card.get(2)) == 0) {
                cardLinkedList.add(card.get(1));
                cardLinkedList.add(card.get(2));
                return cardLinkedList;
            }
        }

        if ((card.get(0).getRankValue() + card.get(1).getRankValue() + card.get(2).getRankValue()) == score) {
            if (CardComparatorToShow.compare(card.get(0), card.get(1)) == 0 && CardComparatorToShow.compare(card.get(1), card.get(2)) == 0) {
                cardLinkedList.add(card.get(0));
                cardLinkedList.add(card.get(1));
                cardLinkedList.add(card.get(2));
                return cardLinkedList;
            }
        }

        return card;
    }
}

