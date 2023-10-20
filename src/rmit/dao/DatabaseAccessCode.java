package rmit.dao;

import rmit.entity.Posts;
import rmit.entity.User;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseAccessCode {
    //--------------------------------User Database Controls-------------------/
    //Add new user
    public boolean addUser(User u) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO users (username, password, first_name, last_name)" +
                "VALUES (?,?,?,?)");
        stm.setString(1,u.getUsername());
        stm.setString(2,u.getPassword());
        stm.setString(3,u.getFirstName());
        stm.setString(4,u.getLastName());
        return stm.executeUpdate() > 0;
    }

    //Search user for login
    public User searchUser(String inputUsername, String inputPassword) throws SQLException, ClassNotFoundException {
        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM users WHERE username='"+inputUsername+"'");
        if(rst.next()){
            if(rst.getString("password").equals(inputPassword)){
                String username = rst.getString("username");
                String password = rst.getString("password");
                String firstName = rst.getString("first_name");
                String lastName = rst.getString("last_name");
                boolean isVip = rst.getBoolean("is_vip");
                return new User(username,password,firstName,lastName,isVip);
            }
        }
        return null;
    }

    //update user from profile settings
    public boolean updateUser(User u, String currentUsername) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("UPDATE users SET username=?, first_name=?, last_name=?, password=? WHERE username=?");
        stm.setString(1,u.getUsername());
        stm.setString(2,u.getFirstName());
        stm.setString(3,u.getLastName());
        stm.setString(4,u.getPassword());
        stm.setString(5,currentUsername);
        return stm.executeUpdate() > 0;
    }

    //update user to vip
    public boolean updateToVip(boolean isVip, String currentUsername) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("UPDATE users SET is_vip=? WHERE username=?");
        stm.setBoolean(1,isVip);
        stm.setString(2,currentUsername);
        return stm.executeUpdate() > 0;

    }


    //-----------------------------Posts Database Controls----------------------//

    //Add new post
    public boolean addPost(Posts p) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO posts (postId, content, author, noOfLikes, noOfShares, dateTime)" +
                "VALUES (?,?,?,?,?,?)");
        stm.setInt(1, p.getPostID());
        stm.setString(2, p.getContent());
        stm.setString(3, p.getAuthor());
        stm.setInt(4, p.getNoOfLikes());
        stm.setInt(5, p.getNoOfShares());
        stm.setString(6, p.getDateTime());
        return stm.executeUpdate() > 0;
    }

    //delete a post
    public boolean deletePost(int postId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("DELETE FROM posts WHERE postId='"+postId+"'");
        return stm.executeUpdate() > 0;
    }

    //search post
    public Posts searchPost(int postId) throws SQLException, ClassNotFoundException {
        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM posts WHERE postId='"+postId+"'");
        if(rst.next()){
            String content = rst.getString("content");
            String author = rst.getString("author");
            int noOfLikes = rst.getInt("noOfLikes");
            int noOfShares = rst.getInt("noOfShares");
            String dateTime = rst.getString("dateTime");
            return new Posts(postId,content,author,noOfLikes,noOfShares,dateTime);
        }
        return null;
    }

    //retrieve top like posts
    public ArrayList<Posts> topLikePosts(int num) throws SQLException, ClassNotFoundException {
        Statement stm = DBConnection.getInstance().getConnection().createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM posts ORDER BY noOfLikes DESC LIMIT '"+num+"'");
        ArrayList<Posts> topPosts = new ArrayList<>();
        while (rst.next()){
            Posts post = new Posts(rst.getInt("postId"),rst.getString("content"),rst.getString("author"),
                    rst.getInt("noOfLikes"),rst.getInt("noOfShares"),rst.getString("dateTime"));
            topPosts.add(post);
        }
        return topPosts;
    }

}
