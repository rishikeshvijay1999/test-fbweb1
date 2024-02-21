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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/login")
@MultipartConfig
public class LoginServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://your_database_host:3306/your_database_name";
    private static final String JDBC_USER = "your_database_user";
    private static final String JDBC_PASSWORD = "your_database_password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("psw");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                String sql = "SELECT * FROM web WHERE LOWER(email) = LOWER(?) AND password=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, hashPassword(password));

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Set user attributes for the profile.jsp
                            request.setAttribute("userName", resultSet.getString("name"));
                            request.setAttribute("userId", resultSet.getInt("id"));

                            // Forward to profile.jsp
                            request.getRequestDispatcher("/profile.jsp").forward(request, response);
                        } else {
                            out.println("Invalid email or password. Please try again.");
                            System.out.println("Login failed for email: " + email);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
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
}
