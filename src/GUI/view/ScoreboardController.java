package GUI.view;

import distributedLogic.game.Game;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardController {

    @FXML
    private VBox highScore;

    public void winnerScoreBoard(Game game) {

        this.highScore.setSpacing(10);
        this.highScore.getChildren().clear();

        Platform.runLater(
                () -> {

                    HashMap<String, Integer> tmpScoreboard = game.getPlayersScoreBoard();
                    int i = 1;
                    for (Map.Entry<String, Integer> pair : tmpScoreboard.entrySet()) {
                        String number = String.valueOf(i) + ": ";
                        String name = pair.getKey();
                        String scorevalue = String.valueOf(pair.getValue());
                        this.highScore.getChildren().add(createRectangle(number, name, scorevalue));
                        i++;

                    }
                });
    }

    private Group createRectangle(String number, String name, String point) {
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

        Text text2 = new Text(point);
        text2.setX(250 - text2.getLayoutBounds().getWidth() - 20);
        text2.setY(20);
        text2.setStyle("-fx-font-size: 18px;");
        text2.setStyle("-fx-font-weight: bold");
        text2.setFill(Color.web("e04c2e"));

        return new Group(rectangle, text, text1, text2);
    }
}
