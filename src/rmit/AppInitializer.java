package rmit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rmit.db.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppInitializer extends Application {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //Connection connection = DBConnection.getInstance().getConnection();
        launch(args); //calling start method

    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(
                new Scene(FXMLLoader.load(
                        getClass().getResource("view/LoginForm.fxml"))));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
