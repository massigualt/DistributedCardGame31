package GUI;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Angie
 */
public class ScreensFramework extends Application {
    
    public static String screenLoginID = "main";
    public static String screenLoginFile = "ScreenLogin.fxml";
    public static String screenGameInit = "client";
    public static String screenGameFile = "ScreenGame.fxml";
    public static String screen3ID = "screen3";
    public static String screen3File = "Screen3.fxml";
    public static String screenServerInit = "timer";
    public static String screenServerInitFile = "ServerInit.fxml";
    public static String screen2ID = "screen2";
    
    
    @Override
    public void start(Stage primaryStage) {
        
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(ScreensFramework.screenLoginID, ScreensFramework.screenLoginFile);
        //mainContainer.loadScreen(ScreensFramework.screen2ID, ScreensFramework.screenGameFile);
        mainContainer.loadScreen(ScreensFramework.screen3ID, ScreensFramework.screen3File);
        mainContainer.loadScreen(ScreensFramework.screenServerInit, ScreensFramework.screenServerInitFile);
        mainContainer.loadScreen(ScreensFramework.screenGameInit, ScreensFramework.screenGameFile);
        
        mainContainer.setScreen(ScreensFramework.screenLoginID);
        
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
