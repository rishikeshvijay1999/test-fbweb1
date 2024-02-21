import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private static final String JDBC_URL = "jdbc:mysql://192.168.138.114:3306/myDB";
    private static final String JDBC_USER = "mysql";
    private static final String JDBC_PASSWORD = "mysql";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                String name = request.getParameter("Name");
                String mobile = request.getParameter("mobile");
                String email = request.getParameter("email");
                String password = request.getParameter("psw");

                // Check if the email already exists in the database
                if (isEmailRegistered(connection, email)) {
                    out.println("Email already registered. Please choose another email.");
                    return;
                }

                // Process the uploaded profile image
                Part filePart = request.getPart("profileImage");
                String fileName = getFileName(filePart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File fileUploadDirectory = new File(uploadPath);
                if (!fileUploadDirectory.exists()) {
                    fileUploadDirectory.mkdirs();
                }
                String filePath = uploadPath + File.separator + fileName;
                filePart.write(filePath);

                String hashedPassword = hashPassword(password);

                String sql = "INSERT INTO web (name, mobile, email, password, profile_image) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, mobile);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, hashedPassword);
                    preparedStatement.setString(5, filePath);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        out.println("User registered successfully!");
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

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
