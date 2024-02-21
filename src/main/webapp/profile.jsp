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
        #profile-img {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Welcome to Your Profile, <%= request.getAttribute("userName") %>!</h1>
    <img id="profile-img" src="<%= request.getAttribute("profileImage") %>" alt="Profile Image">
    <br>
    <p>Edit Bio: <textarea rows="4" cols="50" name="bio" disabled><%= request.getAttribute("userBio") %></textarea></p>
    <p>Edit Address: <input type="text" name="address" value="<%= request.getAttribute("userAddress") %>" disabled></p>

    <!-- Add more profile information and edit options as needed -->
    
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
</div>

</body>
</html>
