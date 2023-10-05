package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import rmit.db.Database;
import rmit.entity.User;

import java.io.IOException;

public class LoginFormController {
    public TextField txtUsername;
    public AnchorPane LoginFormContext;
    public PasswordField txtPassword;

    public void initialize(){

    }

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        //catching the user
        User selectedUser = Database.users.stream().
                filter(user -> user.getUsername().
                        equals(txtUsername.getText())).
                findFirst().orElse(null);
        //checking password
        if (selectedUser!=null){
            if(BCrypt.checkpw(txtPassword.getText(),selectedUser.getPassword())){
                Stage stage = (Stage) LoginFormContext.getScene().getWindow();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
                stage.centerOnScreen();
            }else {
                new Alert(Alert.AlertType.WARNING,"Wrong Password!").show();
            }
        }else {
            new Alert(Alert.AlertType.WARNING,"Username not found!").show();
        }
    }

    public void createAccOnAction(ActionEvent actionEvent) {
    }

}
