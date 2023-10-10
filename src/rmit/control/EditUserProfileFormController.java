package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.db.DBConnection;
import rmit.entity.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditUserProfileFormController {
    public AnchorPane edtiUserProfileFormContext;
    public TextField txtUsernameChange;
    public TextField txtFirstNameChange;
    public TextField txtLastNameChange;
    public TextField txtPwdChange;
    public User currentUser;
    public void setUser(User user){
        this.currentUser = user;
    }
    public void setUserDetails(){
        txtUsernameChange.setText(currentUser.getUsername());
        txtFirstNameChange.setText(currentUser.getFirstName());
        txtLastNameChange.setText(currentUser.getLastName());
        txtPwdChange.setText(currentUser.getPassword());
    }

    public void updateAccountOnAction(ActionEvent actionEvent) {
        String username = txtUsernameChange.getText();
        String firstName = txtFirstNameChange.getText();
        String lastName = txtLastNameChange.getText();
        String password = txtPwdChange.getText();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE users SET username=?, first_name=?, last_name=?, password=? WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1,username);
            stm.setString(2,firstName);
            stm.setString(3,lastName);
            stm.setString(4,password);
            stm.setString(5,currentUser.getUsername());
            int affectedRows = stm.executeUpdate();
            if(affectedRows>0){
                currentUser.setUsername(username);
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setPassword(password);
                new Alert(Alert.AlertType.INFORMATION,"Profile updated sucessfully").show();
                setUserDetails();
            }else {
                new Alert(Alert.AlertType.ERROR,"Failed to update profile. Please check the user details").show();
            }
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader;
            if(currentUser.isVip()){
                loader = new FXMLLoader(getClass().getResource("../view/VipDashboardForm.fxml"));
                Scene scene = new Scene(loader.load());
                VipDashboardFormController vipDashboardFormController = loader.getController();
                vipDashboardFormController.setUser(currentUser);
                Stage stage = (Stage) edtiUserProfileFormContext.getScene().getWindow();
                stage.setScene(scene);
                stage.centerOnScreen();
            }
            loader = new FXMLLoader(getClass().getResource("../view/DashboardForm.fxml"));
            Scene scene = new Scene(loader.load());
            DashboardFormController dashboardFormController = loader.getController();
            dashboardFormController.setUser(currentUser);
            Stage stage = (Stage) edtiUserProfileFormContext.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }
}
