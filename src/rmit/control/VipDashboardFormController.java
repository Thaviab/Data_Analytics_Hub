package rmit.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rmit.dao.DatabaseAccessCode;
import rmit.dao.DBConnection;
import rmit.entity.Posts;
import rmit.entity.User;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class VipDashboardFormController {
    public AnchorPane vipDashboardFormContext;
    public Label lblName;
    public User currentUser;
    public TextField txtAddId;
    public TextField txtAddContent;
    public TextField txtAddAuthor;
    public TextField txtAddLikes;
    public TextField txtAddShares;
    public TextField txtRetId;
    public TextArea txtAreaRetrievePost;
    public TextField txtDeletePostId;
    public TextField txtRetNPosts;
    public TextField txtExportPost;
    public AnchorPane importPostsFormContext;
    public AnchorPane displayPieCHartFormContext;
    public AnchorPane exportPostFormContext;
    public AnchorPane retreiveTopPostsFormContext;
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
        //getting User object to display yhe name
        this.currentUser = user;
        lblName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
    }
    public void logoutOnAction(ActionEvent actionEvent) throws IOException {
        //getting confirmation
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

    public void addPostOnAction(ActionEvent actionEvent) {
        //getting the local time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = currentDateTime.format(formatter);
        try {
            //passing to addPost in PostController
            Posts newPost = new Posts(Integer.parseInt(txtAddId.getText()),
                    txtAddContent.getText(),txtAddAuthor.getText(),Integer.parseInt(txtAddLikes.getText()),
                    Integer.parseInt(txtAddShares.getText()),formattedDateTime);
            //passing to addPost in DatabaseAccessCode
            boolean isAdded = new DatabaseAccessCode().addPost(newPost);
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
            Posts post = new DatabaseAccessCode().searchPost(postId);
            if(post != null){
                StringBuilder displayPost = new StringBuilder();
                displayPost.append("Post ID: "+post.getPostID()+"\nContent: "
                        +post.getContent()+"\nAuthor: "+post.getAuthor()
                        +"\nNo of Likes: "+post.getNoOfLikes()+"\nNo of Shares: "+post.getNoOfShares()
                        +"\nDate & Time: "+post.getDateTime());
                //Displaying the StringBUilder in TextArea
                txtAreaRetrievePost.setText(displayPost.toString());
            }
        }catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid post ID. Try Again").show();
            txtRetId.clear();
            txtAreaRetrievePost.clear();
        } catch (SQLException | ClassNotFoundException e) {
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
            boolean isRemoved = new DatabaseAccessCode().deletePost(postId);
            if(isRemoved){
                txtDeletePostId.clear();
                new Alert(Alert.AlertType.INFORMATION,"Post removed!").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Failed to remove the post. Please try again").show();
            }
        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid post ID. Try Again").show();
            txtDeletePostId.clear();
        }catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void retrieveNPostsOnAction(ActionEvent actionEvent) {
        if(txtRetNPosts.getText() == null || txtRetNPosts.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Give a valid number").show();
            return;
        }
        try {
            int n = Integer.parseInt(txtRetNPosts.getText());
            //calling topLikesPosts method in DatabaseAccessCode and return the ArrayList
            List<Posts> topPosts = new DatabaseAccessCode().topLikePosts(n);
            //using to String Builder to display text in TextArea
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
        }catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR,"Invalid Number. Try Again").show();
            txtRetNPosts.clear();
            txtAreaTopNPosts.clear();
        } catch (SQLException | ClassNotFoundException e) {
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
            //passing to DatabaseAccessCode to return the post
            Posts exportPost = new DatabaseAccessCode().searchPost(postId);
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
    public void importFileOnAction(ActionEvent actionEvent) {
        //Filechooser to select csv file
        FileChooser fileSelect = new FileChooser();
        fileSelect.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV FIles","*.csv"));
        File selectedFile = fileSelect.showOpenDialog(null);

        if(selectedFile != null){
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))){
                String line = br.readLine(); //skipping first line

                Connection connection = DBConnection.getInstance().getConnection();

                boolean affectedRows = false;

                while ((line = br.readLine()) != null){
                    String[] lineData = line.split(",");
                    int id = Integer.parseInt(lineData[0].trim());

                    if(!idCheck(id)){
                        //getting parameters from the read line
                        String content = lineData[1].trim();
                        String author = lineData[2].trim();
                        int likes = (Integer.parseInt(lineData[3].trim()));
                        int shares = Integer.parseInt(lineData[4].trim());
                        String dateTime = lineData[5].trim();

                        Posts importPost = new Posts(id,content,author,likes,shares,dateTime);

                        //passing the Posts object to addPost method in DatabaseAccess Code
                        affectedRows = new DatabaseAccessCode().addPost(importPost);
                    }
                }
                if(affectedRows){
                    new Alert(Alert.AlertType.INFORMATION,"Import sucessfull").show();
                }else {
                    new Alert(Alert.AlertType.INFORMATION,"No posts were imported").showAndWait();
                }
            } catch (IOException | SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }else {
            new Alert(Alert.AlertType.ERROR,"Import CSV File Failed").show();
        }
    }
    private boolean idCheck(int postId) throws SQLException, ClassNotFoundException {
        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery("SELECT COUNT(postId) FROM posts WHERE postId='"+postId+"'");
        if (rst.next()){
            return rst.getInt(1) > 0;
        }
        return false;
    }

    public void btnAddPostControlPanel(ActionEvent actionEvent) {
        addPostFormContext.toFront();
        txtAddId.clear();
        txtAddContent.clear();
        txtAddAuthor.clear();
        txtAddLikes.clear();
        txtAddShares.clear();
    }

    public void btnRetrievePostControlPanel(ActionEvent actionEvent) {
        retrievePostFormContext.toFront();
        txtRetId.clear();
        txtAreaRetrievePost.clear();
    }

    public void btnremovePostControlPanel(ActionEvent actionEvent) {
        removePostFormContext.toFront();
        txtDeletePostId.clear();
    }

    public void btnTopPostControlPanel(ActionEvent actionEvent) {
        retreiveTopPostsFormContext.toFront();
        txtRetNPosts.clear();
        txtAreaTopNPosts.clear();
    }

    public void btnExportPostControlPanel(ActionEvent actionEvent) {
        exportPostFormContext.toFront();
        txtExportPost.clear();
    }

    public void btnDataChartControlPanel(ActionEvent actionEvent) {
        displayPieCHartFormContext.toFront();

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;

        try {
            //getting post count with likes 0 to 99
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stmt1 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares BETWEEN 0 AND 99");
            ResultSet rst1 = stmt1.executeQuery();
            if (rst1.next()) {
                count1 = rst1.getInt(1);
            }

            //getting post count with likes 100 to 999
            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares BETWEEN 100 AND 999");
            ResultSet rst2 = stmt2.executeQuery();
            if (rst2.next()) {
                count2 = rst2.getInt(1);
            }

            //getting post count with likes 1000 and more
            PreparedStatement stmt3 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares >= 1000");
            ResultSet rst3 = stmt3.executeQuery();
            if (rst3.next()) {
                count3 = rst3.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }

        //creating pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("0-99 #Shares", count1),
                new PieChart.Data("100-999 #Shares", count2),
                new PieChart.Data("1000+ #Shares", count3));

        //creating the pie chart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Distribution of Posts by Shares");

        //setting coordinates of the anchor pane
        AnchorPane.setTopAnchor(pieChart, 0.0);
        AnchorPane.setRightAnchor(pieChart, 0.0);
        AnchorPane.setBottomAnchor(pieChart, 0.0);
        AnchorPane.setLeftAnchor(pieChart, 0.0);

        displayPieCHartFormContext.getChildren().add(pieChart);
    }

    public void btnImportPostControlPanel(ActionEvent actionEvent) {
        importPostsFormContext.toFront();
    }
}
