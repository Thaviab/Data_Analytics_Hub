package rmit.control;

import rmit.dao.DBConnection;
import rmit.model.User;

import java.sql.*;

public class UserController {
    public static boolean addUser(String username, String password, String firstName, String lastName) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO users (username, password, first_name, last_name)" +
                "VALUES (?,?,?,?)");
        stm.setString(1,username);
        stm.setString(2,password);
        stm.setString(3,firstName);
        stm.setString(4,lastName);
        return stm.executeUpdate() > 0;
    }
    public static User searchUser(String inputUsername, String inputPassword) throws SQLException, ClassNotFoundException {
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

    public static boolean updateUser(String newUsername, String newFirstName, String newLastName, String newPwd, String currentUsername) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("UPDATE users SET username=?, first_name=?, last_name=?, password=? WHERE username=?");
        stm.setString(1,newUsername);
        stm.setString(2,newFirstName);
        stm.setString(3,newLastName);
        stm.setString(4,newPwd);
        stm.setString(5,currentUsername);
        return stm.executeUpdate() > 0;
    }

}
