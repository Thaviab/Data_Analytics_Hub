import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppInitializer extends Application {
    public static void main(String[] args) {
        launch(args); //calling start method
    }
    //catching the fxml file -> then load -> then setting the scene -> show the stage
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(
                new Scene(FXMLLoader.load(
                        getClass().getResource("MainForm.fxml"))));
        primaryStage.setTitle("Data Analytics Hub");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
