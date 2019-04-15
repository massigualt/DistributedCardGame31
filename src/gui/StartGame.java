package gui;

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
        Scene scene = new Scene(root,600,400);
        scene.getStylesheets().add(getClass().getResource("view/fxml/style.css").toExternalForm());

        primaryStage.setTitle("Distributed 31");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("view/img/31.png")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();

        primaryStage.setOnCloseRequest(windowsEvent -> {
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
