package rmit.db;

import rmit.entity.User;

import java.util.ArrayList;

public class Database {
    public static ArrayList<User> users = new ArrayList();

    static {
        users.add(new User("Linda",encryptPassword("1234")));
        users.add(new User("Anna",encryptPassword("1234")));
    }

    private static String encryptPassword(String rowPassword){

        return null;
    }
}
