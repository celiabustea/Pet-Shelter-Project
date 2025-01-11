package org.example.petshelterv0;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private int id;
    private String userId;
    private String name;
    private String breed;
    private String description;
    private String status;
    private String requestDate;
    private String requestType;
    private String imagePath;
    private int age;

    // Constructor
    public Request(int id, String userId, String name, String breed, String description,
                   String status, String requestDate, String requestType, int age, String imagePath) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.breed = breed;
        this.description = description;
        this.status = status;
        this.requestDate = requestDate;
        this.requestType = requestType;
        this.age = age;
        this.imagePath = imagePath;
    }


    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public int getAge() {
        return age;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static void saveRequest(Request request) {
        String query = "INSERT INTO petrequests (name, breed, description, status, request_date, request_type, image_path, age, user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DBHelper.executeUpdateQuery(query,
                request.getName(),
                request.getBreed(),
                request.getDescription(),
                request.getStatus(),
                request.getRequestDate(),
                request.getRequestType(),
                request.getImagePath(),
                request.getAge(),
                request.getUserId());
    }

    public static boolean isValidRequest(String name, String requestType, String userName) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (requestType == null || requestType.isEmpty() ||
                (!requestType.equalsIgnoreCase("adopt") && !requestType.equalsIgnoreCase("surrender"))) {
            return false;
        }
        if (userName == null || userName.isEmpty()) {
            return false;
        }
        return true;
    }


    public static List<Request> fromResultSet(ResultSet resultSet) throws SQLException {
        List<Request> requests = new ArrayList<>();
        while (resultSet.next()) {
            Request request = new Request(
                    resultSet.getInt("id"),
                    resultSet.getString("user_id"),
                    resultSet.getString("name"),
                    resultSet.getString("breed"),
                    resultSet.getString("description"),
                    resultSet.getString("status"),
                    resultSet.getString("request_date"),
                    resultSet.getString("request_type"),
                    resultSet.getInt("age"),
                    resultSet.getString("image_path")
            );
            requests.add(request);
        }
        return requests;
    }
}
