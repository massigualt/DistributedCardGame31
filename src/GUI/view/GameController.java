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
    private Node coveredDeckG, uncoveredCardG, firstCardEmpty;
    @FXML
    private ListView partecipantList;
    ObservableList<String> userList = FXCollections.observableArrayList();

    private Game game;
    private ClientLogic clientLogic;
    private int status;

    private boolean coveredPick;
    private int discardCard;


    public void initializeInterface(Game game, ClientLogic clientLogic) {
        this.game = game;
        this.clientLogic = clientLogic;

        this.userLabel.setText(this.game.getPlayers()[game.getMyId()].getUsername());
        this.statusLabel.setText("");
        this.status = 0;

        this.coveredDeckG = createCoveredDeckGui();
        this.tableCardHB.setSpacing(10);
        this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false);
        this.firstCardEmpty = createEmptyUncoveredDeck();
        this.tableCardHB.getChildren().addAll(this.coveredDeckG, this.uncoveredCardG);

        this.cardsHB.setSpacing(10);
        updateCardHB();

        for (int i = 0; i < this.game.getPlayers().length; i++) {
            this.userList.add(this.game.getPlayers()[i].getUsername());
        }

        this.partecipantList.setItems(this.userList);
        this.handPoints.setText(String.valueOf(this.game.getHand(this.game.getMyId()).getHandPoints()));

        disableBoard(true);
    }

    private void pickCard(MouseEvent event) {
        String id = ((Node) event.getSource()).getId();
        Card cardToAdd = null;
        if (id.equals("uncovered")) {
            cardToAdd = this.game.pickFromUncoveredDeck(this.game.getMyId());
            updateUncoveredDeck("pick");
            this.setCoveredPick(false);
        } else if (id.equals("covered")) {
            cardToAdd = this.game.pickFromCoveredDeck(this.game.getMyId());
            this.setCoveredPick(true);
        }

        this.cardsHB.getChildren().add(createUncoveredCardGui(cardToAdd, true));
        this.handPoints.setText(String.valueOf(this.game.getHand(this.game.getMyId()).getHandPoints()));

        updateStatusBoard();
        print();
        this.game.updateMe();
        this.setStatus(2);
    }

    private void discardCard(int position) {
        this.game.discardCard(position,this.game.getMyId());
        this.setDiscardCard(position);
        updateCardHB();
        updateUncoveredDeck("discard");
        this.handPoints.setText(String.valueOf(this.game.getHand(this.game.getMyId()).getHandPoints()));
        updateStatusBoard();
        print();
        this.game.updateMe();
        message();
    }

    @FXML
    private void message() {
        this.statusLabel.setText("");
        this.clientLogic.notifyMove(new Move(this.coveredPick, this.discardCard, "passo " + this.userLabel.getText(), this.game.getCurrentPlayer(), false));
        disableBoard(true);
    }

    private Node createUncoveredCardGui(Card carta, boolean handCard) {
        Rectangle cardRectangle = CreateRectangle();
        String cardText = carta.getRank().name();

        if (!cardText.matches("J|Q|K|A")){
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
        g.setId("uncovered");
        g.setOnMouseClicked(event -> {
            if (handCard) {
                int positionHB = this.cardsHB.getChildren().indexOf(g);
                discardCard(positionHB);
            } else {
                pickCard(event);
            }
        });
        return g;
    }

    private Node createCoveredDeckGui() {
        Rectangle cardRectangle = CreateRectangle();

        Text text1 = new Text(Integer.toString(this.game.getCoveredDeck().getPile().size()));
        text1.setStyle("-fx-font-size: 12px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setX(CARD_WIDTH - text1.getLayoutBounds().getWidth() - 35);
        text1.setY(CARD_HEIGHT - text1.getLayoutBounds().getHeight() - 35);

        String seedPath = "img/coveredCard.png";
        Image image = new Image(getClass().getResourceAsStream(seedPath), CARD_WIDTH, CARD_HEIGHT + 5, true, true);

        Group g = new Group(cardRectangle, new ImageView(image), text1);
        g.setId("covered");
        g.setOnMouseClicked(event -> {
            pickCard(event);
            text1.setText(String.valueOf(this.game.getCoveredDeck().getPile().size()));
        });
        return g;
    }

    private Node createEmptyUncoveredDeck() {
        Rectangle cardRectangle = CreateRectangle();
        cardRectangle.setFill(Color.web( "59882b"));
        return cardRectangle;
    }

    private Rectangle CreateRectangle() {
        Rectangle cardRectangle = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        cardRectangle.setArcWidth(10);
        cardRectangle.setArcHeight(10);
        cardRectangle.setFill(Color.WHITE);

        return cardRectangle;
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

    public void updateCurrentPlayerGUI(int index) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                partecipantList.scrollTo(index);
                partecipantList.getSelectionModel().select(index);
            }
        });
    }

    public synchronized void updateTableCardHB() {
        Node card = (Group) this.tableCardHB.getChildren().get(0);
        Text t = (Text) ((Group) card).getChildren().get(2);
        t.setText(String.valueOf(this.game.getCoveredDeck().getPile().size()));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                uncoveredCardG = createUncoveredCardGui(game.getUncoveredDeck().getFirstElement(), false);
                tableCardHB.getChildren().set(1, uncoveredCardG);
            }
        });
    }

    public void updateStatusBoard() {
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

    private void updateCardHB() {
        this.cardsHB.getChildren().clear();
        this.game.getMe().getHand().orderCard();
        for (Card card : this.game.getMe().getHand()) {
            this.cardsHB.getChildren().add(createUncoveredCardGui(card, true));
        }
    }

    private void updateUncoveredDeck(String operation) {
        int size = this.game.getUncoveredDeck().getPile().size();
        if (size >= 1 || operation.equals("discard")) {
            this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false);
            this.tableCardHB.getChildren().set(1, this.uncoveredCardG);
        } else {
            this.tableCardHB.getChildren().set(1, this.firstCardEmpty);
        }
    }

    private void print() {
        System.out.println("COVERED DECK: " + this.game.getCoveredDeck().getPile().toString());
        System.out.println("HAND: " + this.game.getHand(this.game.getMyId()).toString());
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
