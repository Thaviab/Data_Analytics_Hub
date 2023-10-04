package rmit.control;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginFormController {

    public TextField txtUsername;
    public TextField txtPassword;

    public void initialize(){

    }

    public void signMeOnAction(MouseEvent mouseEvent) {

        txtUsername.clear();
        txtPassword.clear();
    }

    public void createAccOnAction(MouseEvent mouseEvent) {
    }
}
