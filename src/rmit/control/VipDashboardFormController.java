package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.entity.User;

import java.io.IOException;
import java.util.Optional;

public class VipDashboardFormController {
    public AnchorPane vipDashboardFormContext;
    public Label lblName;
    public User currentUser;
    public void setUser(User user){
        this.currentUser = user;
        lblName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
    }
    public void logoutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> type = alert.showAndWait();
        if (type.get()==ButtonType.YES){
            Stage stage = (Stage) vipDashboardFormContext.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
            stage.centerOnScreen();
        }
    }

    public void profileSettingsOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/EditUserProfileForm.fxml"));
        Scene scene = new Scene(loader.load());
        EditUserProfileFormController editUserProfileFormController = loader.getController();
        editUserProfileFormController.setUser(currentUser);
        editUserProfileFormController.setUserDetails();

        Stage stage = (Stage) vipDashboardFormContext.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
