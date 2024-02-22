import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.*;
import org.mockito.Mockito;

public class MainServletTest {

    @Test
    public void testDoPost() throws ServletException, IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());

        Mockito.when(response.getWriter()).thenReturn(writer);

        // Test case 1: Valid input
        Mockito.when(request.getParameter("Name")).thenReturn("John Doe");
        Mockito.when(request.getParameter("mobile")).thenReturn("1234567890");
        Mockito.when(request.getParameter("email")).thenReturn("john.doe@example.com");
        Mockito.when(request.getParameter("psw")).thenReturn("password");
        Mockito.when(request.getParameter("bio")).thenReturn("A short bio");
        Mockito.when(request.getParameter("address")).thenReturn("123 Main St");

        new MainServlet().doPost(request, response);

        writer.flush();
        String result = new String(writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8));
        assertTrue(result.contains("User registered successfully!"));

        // Test case 2: Invalid name (contains a number)
        Mockito.when(request.getParameter("Name")).thenReturn("John Doe123");
        new MainServlet().doPost(request, response);

        writer.flush();
        result = new String(writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8));
        assertTrue(result.contains("Invalid input for Name"));

        // Test case 3: Invalid mobile number (not 10 digits)
        Mockito.when(request.getParameter("Name")).thenReturn("John Doe");
        Mockito.when(request.getParameter("mobile")).thenReturn("123456789");
        new MainServlet().doPost(request, response);

        writer.flush();
        result = new String(writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8));
        assertTrue(result.contains("Invalid input for Mobile"));

        // Test case 4: Invalid password (less than 8 characters)
        Mockito.when(request.getParameter("mobile")).thenReturn("1234567890");
        Mockito.when(request.getParameter("psw")).thenReturn("pass");
        new MainServlet().doPost(request, response);

        writer.flush();
        result = new String(writer.getBuffer().toString().getBytes(StandardCharsets.UTF_8));
        assertTrue(result.contains("Invalid input for Password"));
    }
}
