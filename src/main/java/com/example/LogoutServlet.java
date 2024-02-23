import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    // Handling HTTP POST requests for logout
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve the HttpSession
        HttpSession session = request.getSession();
        
        // Invalidate the session to logout the user (clears user-related data)
        session.invalidate();

        // Redirect the user to the login.jsp page after logout
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
