
import java.sql.SQLException;

import org.example.petshelterv0.DBHelper;
import org.example.petshelterv0.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DBHelperTest {

    @Test
    public void testIsEmailExisting() {
        boolean result = DBHelper.isEmailExisting("busteacarmina@gmail.com");
        assertTrue(result);
    }

    @Test
    public void testGetUser() {
        User user = DBHelper.getUser("nonexistentemail@example.com");
        assertNull(user);
    }
    @Test
    public void testIsAdmin() throws SQLException {

        String email = "busteacarmina@gmail.com";
        boolean result = DBHelper.isAdmin(email);
        assertTrue(result, "The user should be an admin");
    }
    @Test
    public void testUserConstructor() {

        User user = new User("john_doe", "1234", "johndoe@gmail.com", "John Doe", "general");
        assertNotNull(user, "User should be created successfully");
        assertEquals("john_doe", user.getUsername(), "Username should match");
        assertEquals("johndoe@gmail.com", user.getEmail(), "Email should match");
        assertEquals("1234", user.getPassword(), "Password should match");
        assertEquals("John Doe", user.getFullName(), "Full name should match");
        assertEquals("general", user.getRole(), "Role should match");
    }

    @Test
    public void testSaveUser() {
        DBHelper dbHelper = new DBHelper();
        User user = new User("john_doe", "1234", "johndoe@gmail.com", "John Doe", "general");


    }



}
