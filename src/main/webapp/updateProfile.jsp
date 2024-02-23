<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Update Profile - Facebook Lite</title>
    <style>
        .container {
            width: 50%;
            margin: auto;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Update Your Profile</h1>
    <h1>This page is Inprogress</h1>
    <form action="<%= request.getContextPath() %>/updateProfile?userId=<%= request.getAttribute("userId") %>" method="post">
        <label for="name"><b>Name:</b></label>
        <input type="text" placeholder="Enter Name" name="name" value="<%= request.getAttribute("userName") %>">
        <br>

        <label for="mobile"><b>Mobile:</b></label>
        <input type="text" placeholder="Enter Mobile" name="mobile" value="<%= request.getAttribute("userMobile") %>">
        <br>

        <label for="bio"><b>Bio:</b></label>
        <input type="text" placeholder="Enter Bio" name="bio" value="<%= request.getAttribute("userBio") %>">
        <br>

        <label for="address"><b>Address:</b></label>
        <input type="text" placeholder="Enter Address" name="address" value="<%= request.getAttribute("userAddress") %>">
        <br>

        <button type="submit">Update Profile</button>
    </form>
</div>

</body>
</html>
