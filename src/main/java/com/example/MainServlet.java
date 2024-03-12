import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.Part;

@WebServlet("/main")
@MultipartConfig
public class MainServlet extends HttpServlet {
    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://192.168.75.158:3306/mydb";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql@123";

    // Handling HTTP POST requests
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // PrintWriter to send response back to the client
        PrintWriter out = response.getWriter();

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                // Retrieve user registration details from the request parameters
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

                // Hash the password before storing it in the database
                String hashedPassword = hashPassword(password);

                // SQL query to insert user details into the 'web' table
                String sql = "INSERT INTO web (name, mobile, email, password) VALUES (?, ?, ?, ?)";

                // Use PreparedStatement to prevent SQL injection
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, mobile);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, hashedPassword);

                    // Execute the SQL query
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        // Retrieve the auto-generated user ID
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);

                            // Store additional details in the 'user_details' table
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
            // Handle exceptions (e.g., ClassNotFoundException, SQLException, NoSuchAlgorithmException)
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    // Method to store additional details in the 'user_details' table
    private void storeAdditionalDetails(Connection connection, int userId, String bio, String address)
            throws SQLException {
        // SQL query to insert additional details into the 'user_details' table
        String sql = "INSERT INTO user_details (user_id, bio, address) VALUES (?, ?, ?)";

        // Use PreparedStatement to prevent SQL injection
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, address);

            // Execute the SQL query
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Additional details stored successfully!");
            } else {
                System.out.println("Failed to store additional details.");
            }
        }
    }

    // Method to check if the email is already registered in the database
    private boolean isEmailRegistered(Connection connection, String email) throws SQLException {
        // SQL query to check if the email exists in the 'web' table
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

    // Method to hash the password using SHA-256
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());

        StringBuilder stringBuilder = new StringBuilder();
        // Convert the hashed bytes to a hexadecimal string
        for (byte b : hashedBytes) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }
}
