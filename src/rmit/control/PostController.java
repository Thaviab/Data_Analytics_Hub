package rmit.control;

import rmit.dao.DBConnection;
import rmit.model.Posts;

import java.sql.*;

public class PostController {
    public static boolean addPost(int id, String content, String author, int likes, int shares, String dateTime) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO posts (postId, content, author, noOfLikes, noOfShares, dateTime)" +
                "VALUES (?,?,?,?,?,?)");
        stm.setInt(1, id);
        stm.setString(2, content);
        stm.setString(3, author);
        stm.setInt(4, likes);
        stm.setInt(5, shares);
        stm.setString(6, dateTime);
        return stm.executeUpdate() > 0;
    }
    public static boolean deletePost(int postId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("DELETE FROM posts WHERE postId='"+postId+"'");
        return stm.executeUpdate() > 0;
    }

    public static Posts searchPost(int postId) throws SQLException, ClassNotFoundException {
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

}
