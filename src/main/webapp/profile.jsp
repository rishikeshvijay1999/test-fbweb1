<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.nio.file.StandardCopyOption" %>
<%@ page import="java.nio.file.Files" %>

<%@ page import="java.util.Collection" %>
<%@ page import="javax.servlet.annotation.MultipartConfig" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.nio.file.StandardCopyOption" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>

<html>
<head>
    <title>User Profile</title>
    <style>
        .container {
            width: 50%;
            margin: auto;
            text-align: center;
        }
        .success-message {
            color: green;
        }
        .error-message {
            color: red;
        }
        #profile-image {
            width: 200px; /* Adjust the width as needed */
            height: auto;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<%
    // Add this utility method
    String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "";
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        int userId = (int) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");

        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            if ("profileImage".equals(part.getName())) {
                String fileName = getFileName(part);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File fileUploadDirectory = new File(uploadPath);
                if (!fileUploadDirectory.exists()) {
                    fileUploadDirectory.mkdirs();
                }
                String filePath = uploadPath + File.separator + fileName;

                // Save the uploaded file to the server
                try (InputStream input = part.getInputStream()) {
                    Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                }

                // Display a message indicating successful image upload
                out.println("<div class=\"container\">");
                out.println("<h1 class=\"success-message\">Welcome, " + userName + "!</h1>");
                out.println("<p class=\"success-message\">Profile image uploaded successfully.</p>");
                out.println("<img id=\"profile-image\" src=\"" + filePath + "\" alt=\"Profile Image\">");
                out.println("</div>");
            }
        }
    }
%>

<form action="<%= request.getContextPath() %>/login" method="post" enctype="multipart/form-data">
    <div class="container">
        <h1>Upload Profile Image</h1>
        <p>Welcome, <%= request.getAttribute("userName") %>! Upload your profile image.</p>
        <label for="profileImage">Choose Profile Image:</label>
        <input type="file" id="profileImage" name="profileImage" accept="image/*" required>
        <br>
        <button type="submit">Upload</button>
    </div>
</form>

</body>
</html>
