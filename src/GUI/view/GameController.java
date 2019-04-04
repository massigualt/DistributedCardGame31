package GUI.view;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.ClientLogic;
import distributedLogic.game.Deck;
import distributedLogic.game.Hand;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameController {
    public static final int CARD_WIDTH = 80;
    public static final int CARD_HEIGHT = 110;

    @FXML
    private Button passo;
    @FXML
    private Button busso;
    @FXML
    private Label userLabel;
    @FXML
    private Label handPoints;
    @FXML
    private Label statusLabel;
    @FXML
    private HBox cardsHB;
    @FXML
    private HBox tableCardHB;
    @FXML
    private Node coveredDeckG, uncoveredCardG;
    private Node firstCard, secondCard, thirdCard;
    @FXML
    private ListView partecipantList;
    ObservableList<String> userList = FXCollections.observableArrayList();

    private Hand hand;
    private Deck coveredDeck;
    private Card firstUncovered;
    private Player[] players;
    private int myId;
    private String playerName;
    private ClientLogic clientLogic;
    private int status;


    public void initializeInterface(String user, Card uncoveredCard, Deck covered, Hand hand, Player[] players, int myId, ClientLogic clientLogic) {
        this.firstUncovered = uncoveredCard;
        this.coveredDeck = covered;
        this.hand = hand;
        this.players = players;
        this.myId = myId;
        this.clientLogic = clientLogic;

        this.userLabel.setText(user);
        this.statusLabel.setText("");
        this.status = 0;

        this.coveredDeckG = createCoveredCard();
        this.tableCardHB.setSpacing(10);
        this.uncoveredCardG = createCardGui(this.firstUncovered, false);
        this.uncoveredCardG.setOnMouseClicked(event -> {
            this.status = 2;
            updateStatus();
            pickCard(event);
        });
        this.tableCardHB.getChildren().addAll(this.coveredDeckG, this.uncoveredCardG);

        this.cardsHB.setSpacing(10);
        this.firstCard = createCardGui(this.hand.getCard(0), true);
        this.secondCard = createCardGui(this.hand.getCard(1), true);
        this.thirdCard = createCardGui(this.hand.getCard(2), true);
        this.cardsHB.getChildren().addAll(this.firstCard, this.secondCard, this.thirdCard);

        for (int i = 0; i < this.players.length; i++) {
            this.userList.add(players[i].getUsername());
        }

        this.partecipantList.setItems(this.userList);
        this.handPoints.setText(String.valueOf(this.hand.handValue()));

        disableBoard(true);
    }

    @FXML
    private void message() {
        clientLogic.notifyMove();
        disableBoard(true);
    }

    private Node createCardGui(Card carta, boolean player) {
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
        g.setId("fronte");
        g.setOnMouseClicked(event -> {
            if (player) {
                System.out.println(carta.toString());
                this.status = 3;
                updateStatus();
            }
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
        g.setId("retro");
        g.setOnMouseClicked(event -> {
            pickCard(event);
            text1.setText(Integer.toString(this.coveredDeck.getPile().size()));
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

    private void pickCard(MouseEvent event) {
        String id = ((Node) event.getSource()).getId();
        Card cardToAdd = null;
        if (id == "fronte") {
            cardToAdd = this.firstUncovered;
        } else if (id == "retro") {
            cardToAdd = this.coveredDeck.getPile().removeLast();
        }
        this.hand.takeCard(cardToAdd);
        this.cardsHB.getChildren().add(createCardGui(cardToAdd, true));
        this.handPoints.setText(Integer.toString(this.hand.handValue()));

        this.status = 2;
        updateStatus();
    }

    /*---- metodo che blocca o sblocca il tavolo ----*/

    public void disableBoard(boolean disable) {
        disableTableCard(disable);
        disableCardsPlayer(disable);
        disableButton(disable);

        if (!disable) {
            status = 1;
        } else {
            status = 0;
        }
    }

    private void disableTableCard(boolean disable) {
        for (Node card : this.tableCardHB.getChildren()) {
            card.setDisable(disable); // disabilita le carte centrali
        }
    }

    private void disableCardsPlayer(boolean disable) {
        for (Node card : this.cardsHB.getChildren()) {
            card.setDisable(disable); // disabilita le carte del player
        }
    }

    private void disableButton(boolean disable) {
        this.passo.setDisable(disable);
        this.busso.setDisable(disable);
    }

    public void updateStatus() {
        Platform.runLater(
                () -> {
                    switch (status) {
                        case 1:
                            this.statusLabel.setText("Fase 1: Pesca");
                            disableTableCard(false); // attivo
                            disableCardsPlayer(true); // spento
                            disableButton(true);
                            break;
                        case 2:
                            this.statusLabel.setText("Fase 2: Scarta");
                            disableTableCard(true);
                            disableCardsPlayer(false);
                            disableButton(true);
                            break;
                        case 3:
                            this.statusLabel.setText("Fase 3: Passa");
                            disableTableCard(true);
                            disableCardsPlayer(true);
                            disableButton(false);
                            break;
                    }

                }
        );
    }
}
