package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartGame extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("fxml/ScreenLogin.fxml"));
        primaryStage.setTitle("Distributed 31");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        //TODO icons
        //primaryStage.getIcons();
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
