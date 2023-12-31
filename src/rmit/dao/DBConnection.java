package rmit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;
    private DBConnection() throws ClassNotFoundException, SQLException{
        String path = "D:\\RMIT\\Sem 2\\Advance Prog Prac\\Assignment2\\";
        connection = DriverManager.getConnection("jdbc:sqlite:"+path+"Data_Analytics_Hub\\dataHub.db");
    }
    public static DBConnection getInstance() throws ClassNotFoundException, SQLException{
        if(dbConnection==null){
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }
    public Connection getConnection(){
        return connection;
    }
}
