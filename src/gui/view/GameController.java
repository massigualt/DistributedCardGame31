package gui.view;

import distributedLogic.Player;
import distributedLogic.game.Card;
import distributedLogic.game.Game;
import distributedLogic.game.Move;
import distributedLogic.game.PlayerLogic;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameController {
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 110;

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
    private ListView listViewPlayers;


    private Game game;
    private PlayerLogic playerLogic;

    private boolean coveredPick;
    private int discardCard;

    public void initializeInterface(Game game, PlayerLogic playerLogic) {
        this.game = game;
        this.playerLogic = playerLogic;

        this.userLabel.setText(this.game.getPlayers()[game.getMyId()].getUsername());
        this.statusLabel.setText("");

        this.coveredDeckG = createCoveredDeckGui();
        this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false, false);
        this.tableDecksHB.setSpacing(10);
        this.tableDecksHB.getChildren().addAll(this.coveredDeckG, this.uncoveredCardG);

        this.cardsPlayerHB.setSpacing(10);
        updateCardsPlayerHB();

        updateListViewPlayers();
        this.listViewPlayers.setMouseTransparent(true);
        this.listViewPlayers.setFocusTraversable(false);

        lockUnlockElementTable(0);
    }

    private synchronized void pickCard(String deck) {
        Card cardToAdd;
        if (deck.equals("uncovered")) {
            cardToAdd = this.game.pickFromUncoveredDeck(this.game.getCurrentPlayer());
            updateUncoveredDeck("pick");
            this.setCoveredPick(false);
        } else { // covered
            cardToAdd = this.game.pickFromCoveredDeck(this.game.getCurrentPlayer());
            setTextNumberCoveredDeck(this.game.getCoveredDeck().getDeckSize());
            this.setCoveredPick(true);
        }

        this.cardsPlayerHB.getChildren().add(createUncoveredCardGui(cardToAdd, true, true));
        this.handPoints.setText(String.valueOf(this.game.getPlayers()[this.game.getMyId()].getHandScore()));

        message("pick");
    }

    private synchronized void discardCard(int position) {
        this.game.discardCard(position, this.game.getCurrentPlayer());
        this.setDiscardCard(position);
        updateUncoveredDeck("discard");
        updateCardsPlayerHB();

        message("discard");
    }

    @FXML
    private synchronized void busso() {
        this.game.saidBusso(this.game.getCurrentPlayer());
        message("busso");
    }

    private synchronized void message(String operation) {
        boolean busso = false;

        if (operation.matches("discard|busso")) {
            lockUnlockElementTable(0);
            if (operation.equals("busso"))
                busso = true;
        } else {
            lockUnlockElementTable(2);
        }
        Move myMove = new Move(this.coveredPick, this.discardCard, operation, this.game.getCurrentPlayer(), busso);

        this.playerLogic.notifyMove(myMove);
    }


    /**
     * metodo che blocca o sblocca il tavolo in base all'iterOperation
     *
     * @param iterOperation
     */
    public void lockUnlockElementTable(int iterOperation) {
        Platform.runLater(
                () -> {
                    switch (iterOperation) {
                        case 1:
                            disableTableDecks(false); // attivo
                            disableCardsPlayer(true); // spento
                            if (this.game.isSaidBusso() || this.game.getPlayers()[this.game.getCurrentPlayer()].getNumberMoves() < 3) {
                                this.statusLabel.setText("1: Pesca");
                                disableButtonBusso(true);
                            } else {
                                this.statusLabel.setText("1: Pesca o Bussa");
                                disableButtonBusso(false);
                            }
                            break;
                        case 2:
                            this.statusLabel.setText("2: Scarta");
                            disableTableDecks(true);
                            disableCardsPlayer(false);
                            disableButtonBusso(true);
                            break;
                        case 0:
                            this.discardCard = -1;
                            this.statusLabel.setText("");
                            disableTableDecks(true);
                            disableCardsPlayer(true);
                            disableButtonBusso(true);
                    }
                }
        );
    }


    private void updateCardsPlayerHB() {
        this.cardsPlayerHB.getChildren().clear();
        this.game.getMyHand().orderCard();

        //TODO remove print
        //print();

        for (Card card : this.game.getMyHand().getHand()) {
            this.cardsPlayerHB.getChildren().add(createUncoveredCardGui(card, true, false));
        }
        this.handPoints.setText(String.valueOf(this.game.getPlayers()[this.game.getMyId()].getHandScore()));
    }

    private void updateUncoveredDeck(String operation) {
        if (operation.matches("discard|crash") || this.game.getUncoveredDeck().getDeckSize() > 0) {
            this.uncoveredCardG = createUncoveredCardGui(this.game.getUncoveredDeck().getFirstElement(), false, false);
            this.tableDecksHB.getChildren().set(1, this.uncoveredCardG);
        } else { // -> pick
            this.tableDecksHB.getChildren().set(1, createEmptyUncoveredDeck());
        }
        // un altro else -> busso
    }

    private void print() {
        System.out.println("\n\n -----------------------------------------------------------------------------");
        // System.out.println("COVERED DECK: " + this.game.getCoveredDeck().getPile().toString());
        // System.out.println("UNCOVERED DECK: " + this.game.getUncoveredDeck().getPile().toString());
        System.out.println("HAND: " + this.game.getMyHand().getHand().toString());
        System.out.println("Covered deck Last: [" + this.game.getCoveredDeck().getPile().peekLast().toString() + "]");
        System.out.println("Uncovered deck Last: [" + this.game.getUncoveredDeck().getPile().peekLast().toString() + "]");
        System.out.println(" ----------------------------------------------------------------------------- \n\n");
    }

    public void updateListViewPlayers() {
        updateListView(this.game.getPlayers(), this.game.getIdBusso());
    }

    public void updateListDuringMove(int idNode) {
        Player[] players = this.game.getPlayers().clone();

        // Gestico nodo crash [Locale]
        players[idNode].setNodeCrashed();

        int idBusso = this.game.getIdBusso();
        if (this.game.isSaidBusso() && this.game.getIdBusso() == idNode) {
            idBusso = this.game.nextPlayerActive(idNode);
            players[idBusso].sayBusso();
        }

        // GESTISCO MAZZO UNCOVERED GRAFICA
        if (this.game.getUncoveredDeck().getDeckSize() == 0)
            Platform.runLater(() -> {
                Card cardForUncovered = players[idNode].getHandClass().getHand().getLast();
                this.tableDecksHB.getChildren().set(1, createUncoveredCardGui(cardForUncovered, false, false));
            });

        updateListView(players, idBusso);
    }

    private void updateListView(Player[] players, int idBusso) {
        ObservableList<Group> observableListPlayers = FXCollections.observableArrayList();
        boolean currentPlayer, saidBusso, isActive;

        for (Player p : players) {
            if (p.isActive()) {
                if (p.getId() == this.game.getCurrentPlayer()) {
                    currentPlayer = true;
                } else {
                    currentPlayer = false;
                }

                if (p.getId() == idBusso) {
                    saidBusso = true;
                } else {
                    saidBusso = false;
                }
                isActive = true;
            } else {
                isActive = false;
                currentPlayer = false;
                saidBusso = false;
            }
            observableListPlayers.add(createRectangleListView(p.getUsername(), p.getInetAddress().getHostAddress(), currentPlayer, saidBusso, isActive));
        }

        Platform.runLater(() -> {
            listViewPlayers.getItems().clear();
            listViewPlayers.setItems(observableListPlayers);
        });
    }

    public void updateTableCardAfterRemoteMove(String operation) {
        Platform.runLater(() -> {
            setTextNumberCoveredDeck(game.getCoveredDeck().getDeckSize());
            updateUncoveredDeck(operation);
        });
    }

    private Group createRectangleListView(String name, String address, boolean currentPlayer, boolean saidBusso, boolean isActive) {
        Rectangle r1 = new Rectangle(65, 92);
        r1.setArcWidth(10);
        r1.setArcHeight(10);
        r1.setStrokeWidth(1.5);
        r1.setFill(Color.web("ffffff"));
        r1.setStroke(Color.web("2d2d2d"));


        Text text = new Text(name);
        text.setFont(Font.font(10));
        text.setX((65 - text.getLayoutBounds().getWidth()) / 2);
        text.setY(20);
        text.setFill(Color.web("2d2d2d"));
        text.setTextAlignment(TextAlignment.CENTER);

        Text text2 = new Text(address);
        text2.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 8));
        text2.setX((65 - text2.getLayoutBounds().getWidth()) / 2);
        text2.setY(60);
        text2.setFill(Color.web("2d2d2d"));
        text2.setTextAlignment(TextAlignment.CENTER);

        Circle circle = new Circle(32.5, 75, 5);
        circle.setFill(Color.web("fefefe"));
        circle.setStroke(Color.web("f1f1f1"));

        if (isActive) {
            if (currentPlayer && saidBusso) {
                circle.setFill(Color.web("3fff00"));
                circle.setStroke(Color.web("003fff"));
                r1.setStroke(Color.web("ff6b00"));
                text.setFill(Color.web("ff6b00"));
            } else if (currentPlayer) {
                circle.setFill(Color.web("3fff00"));
                circle.setStroke(Color.web("003fff"));
                r1.setStroke(Color.web("003fff"));
            } else if (saidBusso) {
                circle.setFill(Color.web("ffc000"));
                circle.setStroke(Color.web("ff6b00"));
                r1.setStroke(Color.web("ff6b00"));
                text.setFill(Color.web("ff6b00"));
            }
        } else {
            circle.setFill(Color.web("f20000"));
            circle.setStroke(Color.web("a50000"));

            text.setFill(Color.web("a50000"));
            text.setText(name + "\nOut");

            r1.setStroke(Color.web("a50000"));
        }


        return new Group(r1, text, text2, circle);
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

        Group group = new Group(cardRectangle, image1, oppositeImage, text1, text2);
        group.setOnMouseClicked(event -> {
            if (playerCard) {
                int positionHB = this.cardsPlayerHB.getChildren().indexOf(group);
                discardCard(positionHB);
            } else {
                pickCard("uncovered");
            }
        });
        return group;
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
        cardRectangle.setFill(Color.web("13720e"));
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
            cardRectangle.setStroke(Color.BLUE);

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

    private void disableButtonBusso(boolean disable) {
        this.busso.setDisable(disable);
    }

    public boolean isCoveredPick() {
        return coveredPick;
    }

    public Label getUserLabel() {
        return userLabel;
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
}
