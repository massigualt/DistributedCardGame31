package gui.view;

import distributedLogic.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ScoreboardController {

    @FXML
    private VBox highScore;

    public void initializeScoreTable(Player[] playerOrdered) {

        this.highScore.setSpacing(10);
        this.highScore.getChildren().clear();

        Platform.runLater(
                () -> {
                    String score;
                    int number = 1;
                    for (Player p : playerOrdered) {
                        score = String.valueOf(p.getHandScore());
                        if (!p.isActive())
                            score = "Player Out";

                        this.highScore.getChildren().add(createRectangle(String.valueOf(number), p.getUsername(), score));
                        number++;
                    }
                });
    }

    private Group createRectangle(String number, String name, String score) {
        Rectangle rectangle = new Rectangle(250, 30);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setFill(Color.web("80c23f"));
        rectangle.setStroke(Color.web("4f7926"));

        Text text = new Text(number);
        text.setX(10);
        text.setY(20);
        text.setStyle("-fx-font-size: 18px;");
        text.setStyle("-fx-font-weight: bold");
        text.setFill(Color.WHITE);

        Text text1 = new Text(name);
        text1.setX(25);
        text1.setY(20);
        text1.setStyle("-fx-font-size: 18px;");
        text1.setStyle("-fx-font-weight: bold");
        text1.setFill(Color.WHITE);

        Text text2 = new Text(score);
        text2.setX(250 - text2.getLayoutBounds().getWidth() - 20);
        text2.setY(20);
        text2.setStyle("-fx-font-size: 18px;");
        text2.setStyle("-fx-font-weight: bold");
        text2.setFill(Color.web("e04c2e"));

        return new Group(rectangle, text, text1, text2);
    }
}
