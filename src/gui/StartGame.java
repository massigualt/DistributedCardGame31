package gui;

import gui.view.LoginController;
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
        Scene scene = new Scene(root);
        scene.getStylesheets().add(LoginController.class.getResource("fxml/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(StartGame.class.getResourceAsStream("view/img/31.png")));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(windowsEvent -> {
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
