import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://192.168.75.158:3306/mydb";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql@123";

    // Declare the connection variable at the class level
    private Connection connection;

    // Initialize the connection in the init() method
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Handling HTTP POST requests for login
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // PrintWriter to send response back to the client
        PrintWriter out = response.getWriter();

        // Retrieve email and password from request parameters
        String email = request.getParameter("email");
        String password = request.getParameter("psw");

        try {
            // SQL query to check if the email and hashed password match in the 'web' table
            String sql = "SELECT * FROM web WHERE LOWER(email) = LOWER(?) AND password=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, hashPassword(password));

                // Execute the SQL query
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Set user attributes for the profile.jsp
                        request.setAttribute("userName", resultSet.getString("name"));
                        request.setAttribute("userEmail", resultSet.getString("email"));
                        request.setAttribute("userMobile", resultSet.getString("mobile"));

                        // Retrieve additional details from the user_details table
                        retrieveAdditionalDetails(request, resultSet.getInt("id"));

                        // Forward to profile.jsp
                        request.getRequestDispatcher("/profile.jsp").forward(request, response);
                    } else {
                        out.println("Invalid email or password. Please try again.");
                        System.out.println("Login failed for email: " + email);
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            // Handle exceptions (e.g., SQLException, NoSuchAlgorithmException)
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    // Method to retrieve additional details from the 'user_details' table
    private void retrieveAdditionalDetails(HttpServletRequest request, int userId) throws SQLException {
        String sql = "SELECT * FROM user_details WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            // Execute the SQL query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    request.setAttribute("userBio", resultSet.getString("bio"));
                    request.setAttribute("userAddress", resultSet.getString("address"));
                }
            }
        }
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

    // Close the connection in the destroy() method
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
