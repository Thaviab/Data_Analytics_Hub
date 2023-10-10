package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rmit.db.DBConnection;
import rmit.entity.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class DashboardFormController {

    public AnchorPane dashboardFormContext;
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
            Stage stage = (Stage) dashboardFormContext.getScene().getWindow();
            //stage.setTitle("Dashboard");
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
            stage.centerOnScreen();
        }
    }

    public void subscribeOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to subscribe and be a VIP user?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> type = alert.showAndWait();
        if(type.get()==ButtonType.YES){
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement stm = connection.prepareStatement("UPDATE users SET is_vip=? WHERE username=?");
                stm.setBoolean(1,true);
                stm.setString(2,currentUser.getUsername());
                int affectedRows = stm.executeUpdate();
                if(affectedRows>0){
                    currentUser.setVip(true);
                    new Alert(Alert.AlertType.INFORMATION,"You are now a VIP user! Log again to experience the premium features").showAndWait();
                    Stage stage = (Stage) dashboardFormContext.getScene().getWindow();
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
                    stage.centerOnScreen();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Failed to update VIP status. Try again later").showAndWait();
                }
            }catch (SQLException | ClassNotFoundException | IOException ex){
                new Alert(Alert.AlertType.ERROR,"Error updating VIP status: "+ ex.getMessage()).show();
            }
        }
    }

    public void profileSettingsOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/EditUserProfileForm.fxml"));
        Scene scene = new Scene(loader.load());
        EditUserProfileFormController editUserProfileFormController = loader.getController();
        editUserProfileFormController.setUser(currentUser);
        editUserProfileFormController.setUserDetails();

        Stage stage = (Stage) dashboardFormContext.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();

    }
}
