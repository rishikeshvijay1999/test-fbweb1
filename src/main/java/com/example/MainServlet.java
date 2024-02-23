import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("Name");
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");
        String password = request.getParameter("psw");
        String confirmPassword = request.getParameter("psw-repeat");

        if (password.equals(confirmPassword)) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<div class=\"container\" id=\"loggedin-container\">");
            out.println("<h1 class=\"success-message\">Thank you, " + name + "! You are logged in.</h1>");
            out.println("<img src=\"path/to/your/image.jpg\" alt=\"Profile Image\">");
            out.println("</div>");
        } else {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<div class=\"container\">");
            out.println("<p class=\"error-message\">Passwords do not match. Please try again.</p>");
            out.println("</div>");
        }
    }
}
