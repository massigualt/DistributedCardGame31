package GUI.view;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameController {
    public static final int CARD_WIDTH = 80;
    public static final int CARD_HEIGHT = 110;

    @FXML
    private Label userLabel;
    @FXML
    private Label handPoints;
    @FXML
    private HBox cardsHB;
    @FXML
    private HBox tableCardHB;
    @FXML
    private Node coveredDeckG;
    @FXML
    private ListView partecipantList;
    ObservableList<String> userList = FXCollections.observableArrayList();

    private Hand hand;
    private Deck coveredDeck;
    private Card firstUncovered;
    private Player[] players;
    private int myId;
    private String playerName;


    public void initializeInterface(String user, Card uncoveredCard, Deck covered, Hand hand, Player[] players, int myId) {
        this.firstUncovered = uncoveredCard;
        this.coveredDeck = covered;
        this.hand = hand;
        this.players = players;
        this.myId = myId;

        this.userLabel.setText(user);

        this.coveredDeckG = createCoveredCard();
        this.tableCardHB.setSpacing(10);
        this.tableCardHB.getChildren().addAll(this.coveredDeckG, createCardGui(this.firstUncovered));

        this.cardsHB.setSpacing(10);
        this.cardsHB.getChildren().addAll(createCardGui(this.hand.getCard(0)), createCardGui(this.hand.getCard(1)), createCardGui(this.hand.getCard(2)));

        for (int i = 0; i < this.players.length; i++) {
            this.userList.add(players[i].getUsername());
        }

        this.partecipantList.setItems(this.userList);
        this.handPoints.setText(String.valueOf(this.hand.handValue()));
    }


    private Node createCardGui(Card carta) {
        Rectangle cardRectangle = setRectangle();

        Text text1 = new Text(carta.getRank().name());
        text1.setFont(Font.font(12));
        text1.setX(CARD_WIDTH - text1.getLayoutBounds().getWidth() - 8);
        text1.setY(text1.getLayoutBounds().getHeight());

        Text text2 = new Text(text1.getText());
        text2.setFont(Font.font(12));
        text2.setX(8);
        text2.setY(CARD_HEIGHT - 10);


        String seedPath = "img/" + carta.getSeme().toString() + ".png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), 23, 23, true, true);

        ImageView oppositeImage = new ImageView(image);
        oppositeImage.setRotate(180);
        oppositeImage.setX(CARD_WIDTH - 25);
        oppositeImage.setY(CARD_HEIGHT - 25);

        Group g = new Group(cardRectangle, new ImageView(image), oppositeImage, text1, text2);
        g.setOnMouseClicked(event -> {
            System.out.println(carta.toString());
        });

        return g;
    }

    private Node createCoveredCard() {
        Rectangle cardRectangle = setRectangle();
        Text text1 = new Text(Integer.toString(coveredDeck.getPile().size()));
        text1.setStyle("-fx-font-size: 12px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setX(CARD_WIDTH - text1.getLayoutBounds().getWidth() - 35);
        text1.setY(CARD_HEIGHT - text1.getLayoutBounds().getHeight() - 35);

        String seedPath = "img/coveredCard.png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), CARD_WIDTH, CARD_HEIGHT + 5, true, true);

        Group g = new Group(cardRectangle, new ImageView(image), text1);
        g.setOnMouseClicked(event -> {
            Card addCard = this.coveredDeck.getPile().removeLast();
            hand.takeCard(addCard);
            cardsHB.getChildren().add(createCardGui(addCard));
            text1.setText(Integer.toString(this.coveredDeck.getPile().size()));
            handPoints.setText(Integer.toString(hand.handValue()));
        });
        return g;
    }

    private Rectangle setRectangle() {
        Rectangle cardRectangle = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        cardRectangle.setArcWidth(20);
        cardRectangle.setArcHeight(20);
        cardRectangle.setFill(Color.WHITE);

        return cardRectangle;
    }

}
