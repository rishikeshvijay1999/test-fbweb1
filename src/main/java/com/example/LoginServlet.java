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
    private static final String JDBC_URL = "jdbc:mysql://192.168.138.114:3306/myDB";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql";
    
    // Declare the connection variable at the class level
    private Connection connection;

    public void init() throws ServletException {
        // Initialize the connection in the init() method
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("psw");

        try {
            String sql = "SELECT * FROM web WHERE LOWER(email) = LOWER(?) AND password=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, hashPassword(password));

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
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private void retrieveAdditionalDetails(HttpServletRequest request, int userId) throws SQLException {
        String sql = "SELECT * FROM user_details WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    request.setAttribute("userBio", resultSet.getString("bio"));
                    request.setAttribute("userAddress", resultSet.getString("address"));
                }
            }
        }
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

    public void destroy() {
        // Close the connection in the destroy() method
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
