package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.dao.DatabaseAccessCode;
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

            currentUser = new DatabaseAccessCode().searchUser(txtUsername.getText(),txtPassword.getText());
            if(currentUser!=null){
                new Alert(Alert.AlertType.INFORMATION,"Login Sucessfull").show();
                FXMLLoader loader;
                if(currentUser.isVip()){
                    //setting up the vip User interface loader
                    loader = new FXMLLoader(getClass().getResource("../view/VipDashboardForm.fxml"));
                }else {
                    //setting up the normal User interface loader
                    loader = new FXMLLoader(getClass().getResource("../view/DashboardForm.fxml"));
                }
                //setting the stage depending on the User
                Scene scene = new Scene(loader.load());
                if(currentUser.isVip()){
                    VipDashboardFormController vipDashboardFormController = loader.getController();
                    vipDashboardFormController.setUser(currentUser);
                }
                else {
                    DashboardFormController dashboardFormController = loader.getController();
                    dashboardFormController.setUser(currentUser);
                }
                Stage stage = (Stage) LoginFormContext.getScene().getWindow();
                stage.setScene(scene);
                stage.centerOnScreen();
            }else {
                new Alert(Alert.AlertType.ERROR,"Incorrect Credentials. Please try again").show();
            }
        }catch (SQLException | ClassNotFoundException ex){
            new Alert(Alert.AlertType.ERROR,"Error in when logging into the account: "+ex.getMessage());
        }
    }

    public void createAccOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) LoginFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CreateAccountForm.fxml"))));
        stage.setTitle("Data Analytics Hub");
        stage.centerOnScreen();
    }

}
