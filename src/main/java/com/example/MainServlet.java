@WebServlet("/main")
public class MainServlet extends HttpServlet {
    // ... existing code ...

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
}
