package rmit.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rmit.dao.DatabaseAccessCode;
import rmit.entity.Posts;
import rmit.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAccessCodeTest {
    private DatabaseAccessCode dao;
    @BeforeEach
    void initialize(){
        dao = new DatabaseAccessCode();
    }

    @Test
    void addUser() throws SQLException, ClassNotFoundException {
        User user = new User("testUsername", "testPassword", "testFirstName", "testLastName");
        assertTrue(dao.addUser(user));
    }

    @Test
    void searchUser() throws SQLException, ClassNotFoundException {
        User userAdded = new User("testUsername1", "testPassword", "testFirstName", "testLastName");
        dao.addUser(userAdded);
        User user = dao.searchUser("testUsername1", "testPassword");
        assertNotNull(user);
        assertEquals("testUsername1", user.getUsername());
    }

    @Test
    void updateUser() throws SQLException, ClassNotFoundException {
        User userUpdateCheck = new User("User1", "12345", "FirstName", "LastName");
        dao.addUser(userUpdateCheck);

        User updatedUser = new User("updatedUsername", "updatedPassword", "updatedFirstName", "updatedLastName");
        assertTrue(dao.updateUser(updatedUser, "User1"));
    }

    @Test
    void updateToVip() throws SQLException, ClassNotFoundException {
        //by default isVip = 0
        User userVipCheck = new User("vipUser", "testPassword", "updatedFirstName", "updatedLastName");
        dao.addUser(userVipCheck);
        assertTrue(dao.updateToVip(true, "vipUser"));
    }

    @Test
    void addPost() throws SQLException, ClassNotFoundException {
        Posts post = new Posts(1, "testContent", "testAuthor", 10, 5, "15/11/2022 23:30");
        assertTrue(dao.addPost(post));
    }

    @Test
    void deletePost() throws SQLException, ClassNotFoundException {
        Posts post = new Posts(999, "deleteContent", "testAuthor", 10, 5, "2023-01-01");
        dao.addPost(post);
        assertTrue(dao.deletePost(999));
    }

    @Test
    void searchPost() throws SQLException, ClassNotFoundException {
        Posts postAdd = new Posts(888, "searchContent", "testAuthor", 10, 5, "2023-01-01");
        dao.addPost(postAdd);
        Posts post = dao.searchPost(888);
        assertNotNull(post);
        assertEquals(888, post.getPostID());
    }

    @Test
    void topLikePosts() throws SQLException, ClassNotFoundException {
        ArrayList<Posts> topPosts = dao.topLikePosts(5);
        assertNotNull(topPosts);
        assertTrue(topPosts.size() <= 5);
    }
    @AfterEach
    void revertDown() throws SQLException, ClassNotFoundException {
        // Reverting changes made by addUser() and searchUser()
        User user = dao.searchUser("testUsername", "testPassword");
        if (user != null) {
            dao.deleteUser("testUsername");
        }
        // Reverting changes made by searchUser()
        User user1 = dao.searchUser("testUsername1", "testPassword");
        if (user1 != null) {
            dao.deleteUser("testUsername1");
        }

        // Reverting changes made by updateUser()
        User updatedUser = dao.searchUser("updatedUsername", "updatedPassword");
        if (updatedUser != null) {
            dao.deleteUser("updatedUsername");
        }
        // Reverting changes made by updateToVip()
        User user2 = dao.searchUser("vipUser", "testPassword");
        if (user2 != null) {
            dao.deleteUser("vipUser");
        }

        // Reverting changes made by addPost() and searchPost()
        if (dao.searchPost(1) != null) {
            dao.deletePost(1);
        }

        // Reverting changes made by deletePost()
        if (dao.searchPost(999) != null) {
            dao.deletePost(999);
        }

        // Reverting changes made by searchPost()
        if (dao.searchPost(888) != null) {
            dao.deletePost(888);
        }
    }

}