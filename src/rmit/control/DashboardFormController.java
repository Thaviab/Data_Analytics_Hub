package rmit.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    public AnchorPane exportPostFormContext;
    public AnchorPane retrieveTopNPostsFormContext;
    public TextArea txtAreaTopNPosts;
    public AnchorPane removePostFormContext;
    public AnchorPane retrievePostFormContext;
    public AnchorPane addPostFormContext;
    public AnchorPane welcomeFormContext;

    @FXML
    private void initialize(){
        //ensuring the welcome anchor pane is set visible and in front when dashboard begins
        welcomeFormContext.setVisible(true);
        welcomeFormContext.toFront();

    }
    public void setUser(User user){
        //setting the user and displaying the name
        this.currentUser = user;
        lblName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
    }
    public void logoutOnAction(ActionEvent actionEvent) throws IOException {
        //getting confirmation from the user
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
                //if updated it will return to login form
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
        //getting the local time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = currentDateTime.format(formatter);
        try {
            //passing to addPost in PostController
            boolean isAdded = PostController.addPost(Integer.parseInt(txtAddId.getText()),
                    txtAddContent.getText(),txtAddAuthor.getText(),Integer.parseInt(txtAddLikes.getText()),
                    Integer.parseInt(txtAddShares.getText()),formattedDateTime);
            if (isAdded) {
                txtAddId.clear();
                txtAddContent.clear();
                txtAddAuthor.clear();
                txtAddLikes.clear();
                txtAddShares.clear();
                new Alert(Alert.AlertType.INFORMATION, "Post added sucessfully").show();
            }else {
                new Alert(Alert.AlertType.INFORMATION, "Post adding unsucessfull").show();
            }
        }catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid inputs. Try Again").show();
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void retrievePostOnAction(ActionEvent actionEvent) {
        //checking if the input is null
        if(txtRetId.getText() == null || txtRetId.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Invalid ID. Please try again");
            return;
        }

        try {
            int postId = Integer.parseInt(txtRetId.getText());
            //passing postId to execute the query
            Posts post = PostController.searchPost(postId);
            //geeting parameter if post in not null
            if(post!=null){
                txtRetContent.setText(post.getContent());
                txtRetAuthor.setText(post.getAuthor());
                txtRetLikes.setText(String.valueOf(post.getNoOfLikes()));
                txtRetShares.setText(String.valueOf(post.getNoOfLikes()));
                txtRetDateTime.setText(post.getDateTime());
            } else {
                new Alert(Alert.AlertType.WARNING,"No post found with the post ID!").show();
            }
        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Enter a valid post ID").show();
            txtRetId.clear();
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void removePostOnAction(ActionEvent actionEvent) {
        //checking if the input is null
        if(txtDeletePostId.getText() == null || txtDeletePostId.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Invalid ID. Please try again");
            return;
        }
        try {
            int postId = Integer.parseInt(txtDeletePostId.getText());
            //calling delete query
            boolean isRemoved = PostController.deletePost(postId);
            if(isRemoved){
                txtDeletePostId.clear();
                new Alert(Alert.AlertType.INFORMATION,"Post removed!").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Failed to remove the post. Please try again").show();
            }
        }catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid post ID. Try Again").show();
            txtDeletePostId.clear();
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
            StringBuilder displayText = new StringBuilder();

            for(Posts posts : topPosts){
                displayText.append("ID: ").append(posts.getPostID())
                        .append(", Content: ").append(posts.getContent())
                        .append(", Author: ").append(posts.getAuthor())
                        .append(", Likes: ").append(posts.getNoOfLikes())
                        .append(", Shares: ").append(posts.getNoOfShares())
                        .append(", Date & Time: ").append(posts.getDateTime())
                        .append("\n-----------------------------------------------------\n");
            }
            txtAreaTopNPosts.setText(displayText.toString());
        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid post ID. Try Again").show();
            txtRetNPosts.clear();
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void exportPostOnAction(ActionEvent actionEvent) {
        //checking if the input is null or empty
        if(txtExportPost == null || txtExportPost.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Please provide a post ID").show();
            return;
        }
        try {
            int postId = Integer.parseInt(txtExportPost.getText());
            Posts exportPost = PostController.searchPost(postId);
            if(exportPost == null){
                new Alert(Alert.AlertType.ERROR,"No post found with the given post ID").show();
                return;
            }
            //fetching post data
            String content = exportPost.getContent();
            String author = exportPost.getAuthor();
            int noOfLikes = exportPost.getNoOfLikes();
            int noOfShares = exportPost.getNoOfShares();
            String dateTime = exportPost.getDateTime();

            //Using javaFX's FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Post");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files","*.csv"));
            File file = fileChooser.showSaveDialog(txtExportPost.getScene().getWindow());
            //Checking the file
            if(file != null){
                try (FileWriter fileWriter = new FileWriter(file)){
                    //writing the file
                    fileWriter.write(postId+", "+content+", "+author+", "+noOfLikes+", "+noOfShares+", "+dateTime+"\n");
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
                new Alert(Alert.AlertType.INFORMATION,"Post data exported sucessfully").show();
            }

        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid post ID. Try Again").show();
            txtExportPost.clear();
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,"Error exporting post data: "+e.getMessage()).show();
        }

    }

    public void btnAddPostControlPanel(ActionEvent actionEvent) {
        addPostFormContext.toFront();
    }

    public void btnRetrievePostControlPanel(ActionEvent actionEvent) {
        retrievePostFormContext.toFront();
    }

    public void btnremovePostControlPanel(ActionEvent actionEvent) {
        removePostFormContext.toFront();
    }

    public void btnTopPostControlPanel(ActionEvent actionEvent) {
        retrieveTopNPostsFormContext.toFront();
    }

    public void btnExportPostControlPanel(ActionEvent actionEvent) {
        exportPostFormContext.toFront();
    }
}
