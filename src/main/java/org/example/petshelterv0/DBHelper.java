package org.example.petshelterv0;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pet_shelter_db"; // Change to your database URL
    private static final String DB_USERNAME = "root"; // Replace with your database username
    private static final String DB_PASSWORD = ""; // Replace with your database password

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static boolean isAdmin(String email) {
        try (Connection connection = getConnection()) {
            String query = "SELECT role FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    return "admin".equalsIgnoreCase(role); // Return true if user is admin
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if email not found
    }

    // Check if the email already exists in the database
    public static boolean isEmailExisting(String email) {
        try (Connection connection = getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if email exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if email does not exist
    }

    // Get a user by their email
    public static User getUser(String email) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String fullName = rs.getString("full_name");
                    String role = rs.getString("role");
                    return new User(username, password, email, fullName, role); // Return user object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no user found
    }

    // Save a new user to the database
    public static void saveUser(User user) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getFullName());
                stmt.setString(5, user.getRole());
                stmt.executeUpdate(); // Execute the insert
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void savePet(Pet pet) {
        String query = "INSERT INTO pets (name, age, image_path, breed, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, pet.getName());
            stmt.setInt(2, pet.getAge());
            stmt.setString(3, pet.getImagePath());
            stmt.setString(4, pet.getBreed());
            stmt.setString(5, pet.getDescription());
            stmt.executeUpdate();  // Execute the insert statement

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ResultSet getAllPets() {
        String query = "SELECT * FROM pets";
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void deletePet(int petId) {
        String query = "DELETE FROM pets WHERE pet_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, petId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
