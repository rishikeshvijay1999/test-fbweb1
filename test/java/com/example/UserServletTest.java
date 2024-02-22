import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UserServletTest {
    // Use mock objects for HttpServletRequest and HttpServletResponse
    private static HttpServletRequest request;
    private static HttpServletResponse response;

    // Use a mock Connection for testing database interactions
    private static Connection mockConnection;

    @BeforeAll
    static void setUp() throws ServletException, IOException, SQLException {
        // Initialize mock objects and connection before the tests
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        // Set up the mock connection and inject it into the servlets
        mockConnection = Mockito.mock(Connection.class);

        // Initialize the servlets with the mock connection
        MainServlet mainServlet = new MainServlet() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };
        LoginServlet loginServlet = new LoginServlet() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };

        // Set up necessary tables and data in the mock database
        createTestTableAndData();
    }

    @Test
    void testSuccessfulRegistrationAndLogin() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        // Test successful registration
        Mockito.when(request.getParameter("Name")).thenReturn("John Doe");
        Mockito.when(request.getParameter("mobile")).thenReturn("1234567890");
        Mockito.when(request.getParameter("email")).thenReturn("john.doe@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password123");
        Mockito.when(request.getParameter("psw-repeat")).thenReturn("password123");
        Mockito.when(request.getParameter("bio")).thenReturn("Some bio information");
        Mockito.when(request.getParameter("address")).thenReturn("123 Main Street");

        // Mocking the PrintWriter for capturing the servlet response
        HttpServletResponse registrationResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(registrationResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new MainServlet().doPost(request, registrationResponse);

        // Test successful login after registration
        Mockito.when(request.getParameter("email")).thenReturn("john.doe@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password123");

        // Mocking the PrintWriter for capturing the servlet response
        HttpServletResponse loginResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(loginResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new LoginServlet().doPost(request, loginResponse);

        // Verify that the user is redirected to the profile page
        Mockito.verify(request, Mockito.atLeastOnce()).getRequestDispatcher("/profile.jsp");
    }

    @Test
    void testUnsuccessfulLogin() throws ServletException, IOException, SQLException, NoSuchAlgorithmException {
        // Test unsuccessful login with incorrect password
        Mockito.when(request.getParameter("email")).thenReturn("john.doe@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("wrongpassword");

        // Mocking the PrintWriter for capturing the servlet response
        HttpServletResponse loginResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(loginResponse.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));

        new LoginServlet().doPost(request, loginResponse);

        // Verify that the servlet prints the correct error message
        Mockito.verify(loginResponse.getWriter()).println("Invalid email or password. Please try again.");
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Clean up resources after all tests
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
