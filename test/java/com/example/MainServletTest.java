import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MainServletTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private MainServlet mainServlet;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    public void testDoPost() throws IOException, ServletException, SQLException {
        // Set up your test data
        when(mockRequest.getParameter("Name")).thenReturn("John");
        when(mockRequest.getParameter("mobile")).thenReturn("123456789");
        when(mockRequest.getParameter("email")).thenReturn("john@example.com");
        when(mockRequest.getParameter("psw")).thenReturn("password");
        when(mockRequest.getParameter("bio")).thenReturn("Some bio");
        when(mockRequest.getParameter("address")).thenReturn("Some address");

        when(mockResultSet.next()).thenReturn(false); // Email is not registered

        // Capture the output to check the response
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(mockResponse.getWriter()).thenReturn(writer);

        // Call the doPost method
        mainServlet.doPost(mockRequest, mockResponse);

        // Verify that the expected message is printed
        writer.flush();
        String output = stringWriter.toString().trim();
        assertTrue(output.contains("User registered successfully!"));

        // Verify that the expected SQL statements were executed
        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement, times(1)).getGeneratedKeys();
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(3)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStatement, times(1)).executeUpdate();

        // You can also add more assertions based on your specific requirements and test cases
    }
}
