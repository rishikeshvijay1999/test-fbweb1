import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Read content of index.jsp
        String jspContent = getServletContext().getResource("/index.jsp").getContent().toString();

        // Check if "facebook" is present
        if (jspContent.contains("facebook")) {
            out.print("Build failed! 'facebook' found in index.jsp.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            out.print("Build successful! 'Facebook' not found in index.jsp.");
        }
        out.close();
    }
}
