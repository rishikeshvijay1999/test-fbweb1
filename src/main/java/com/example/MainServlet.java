import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/main")
@MultipartConfig
public class MainServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://192.168.138.114:3306/myDB";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                String name = request.getParameter("Name");
                String mobile = request.getParameter("mobile");
                String email = request.getParameter("email");
                String password = request.getParameter("psw");
                String bio = request.getParameter("bio");
                String address = request.getParameter("address");

                // Check if the email already exists in the database
                if (isEmailRegistered(connection, email)) {
                    out.println("Email already registered. Please choose another email.");
                    return;
                }

                if (!isValidName(name)) {
                    out.println("Invalid input for Name, Name does not contain numbers only letters");
                    return;
                }

                if (!isValidMobile(mobile)) {
                    out.println("Invalid input for Mobile");
                    return;
                }

                if (!isValidPassword(password)) {
                    out.println("Invalid input for Password");
                    return;
                }

                String hashedPassword = hashPassword(password);

                String sql = "INSERT INTO web (name, mobile, email, password) VALUES (?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, mobile);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, hashedPassword);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);

                            // Store additional details in the user_details table
                            storeAdditionalDetails(connection, userId, bio, address);

                            out.println("User registered successfully!");
                        } else {
                            out.println("Failed to register user.");
                        }
                    } else {
                        out.println("Failed to register user.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private void storeAdditionalDetails(Connection connection, int userId, String bio, String address) throws SQLException {
        String sql = "INSERT INTO user_details (user_id, bio, address) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, address);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Additional details stored successfully!");
            } else {
                System.out.println("Failed to store additional details.");
            }
        }
    }

    private boolean isEmailRegistered(Connection connection, String email) throws SQLException {
        String checkEmailSql = "SELECT COUNT(*) FROM web WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailSql)) {
            checkEmailStatement.setString(1, email);
            try (ResultSet resultSet = checkEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashedBytes) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }

    private boolean isValidName(String name) {
        // Simple validation: Only letters allowed in the name
        return name.matches("[a-zA-Z]+");
    }

    private boolean isValidMobile(String mobile) {
        // Simple validation: Numeric and 10 digits
        return mobile.matches("\\d{10}");
    }

    private boolean isValidPassword(String password) {
        // Simple validation: At least 8 characters
        return password.length() >= 8;
    }
}
