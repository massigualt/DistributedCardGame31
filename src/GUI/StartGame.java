package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartGame extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("view/fxml/ScreenLogin.fxml"));
        primaryStage.setTitle("Distributed 31");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(StartGame.class.getResourceAsStream("img/31.png")));
        primaryStage.setOnCloseRequest(windowsEvent -> {
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
