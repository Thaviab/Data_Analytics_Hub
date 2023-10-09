package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAccountFormController {
    public AnchorPane createAccFormContext;
    public TextField txtUsername;
    public TextField txtFirstName;
    public TextField txtSecondName;
    public TextField txtPwd;

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) createAccFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
        stage.centerOnScreen();
    }

    public void createOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("INSERT INTO users (username, password, first_name, last_name) " +
                    "VALUES (?,?,?,?)");
            stm.setObject(1, txtUsername.getText());
            stm.setObject(2, txtPwd.getText());
            stm.setObject(3, txtFirstName.getText());
            stm.setObject(4, txtSecondName.getText());
            int affectedRows = stm.executeUpdate();
            if (affectedRows > 0) {
                txtUsername.clear();
                txtPwd.clear();
                txtFirstName.clear();
                txtSecondName.clear();
                new Alert(Alert.AlertType.WARNING, "Account created sucessfully").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Account creation unsucessfull");
            }
        }catch (SQLException | ClassNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }
}
