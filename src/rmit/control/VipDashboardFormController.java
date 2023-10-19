package rmit.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rmit.db.DBConnection;
import rmit.model.Posts;
import rmit.model.User;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        welcomeFormContext.setVisible(true);
        welcomeFormContext.toFront();

    }

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
            StringBuilder displayPost = new StringBuilder();
            if(rst.next()){
                displayPost.append("Post ID: "+rst.getInt("postID")+"\nContent"
                        +rst.getString("content")+"\nAuthor: "+rst.getString("author")
                        +"\nNo of Likes: "+rst.getInt("noOfLikes")+"\nNo of Shares: "+rst.getInt("noOfShares")
                        +"\nDate & Time: "+rst.getString("dateTime"));
                txtAreaRetrievePost.setText(displayPost.toString());
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

    public void retrieveNPostsOnAction(ActionEvent actionEvent) {
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

    public void displayPieChartOnAction(ActionEvent actionEvent) {
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stmt1 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares BETWEEN 0 AND 99");
            ResultSet rst1 = stmt1.executeQuery();
            if (rst1.next()) {
                count1 = rst1.getInt(1);
            }

            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares BETWEEN 100 AND 999");
            ResultSet rst2 = stmt2.executeQuery();
            if (rst2.next()) {
                count2 = rst2.getInt(1);
            }

            PreparedStatement stmt3 = connection.prepareStatement("SELECT COUNT(*) FROM posts WHERE noOfShares >= 1000");
            ResultSet rst3 = stmt3.executeQuery();
            if (rst3.next()) {
                count3 = rst3.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("0-99 #Shares", count1),
                new PieChart.Data("100-999 #Shares", count2),
                new PieChart.Data("1000+ #Shares", count3));

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Distribution of Posts by Shares");
        Scene pieChartScene = new Scene(pieChart, 500, 400);  // width and height values are adjustable
        Stage pieChartStage = new Stage();
        pieChartStage.setTitle("Distribution of Posts by Shares");
        pieChartStage.setScene(pieChartScene);
        pieChartStage.show();
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

                int affectedRows = 0;

                while ((line = br.readLine()) != null){
                    String[] lineData = line.split(",");
                    int id = Integer.parseInt(lineData[0].trim());

                    if(!idCheck(id)){
                        PreparedStatement stm = connection.prepareStatement("INSERT INTO posts (postId, content, author, noOfLikes, noOfShares, dateTime) " +
                                "VALUES (?,?,?,?,?,?)");
                        String content = lineData[1].trim();
                        String author = lineData[2].trim();
                        int likes = (Integer.parseInt(lineData[3].trim()));
                        int shares = Integer.parseInt(lineData[4].trim());
                        String dateTime = lineData[5].trim();

                        stm.setInt(1,id);
                        stm.setString(2,content);
                        stm.setString(3,author);
                        stm.setInt(4,likes);
                        stm.setInt(5,shares);
                        stm.setString(6,dateTime);

                        affectedRows += stm.executeUpdate();
                    }
                }
                if(affectedRows>0){
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
    }

    public void btnRetrievePostControlPanel(ActionEvent actionEvent) {
        retrievePostFormContext.toFront();
    }

    public void btnremovePostControlPanel(ActionEvent actionEvent) {
        removePostFormContext.toFront();
    }

    public void btnTopPostControlPanel(ActionEvent actionEvent) {
        retreiveTopPostsFormContext.toFront();
    }

    public void btnExportPostControlPanel(ActionEvent actionEvent) {
        exportPostFormContext.toFront();
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
