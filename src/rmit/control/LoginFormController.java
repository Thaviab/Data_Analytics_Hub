package rmit.control;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.mindrot.jbcrypt.BCrypt;
import rmit.db.Database;
import rmit.entity.User;

public class LoginFormController {
    public TextField txtUsername;
    public AnchorPane LoginFormContext;
    public PasswordField txtPassword;

    public void initialize(){

    }

    public void loginOnAction(ActionEvent actionEvent) {
        User selectedUser = Database.users.stream().
                filter(user -> user.getUsername().
                        equals(txtUsername.getText())).
                findFirst().orElse(null);
        if (selectedUser!=null){
            if(BCrypt.checkpw(txtPassword.getText(),selectedUser.getPassword())){
                System.out.println(String.format("user logged"));
            }else {
                System.out.println("Wrong Password");
            }
        }else {
            new Alert(Alert.AlertType.WARNING,"Username not found!").show();
        }
    }

    public void createAccOnAction(ActionEvent actionEvent) {
    }
}
