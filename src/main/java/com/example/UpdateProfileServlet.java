import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/updateProfile")
public class UpdateProfileServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://192.168.138.114:3306/myDB";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql";

    // Handling HTTP GET requests for accessing the update profile form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve user ID from the session
        int userId = getUserIdFromSession(request);

        // Retrieve user details for the specified user ID
        retrieveUserDetails(request, userId);

        // Forward the request to the updateProfile.jsp page
        request.getRequestDispatcher("/updateProfile.jsp").forward(request, response);
    }

    // Handling HTTP POST requests for submitting the updated profile form
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a database connection
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                // Retrieve user ID from the session
                int userId = getUserIdFromSession(request);

                // Retrieve updated details from the form parameters
                String name = request.getParameter("name");
                String mobile = request.getParameter("mobile");
                String bio = request.getParameter("bio");
                String address = request.getParameter("address");

                // Update the user profile in the database
                updateProfile(connection, userId, name, mobile, bio, address);

                // Redirect to the updated profile.jsp
                response.sendRedirect(request.getContextPath() + "/profile.jsp");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    // Retrieve user ID from the session (to be implemented based on your session management)
    private int getUserIdFromSession(HttpServletRequest request) {
        // Implement the logic to retrieve the user ID from the session
        // This might involve using HttpSession and extracting the user ID attribute
        // For simplicity, let's assume you have a method like getUserIDFromSession(request)
        // that returns the user ID.
        return 1; // Replace with your actual logic to get the user ID from the session.
    }

    // Update the user profile in the database
    private void updateProfile(Connection connection, int userId, String name, String mobile, String bio, String address)
            throws SQLException {
        // SQL query to update the 'web' table with new name and mobile
        String updateProfileSql = "UPDATE web SET name=?, mobile=? WHERE id=?";
        try (PreparedStatement updateProfileStatement = connection.prepareStatement(updateProfileSql)) {
            updateProfileStatement.setString(1, name);
            updateProfileStatement.setString(2, mobile);
            updateProfileStatement.setInt(3, userId);

            // Execute the update query
            updateProfileStatement.executeUpdate();
        }

        // SQL query to update the 'user_details' table with new bio and address
        String updateUserDetailsSql = "UPDATE user_details SET bio=?, address=? WHERE user_id=?";
        try (PreparedStatement updateUserDetailsStatement = connection.prepareStatement(updateUserDetailsSql)) {
            updateUserDetailsStatement.setString(1, bio);
            updateUserDetailsStatement.setString(2, address);
            updateUserDetailsStatement.setInt(3, userId);

            // Execute the update query
            updateUserDetailsStatement.executeUpdate();
        }
    }

    // Retrieve user details from the database based on the user ID
    private void retrieveUserDetails(HttpServletRequest request, int userId) {
        // SQL query to select user details from the 'web' table
        String userSql = "SELECT * FROM web WHERE id=?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement userStatement = connection.prepareStatement(userSql)) {

            // Set the user ID parameter in the query
            userStatement.setInt(1, userId);

            // Execute the query
            try (ResultSet userResultSet = userStatement.executeQuery()) {
                if (userResultSet.next()) {
                    // Retrieve user details from the 'web' table
                    String userName = userResultSet.getString("name");
                    String userMobile = userResultSet.getString("mobile");

                    // Set user details as request attributes
                    request.setAttribute("userName", userName);
                    request.setAttribute("userMobile", userMobile);
                }
            }

            // SQL query to select additional details from the 'user_details' table
            String userDetailsSql = "SELECT * FROM user_details WHERE user_id=?";
            try (PreparedStatement userDetailsStatement = connection.prepareStatement(userDetailsSql)) {
                // Set the user ID parameter in the query
                userDetailsStatement.setInt(1, userId);

                // Execute the query
                try (ResultSet userDetailsResultSet = userDetailsStatement.executeQuery()) {
                    if (userDetailsResultSet.next()) {
                        // Retrieve additional details from the 'user_details' table
                        String userBio = userDetailsResultSet.getString("bio");
                        String userAddress = userDetailsResultSet.getString("address");

                        // Set additional details as request attributes
                        request.setAttribute("userBio", userBio);
                        request.setAttribute("userAddress", userAddress);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
