import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class UserServletTest {

    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static Connection mockConnection;

    @BeforeAll
    static void setUp() throws ServletException, IOException, SQLException {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        mockConnection = Mockito.mock(Connection.class);

        MainServlet mainServlet = new MainServlet() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };

        createTestTableAndData();
    }

    @Test
    void testInvalidName() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        Mockito.when(request.getParameter("Name")).thenReturn("John123");
        Mockito.when(request.getParameter("mobile")).thenReturn("9876543210");
        Mockito.when(request.getParameter("email")).thenReturn("john123@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password123");
        Mockito.when(request.getParameter("psw-repeat")).thenReturn("password123");

        HttpServletResponse registrationResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(registrationResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new MainServlet().doPost(request, registrationResponse);

        Mockito.verify(registrationResponse.getWriter()).println("Name should only contain letters.");
    }

    @Test
    void testInvalidMobileNumber() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        Mockito.when(request.getParameter("Name")).thenReturn("Jane Doe");
        Mockito.when(request.getParameter("mobile")).thenReturn("123abc456");
        Mockito.when(request.getParameter("email")).thenReturn("jane.doe@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password456");
        Mockito.when(request.getParameter("psw-repeat")).thenReturn("password456");

        HttpServletResponse registrationResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(registrationResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new MainServlet().doPost(request, registrationResponse);

        Mockito.verify(registrationResponse.getWriter()).println("Mobile number should only contain digits.");
    }

    @Test
    void testInvalidPasswordLength() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        Mockito.when(request.getParameter("Name")).thenReturn("Alex Smith");
        Mockito.when(request.getParameter("mobile")).thenReturn("9876543210");
        Mockito.when(request.getParameter("email")).thenReturn("alex.smith@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("pass");
        Mockito.when(request.getParameter("psw-repeat")).thenReturn("pass");

        HttpServletResponse registrationResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(registrationResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new MainServlet().doPost(request, registrationResponse);

        Mockito.verify(registrationResponse.getWriter()).println("Password should contain at least 8 characters.");
    }

    @Test
    void testMismatchedPasswords() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        Mockito.when(request.getParameter("Name")).thenReturn("Mary Johnson");
        Mockito.when(request.getParameter("mobile")).thenReturn("1234567890");
        Mockito.when(request.getParameter("email")).thenReturn("mary.johnson@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password789");
        Mockito.when(request.getParameter("psw-repeat")).thenReturn("differentpassword");

        HttpServletResponse registrationResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(registrationResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new MainServlet().doPost(request, registrationResponse);

        Mockito.verify(registrationResponse.getWriter()).println("Passwords do not match.");
    }

    @AfterAll
    static void tearDown() throws SQLException {
        dropTestTable();
        if (mockConnection != null && !mockConnection.isClosed()) {
            mockConnection.close();
        }
    }

    private static void createTestTableAndData() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS web (id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255), mobile VARCHAR(255), email VARCHAR(255), password VARCHAR(255));";

        String insertTestData = "INSERT INTO web (name, mobile, email, password) VALUES ('John Doe', '1234567890', 'john.doe@example.com', 'password123');";

        try (PreparedStatement createTableStatement = mockConnection.prepareStatement(createTableSQL)) {
            createTableStatement.executeUpdate();
        }

        try (PreparedStatement insertTestDataStatement = mockConnection.prepareStatement(insertTestData)) {
            insertTestDataStatement.executeUpdate();
        }
    }

    private static void dropTestTable() throws SQLException {
        String dropTableSQL = "DROP TABLE IF EXISTS web;";

        try (PreparedStatement dropTableStatement = mockConnection.prepareStatement(dropTableSQL)) {
            dropTableStatement.executeUpdate();
        }
    }
}

