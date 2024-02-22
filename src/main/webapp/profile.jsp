<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Profile - Facebook Lite</title>
    <style>
        .container {
            width: 50%;
            margin: auto;
            text-align: center;
        }
        #profileImage {
            max-width: 200px;
            max-height: 200px;
            border-radius: 50%;
        }
        .user-details {
            margin-top: 20px;
            text-align: left;
        }
        .bio {
            font-style: italic;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Welcome, <%= request.getAttribute("userName") %>!</h1>
    <img id="profileImage" src="<%= request.getAttribute("profileImage") %>" alt="Profile Image">
    <div class="user-details">
        <p><strong>Email:</strong> <%= request.getAttribute("userEmail") %></p>
        <p><strong>Mobile:</strong> <%= request.getAttribute("userMobile") %></p>
        <p class="bio"><strong>Bio:</strong> <%= request.getAttribute("userBio") %></p>
        <p><strong>Address:</strong> <%= request.getAttribute("userAddress") %></p>
    </div>
    
    <!-- Add a link to update profile -->
    <a href="<%= request.getContextPath() %>/updateProfile">Update Profile</a>

    <!-- Logout button -->
    <form action="<%= request.getContextPath() %>/logout" method="post">
        <button type="submit">Logout</button>
    </form>
</div>

</body>
</html>
