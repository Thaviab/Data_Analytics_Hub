package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rmit.db.DBConnection;
import rmit.model.Posts;
import rmit.model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.File;

public class DashboardFormController {

    public AnchorPane dashboardFormContext;
    public Label lblName;
    public User currentUser;
    public TextField txtAddId;
    public TextField txtAddContent;
    public TextField txtAddAuthor;
    public TextField txtAddLikes;
    public TextField txtAddShares;
    public TextField txtRetId;
    public TextField txtRetContent;
    public TextField txtRetAuthor;
    public TextField txtRetLikes;
    public TextField txtRetShares;
    public TextField txtRetDateTime;
    public TextField txtDeletePostId;
    public TextField txtRetNPosts;
    public TextField txtExportPost;
    public Posts currentPost;
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

    public void addPostOnAction(ActionEvent actionEvent) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = currentDateTime.format(formatter);
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("INSERT INTO posts (postId, content, author, noOfLikes, noOfShares, dateTime)"+
                    "VALUES (?,?,?,?,?,?)");
            stm.setString(1,txtAddId.getText());
            stm.setString(2,txtAddContent.getText());
            stm.setString(3,txtAddAuthor.getText());
            stm.setInt(4, Integer.parseInt(txtAddLikes.getText()));
            stm.setInt(5, Integer.parseInt(txtAddShares.getText()));
            stm.setString(6,formattedDateTime);
            int affrectedRows = stm.executeUpdate();
            if (affrectedRows > 0) {
                txtAddId.clear();
                txtAddContent.clear();
                txtAddAuthor.clear();
                txtAddLikes.clear();
                txtAddShares.clear();
                new Alert(Alert.AlertType.INFORMATION, "Post added sucessfully").show();
            }else {
                new Alert(Alert.AlertType.INFORMATION, "Post adding unsucessfull").show();
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    public void retrievePostOnAction(ActionEvent actionEvent) {
        int postId = Integer.parseInt(txtRetId.getText());
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM posts WHERE postId='"+postId+"'");
            if(rst.next()){
                txtRetContent.setText(rst.getString("content"));
                txtRetAuthor.setText(rst.getString("author"));
                txtRetLikes.setText(String.valueOf(rst.getInt("noOfLikes")));
                txtRetShares.setText(String.valueOf(rst.getInt("noOfShares")));
                txtRetDateTime.setText(rst.getString("dateTime"));
            }else {
                new Alert(Alert.AlertType.WARNING,"No post found with the post ID!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void removePostOnAction(ActionEvent actionEvent) {
        int postId = Integer.parseInt(txtDeletePostId.getText());
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM posts WHERE postId='"+postId+"'";
            PreparedStatement stm = connection.prepareStatement(sql);
            int affectedRows = stm.executeUpdate();
            if(affectedRows>0){
                txtDeletePostId.clear();
                new Alert(Alert.AlertType.INFORMATION,"Post removed!").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Failed to remove the post. Please try again").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void retrieveNPostsOnAction(ActionEvent actionEvent) throws IOException {
        try {
            int n = Integer.parseInt(txtRetNPosts.getText());
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM posts ORDER BY noOfLikes DESC LIMIT '"+n+"'");
            //creating a list for N posts
            List<Posts> topPosts = new ArrayList<>();
            while (rst.next()){
                Posts post = new Posts(rst.getInt("postId"),rst.getString("content"),rst.getString("author"),
                        rst.getInt("noOfLikes"),rst.getInt("noOfShares"),rst.getString("dateTime"));
                topPosts.add(post);
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/RetrieveTopNPostsForm.fxml"));
            Parent parent = loader.load();
            RetrieveTopNPostsFormController retrieveTopNPostsFormController = loader.getController();
            retrieveTopNPostsFormController.displayPosts(topPosts);
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("Top N Posts");
            stage.show();
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void exportPostOnAction(ActionEvent actionEvent) {
        String postId = txtExportPost.getText();
        if(postId == null){
            new Alert(Alert.AlertType.ERROR,"Please provide a post ID").show();
            return;
        }
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM posts WHERE postId='"+Integer.parseInt(postId)+"'");
            if(!rst.next()){
                new Alert(Alert.AlertType.ERROR,"No post found with the given post ID").show();
                return;
            }
            //fetching post data
            String content = rst.getString("content");
            String author = rst.getString("author");
            int noOfLikes = rst.getInt("noOfLikes");
            int noOfShares = rst.getInt("noOfShares");
            String dateTime = rst.getString("dateTime");

            //Using javaFX's FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Post");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files","*.csv"));
            File file = fileChooser.showSaveDialog(txtExportPost.getScene().getWindow());
            //Checking the file
            if(file != null){
                try (FileWriter fileWriter = new FileWriter(file)){
                    fileWriter.write(postId+", "+content+", "+author+", "+noOfLikes+", "+noOfShares+", "+dateTime+"\n");
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
                new Alert(Alert.AlertType.INFORMATION,"Post data exported sucessfully").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,"Error exporting post data: "+e.getMessage()).show();
        }

    }
}
