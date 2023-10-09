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
import org.sqlite.core.DB;
import rmit.db.DBConnection;
import rmit.entity.User;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {
    public User currentUser;
    public TextField txtUsername;
    public AnchorPane LoginFormContext;
    public PasswordField txtPassword;

    public void initialize(){

    }

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1,txtUsername.getText());
            ResultSet rst = stm.executeQuery();
            if(rst.next()){
                String storedPassword = rst.getString("password");
                if (txtPassword.getText().equals(storedPassword)){
                    String username = rst.getString("username");
                    String password = rst.getString("password");
                    String firstName = rst.getString("first_name");
                    String lastName = rst.getString("last_name");
                    boolean isVip = rst.getBoolean("is_vip");
                    System.out.println(username+" "+password+" "+firstName+" "+lastName+" "+isVip);
                    currentUser = new User(username,password,firstName,lastName,isVip);
                    new Alert(Alert.AlertType.INFORMATION,"Login Sucessfull").show();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/DashboardForm.fxml"));
                    Scene scene = new Scene(loader.load());
                    //Fetching the controller and passing user
                    DashboardFormController dashboardFormController = loader.getController();
                    dashboardFormController.setUser(currentUser);
                    Stage stage = (Stage) LoginFormContext.getScene().getWindow();
                    stage.setScene(scene);
                    stage.centerOnScreen();
/*                    Stage stage = (Stage) LoginFormContext.getScene().getWindow();
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
                    stage.centerOnScreen();*/
                }else {
                    new Alert(Alert.AlertType.WARNING,"Password Incorrect. Please try again!").show();
                }
            }else {
                new Alert(Alert.AlertType.WARNING,"Username Incorrect. Please try again!").show();
            }
        }catch (SQLException | ClassNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void createAccOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) LoginFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CreateAccountForm.fxml"))));
        stage.centerOnScreen();
    }

}
