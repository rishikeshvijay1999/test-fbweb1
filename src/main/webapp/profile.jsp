<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        // Process image upload after login
        int userId = (int) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");

        Part filePart = request.getPart("profileImage");
        String fileName = getFileName(filePart);
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File fileUploadDirectory = new File(uploadPath);
        if (!fileUploadDirectory.exists()) {
            fileUploadDirectory.mkdirs();
        }
        String filePath = uploadPath + File.separator + fileName;
        filePart.write(filePath);

        // Display a message indicating successful image upload
        out.println("<div class=\"container\">");
        out.println("<h1 class=\"success-message\">Welcome, " + userName + "!</h1>");
        out.println("<p class=\"success-message\">Profile image uploaded successfully.</p>");
        out.println("<img id=\"profile-image\" src=\"" + filePath + "\" alt=\"Profile Image\">");
        out.println("</div>");
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
