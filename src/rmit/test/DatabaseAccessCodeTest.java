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
        User user = dao.searchUser("testUsername", "testPassword");
        assertNotNull(user);
        assertEquals("testUsername", user.getUsername());
    }

    @Test
    void updateUser() throws SQLException, ClassNotFoundException {
        User updatedUser = new User("updatedUsername", "updatedPassword", "updatedFirstName", "updatedLastName");
        assertTrue(dao.updateUser(updatedUser, "testUsername"));
    }

    @Test
    void updateToVip() throws SQLException, ClassNotFoundException {
        assertTrue(dao.updateToVip(true, "testUsername"));
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

}