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
        .profile-image {
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
    <img class="profile-image" src="<%= request.getAttribute("profileImage") %>" alt="Profile Image">
    <div class="user-details">
        <p><strong>Email:</strong> <%= request.getAttribute("userEmail") %></p>
        <p><strong>Mobile:</strong> <%= request.getAttribute("userMobile") %></p>
        <p class="bio"><strong>Bio:</strong> <%= request.getAttribute("userBio") %></p>
        <p><strong>Address:</strong> <%= request.getAttribute("userAddress") %></p>
    </div>
</div>

</body>
</html>
