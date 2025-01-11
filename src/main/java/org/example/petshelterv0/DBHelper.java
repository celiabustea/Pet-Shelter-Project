package org.example.petshelterv0;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DBHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pet_shelter_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static ResultSet executeSelectQuery(String query, Object... params) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeUpdateQuery(String query, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getAllPets() {
        String query = "SELECT * FROM pets";
        return executeSelectQuery(query);
    }

    public static List<Request> getAllRequestsForAdmin() {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT id, user_id, name, breed, description, status, request_date, request_type, age, image_path FROM petrequests";

        try (ResultSet rs = DBHelper.executeSelectQuery(query)) { // Assuming DBHelper.executeSelectQuery is your utility method
            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String userId = rs.getString("user_id");
                String name = rs.getString("name"); // Fetch name
                String breed = rs.getString("breed"); // Fetch breed
                String description = rs.getString("description");
                String status = rs.getString("status");
                String requestDate = rs.getString("request_date");
                String requestType = rs.getString("request_type");
                int age = rs.getInt("age"); // Fetch age
                String imagePath = rs.getString("image_path"); // Fetch image path

                Request request = new Request(id, userId, name, breed, description, status, requestDate, requestType, age, imagePath);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }



    public static void savePet(Pet pet) {
        String query = "INSERT INTO pets (name, age, image_path, breed, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            System.out.println("Saving pet: " + pet.getName() + ", Age: " + pet.getAge());  // Debugging print

            stmt.setString(1, pet.getName());
            stmt.setInt(2, pet.getAge());
            stmt.setString(3, pet.getImagePath());
            stmt.setString(4, pet.getBreed());
            stmt.setString(5, pet.getDescription());

            int rowsAffected = stmt.executeUpdate();  // Execute the insert statement
            if (rowsAffected > 0) {
                System.out.println("Pet saved successfully!");
            } else {
                System.out.println("No rows affected. Pet not saved.");
            }

        } catch (SQLException e) {
            System.err.println("Error saving pet: " + e.getMessage());
            e.printStackTrace();
        }
    }


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

    public static boolean isEmailExisting(String email) {
        try (Connection connection = getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
        return null;
    }

    public static String getUserRole(String email) {
        try (Connection connection = getConnection()) {
            String query = "SELECT role FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "general";
    }

    public static boolean isAdmin(String email) {
        String role = getUserRole(email);
        return "admin".equalsIgnoreCase(role);
    }

    public static boolean deletePetByName(String petName) {
        String sql = "DELETE FROM pets WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, petName);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
