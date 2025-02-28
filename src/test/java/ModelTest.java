import org.example.petshelterv0.Pet;
import org.example.petshelterv0.Request;
import org.example.petshelterv0.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    // Test for Pet constructor
    @Test
    public void testPetConstructor() {
        Pet pet = new Pet("Bella", 3, "/images/bella.jpg", "Labrador", "Friendly and playful dog");

        assertNotNull(pet, "Pet object should not be null");
        assertEquals("Bella", pet.getName(), "Pet name should be 'Bella'");
        assertEquals(3, pet.getAge(), "Pet age should be 3");
        assertEquals("/images/bella.jpg", pet.getImagePath(), "Image path should match");
        assertEquals("Labrador", pet.getBreed(), "Pet breed should be 'Labrador'");
        assertEquals("Friendly and playful dog", pet.getDescription(), "Pet description should match");
    }

    // Test for Request constructor
    @Test
    public void testRequestConstructor() {
        Request request = new Request(1, "user123", "Bella", "Labrador", "Friendly and playful dog",
                "pending", "2025-01-10", "adopt", 3, "/images/bella.jpg");

        assertNotNull(request, "Request object should not be null");
        assertEquals(1, request.getId(), "Request ID should be 1");
        assertEquals("user123", request.getUserId(), "User ID should be 'user123'");
        assertEquals("Bella", request.getName(), "Pet name should be 'Bella'");
        assertEquals("Labrador", request.getBreed(), "Pet breed should be 'Labrador'");
        assertEquals("Friendly and playful dog", request.getDescription(), "Pet description should match");
        assertEquals("pending", request.getStatus(), "Status should be 'pending'");
        assertEquals("2025-01-10", request.getRequestDate(), "Request date should match");
        assertEquals("adopt", request.getRequestType(), "Request type should be 'adopt'");
        assertEquals(3, request.getAge(), "Age should be 3");
        assertEquals("/images/bella.jpg", request.getImagePath(), "Image path should match");
    }

    //test for user constructor
    @Test
    public void testUserConstructor() {
        User user = new User("john_doe", "1234", "johndoe@gmail.com", "John Doe", "general");

        assertNotNull(user, "User object should not be null");
        assertEquals("john_doe", user.getUsername(), "Username should be 'john_doe'");
        assertEquals("1234", user.getPassword(), "Password should be '1234'");
        assertEquals("johndoe@gmail.com", user.getEmail(), "Email should be 'johndoe@gmail.com'");
        assertEquals("John Doe", user.getFullName(), "Full name should be 'John Doe'");
        assertEquals("general", user.getRole(), "Role should be 'general'");
    }

//verifica daca toString returneaza ce trebuie
    @Test
    public void testUserToString() {
        User user = new User("john_doe", "1234", "johndoe@gmail.com", "John Doe", "general");

        String expectedString = "User{username='john_doe', password='1234', email='johndoe@gmail.com', fullName='John Doe', role='general'}";
        assertEquals(expectedString, user.toString(), "The toString method should return the expected string representation of the user");
    }

    //verfica daca se valideaza request-ul cum trebuie
    @Test
    public void testIsValidRequest() {

        boolean result = Request.isValidRequest("Bella", "adopt", "john_doe");
        assertTrue(result, "The request should be valid");


        result = Request.isValidRequest("", "adopt", "john_doe");
        assertFalse(result, "The request should be invalid because the name is empty");


        result = Request.isValidRequest("Bella", "rescue", "john_doe");
        assertFalse(result, "The request should be invalid because the request type is not 'adopt' or 'surrender'");


        result = Request.isValidRequest("Bella", "adopt", "");
        assertFalse(result, "The request should be invalid because the username is empty");
    }

    //verifica ca merg toate getters in clasa Pet
    @Test
    public void testPetGetters() {
        Pet pet = new Pet("Bella", 3, "/images/bella.jpg", "Labrador", "Friendly and playful dog");

        assertEquals("Bella", pet.getName(), "Pet name should be 'Bella'");
        assertEquals(3, pet.getAge(), "Pet age should be 3");
        assertEquals("/images/bella.jpg", pet.getImagePath(), "Image path should be '/images/bella.jpg'");
        assertEquals("Labrador", pet.getBreed(), "Pet breed should be 'Labrador'");
        assertEquals("Friendly and playful dog", pet.getDescription(), "Pet description should match");
    }



}
