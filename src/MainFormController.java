import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MainFormController {

    public TextField txtUsername;
    public TextField txtPassword;

    public void signMeOnAction(MouseEvent mouseEvent) {

        txtUsername.clear();
        txtPassword.clear();
    }

    public void createAccOnAction(MouseEvent mouseEvent) {
    }
}
