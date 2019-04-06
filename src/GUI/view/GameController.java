package GUI.view;

import distributedLogic.game.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
    private Button busso;
    @FXML
    private Label userLabel;
    @FXML
    private Label handPoints;
    @FXML
    private Label statusLabel;
    @FXML
    private HBox cardsPlayerHB;
    @FXML
    private HBox tableDecksHB;
    @FXML
    private Node coveredDeckG, uncoveredCardG;
    @FXML
    private ListView partecipantList;
    ObservableList<String> userList = FXCollections.observableArrayList();

    private Game game;
    private ClientLogic clientLogic;

    private boolean coveredPick;
    private int discardCard;


    public void initializeInterface(Game game, ClientLogic clientLogic) {
        this.game = game;
        this.clientLogic = clientLogic;

        this.userLabel.setText(this.game.getPlayers()[game.getMyId()].getUsername());
        this.statusLabel.setText("");

        this.coveredDeckG = createCoveredDeckGui();
        this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false, false);
        this.tableDecksHB.setSpacing(10);
        this.tableDecksHB.getChildren().addAll(this.coveredDeckG, this.uncoveredCardG);

        this.cardsPlayerHB.setSpacing(10);
        updateCardsPlayerHB();

        for (int i = 0; i < this.game.getPlayers().length; i++) {
            this.userList.add(this.game.getPlayers()[i].getUsername());
        }

        this.partecipantList.setItems(this.userList);
        this.handPoints.setText(String.valueOf(this.game.getMyHand().getHandPoints()));

        lockUnlockElementTable(0);
    }

    private void pickCard(String id) {
        Card cardToAdd;
        if (id.equals("uncovered")) {
            cardToAdd = this.game.pickFromUncoveredDeck(this.game.getMyId());
            updateUncoveredDeck("pick");
            this.setCoveredPick(false);
        } else { // covered
            cardToAdd = this.game.pickFromCoveredDeck(this.game.getMyId());
            setTextNumberCoveredDeck(this.game.getCoveredDeck().getDeckSize());
            this.setCoveredPick(true);
        }

        this.cardsPlayerHB.getChildren().add(createUncoveredCardGui(cardToAdd, true, true));
        this.handPoints.setText(String.valueOf(this.game.getMyHand().getHandPoints()));
        lockUnlockElementTable(2);

        // print();
    }

    private void discardCard(int position) {
        this.game.discardCard(position, this.game.getMyId());
        this.setDiscardCard(position);
        updateCardsPlayerHB();
        updateUncoveredDeck("discard");

        this.handPoints.setText(String.valueOf(this.game.getMyHand().getHandPoints()));
        lockUnlockElementTable(3);

       // print();
        message("discard");
    }

    @FXML
    private void message(String operation) {
        this.statusLabel.setText("");
        this.clientLogic.notifyMove(new Move(this.coveredPick, this.discardCard, operation + this.userLabel.getText(), this.game.getCurrentPlayer(), false));
        lockUnlockElementTable(0);
    }

    private Node createUncoveredCardGui(Card carta, boolean playerCard, boolean isPicked) {
        Rectangle cardRectangle = createRectangle(isPicked);
        String cardText = carta.getRank().name();

        if (!cardText.matches("J|Q|K|A")) {
            cardText = String.valueOf(carta.getRankValue());
        }

        Text text1 = new Text(cardText);
        text1.setStyle("-fx-font-weight: bold");
        text1.setFont(Font.font(13));
        text1.setX(CARD_WIDTH - text1.getLayoutBounds().getWidth() - 8);
        text1.setY(text1.getLayoutBounds().getHeight());

        Text text2 = new Text(text1.getText());
        text2.setStyle("-fx-font-weight: bold");
        text2.setFont(Font.font(13));
        text2.setX(8);
        text2.setY(CARD_HEIGHT - 10);

        String seedPath = "img/" + carta.getSeme().toString() + ".png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), 23, 23, true, true);
        ImageView image1 = new ImageView(image);
        image1.setX(2);
        image1.setY(2);

        ImageView oppositeImage = new ImageView(image);
        oppositeImage.setRotate(180);
        oppositeImage.setX(CARD_WIDTH - 25);
        oppositeImage.setY(CARD_HEIGHT - 25);

        Group g = new Group(cardRectangle, image1, oppositeImage, text1, text2);
        g.setOnMouseClicked(event -> {
            if (playerCard) {
                int positionHB = this.cardsPlayerHB.getChildren().indexOf(g);
                discardCard(positionHB);
            } else {
                pickCard("uncovered");
            }
        });
        return g;
    }

    private Node createCoveredDeckGui() {
        Rectangle cardRectangle = createRectangle(false);

        Text text1 = new Text(Integer.toString(this.game.getCoveredDeck().getDeckSize()));
        text1.setStyle("-fx-font-size: 12px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setX(CARD_WIDTH - text1.getLayoutBounds().getWidth() - 35);
        text1.setY(CARD_HEIGHT - text1.getLayoutBounds().getHeight() - 35);

        String seedPath = "img/coveredCard.png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), CARD_WIDTH, CARD_HEIGHT + 5, true, true);

        Group g = new Group(cardRectangle, new ImageView(image), text1);
        g.setOnMouseClicked(event -> {
            pickCard("covered");
        });
        return g;
    }

    private Node createEmptyUncoveredDeck() {
        Rectangle cardRectangle = createRectangle(false);
        cardRectangle.setFill(Color.web("59882b"));
        return cardRectangle;
    }

    private void setTextNumberCoveredDeck(int size) {
        Group card = (Group) tableDecksHB.getChildren().get(0);
        Text t = (Text) card.getChildren().get(2);
        t.setText(String.valueOf(size));
    }

    private Rectangle createRectangle(boolean isPicked) {
        Rectangle cardRectangle = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        cardRectangle.setArcWidth(10);
        cardRectangle.setArcHeight(10);
        cardRectangle.setFill(Color.WHITE);
        if (isPicked)
            cardRectangle.setStroke(Color.GREEN);

        return cardRectangle;
    }

    private void disableTableDecks(boolean disable) {
        for (Node card : this.tableDecksHB.getChildren()) {
            card.setDisable(disable); // disabilita le carte centrali
        }
    }

    private void disableCardsPlayer(boolean disable) {
        for (Node card : this.cardsPlayerHB.getChildren()) {
            card.setDisable(disable); // disabilita le carte del player
        }
    }

    private void disableButton(boolean disable) {
        this.busso.setDisable(disable);
    }

    /*---- metodo che blocca o sblocca il tavolo in base all'iterOperation ----*/
    public void lockUnlockElementTable(int iterOperation) {
        Platform.runLater(
                () -> {
                    switch (iterOperation) {
                        case 1:
                            this.statusLabel.setText("Fase 1: Pesca");
                            disableTableDecks(false); // attivo
                            disableCardsPlayer(true); // spento
                            disableButton(true);
                            break;
                        case 2:
                            this.statusLabel.setText("Fase 2: Scarta");
                            disableTableDecks(true);
                            disableCardsPlayer(false);
                            disableButton(true);
                            break;
                        case 3:
                            this.statusLabel.setText("Fase 3: Passa");
                            disableTableDecks(true);
                            disableCardsPlayer(true);
                            disableButton(false);
                            break;
                        default:
                            this.statusLabel.setText("");
                            disableTableDecks(true);
                            disableCardsPlayer(true);
                            disableButton(true);
                    }
                }
        );
    }

    private void updateCardsPlayerHB() {
        this.cardsPlayerHB.getChildren().clear();
        this.game.getMyHand().orderCard();

        for (Card card : this.game.getMyHand().getHand()) {
            this.cardsPlayerHB.getChildren().add(createUncoveredCardGui(card, true, false));
        }
    }

    private void updateUncoveredDeck(String operation) {
        if (operation.equals("discard") || this.game.getUncoveredDeck().getDeckSize() > 0) {
            setFirstCardOnUncoveredDeck();
        } else {
            this.tableDecksHB.getChildren().set(1, createEmptyUncoveredDeck());
        }
    }

    private void setFirstCardOnUncoveredDeck() {
        this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false, false);
        this.tableDecksHB.getChildren().set(1, this.uncoveredCardG);
    }

    private void print() {
        System.out.println("COVERED DECK: " + this.game.getCoveredDeck().getPile().toString());
        System.out.println("HAND: " + this.game.getMyHand().getHand().toString());
        System.out.println("UNCOVERED DECK: " + this.game.getUncoveredDeck().getPile().toString());
    }

    public boolean isCoveredPick() {
        return coveredPick;
    }

    public void setCoveredPick(boolean coveredPick) {
        this.coveredPick = coveredPick;
    }

    public int getDiscardCard() {
        return discardCard;
    }

    public void setDiscardCard(int discardCard) {
        this.discardCard = discardCard;
    }

    public void updateCurrentPlayerGUI(int index) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                partecipantList.scrollTo(index);
                partecipantList.getSelectionModel().select(index);
            }
        });
    }

    public void updateTableCardAfterRemoteMove() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setTextNumberCoveredDeck(game.getCoveredDeck().getDeckSize());
                setFirstCardOnUncoveredDeck();
            }
        });
    }
}
