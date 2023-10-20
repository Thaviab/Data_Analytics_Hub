package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.dao.DatabaseAccessCode;
import rmit.entity.User;

import java.io.IOException;
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
        //setting the parameters of User object
        txtUsernameChange.setText(currentUser.getUsername());
        txtFirstNameChange.setText(currentUser.getFirstName());
        txtLastNameChange.setText(currentUser.getLastName());
        txtPwdChange.setText(currentUser.getPassword());
    }

    public void updateAccountOnAction(ActionEvent actionEvent) {
        //checking the fields
        if(txtUsernameChange.getText().isEmpty() || txtFirstNameChange.getText().isEmpty() ||
                txtLastNameChange.getText().isEmpty() || txtPwdChange.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Invalid User details!").show();
            return;
        }
        String username = txtUsernameChange.getText();
        String firstName = txtFirstNameChange.getText();
        String lastName = txtLastNameChange.getText();
        String password = txtPwdChange.getText();
        try {
            User updateUser = new User(username,password,firstName,lastName);
            //passing into DatabaseAccessCode updateArtist method
            boolean isUpdated = new DatabaseAccessCode().updateUser(updateUser,currentUser.getUsername());

            if(isUpdated){
                //updating the object
                currentUser.setUsername(username);
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setPassword(password);
                new Alert(Alert.AlertType.INFORMATION,"Profile updated sucessfully").show();
                setUserDetails();
            }else {
                new Alert(Alert.AlertType.ERROR,"Failed to update profile. Please check the User details").show();
            }
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader;
            //if the User is vip, the form context that should be catached is different
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
