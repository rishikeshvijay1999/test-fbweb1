import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                int userId = getUserIdFromSession(request);

                String name = request.getParameter("name");
                String mobile = request.getParameter("mobile");
                String bio = request.getParameter("bio");
                String address = request.getParameter("address");

                updateProfile(connection, userId, name, mobile, bio, address);

                // Redirect to the updated profile.jsp
                response.sendRedirect(request.getContextPath() + "/profile.jsp");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    private int getUserIdFromSession(HttpServletRequest request) {
        // Implement the logic to retrieve the user ID from the session
        // This might involve using HttpSession and extracting the user ID attribute
        // For simplicity, let's assume you have a method like getUserIDFromSession(request)
        // that returns the user ID.
        return 1; // Replace with your actual logic to get the user ID from the session.
    }

    private void updateProfile(Connection connection, int userId, String name, String mobile, String bio, String address)
            throws SQLException {
        String updateProfileSql = "UPDATE web SET name=?, mobile=? WHERE id=?";
        try (PreparedStatement updateProfileStatement = connection.prepareStatement(updateProfileSql)) {
            updateProfileStatement.setString(1, name);
            updateProfileStatement.setString(2, mobile);
            updateProfileStatement.setInt(3, userId);

            updateProfileStatement.executeUpdate();
        }

        String updateUserDetailsSql = "UPDATE user_details SET bio=?, address=? WHERE user_id=?";
        try (PreparedStatement updateUserDetailsStatement = connection.prepareStatement(updateUserDetailsSql)) {
            updateUserDetailsStatement.setString(1, bio);
            updateUserDetailsStatement.setString(2, address);
            updateUserDetailsStatement.setInt(3, userId);

            updateUserDetailsStatement.executeUpdate();
        }
    }
}
