<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <link rel="stylesheet" href="styles/register.css" type="text/css"/>
</head>
<body>

    <h1>Registration Form</h1>
    <p>To register for our shopping, enter your name and email address below.</p> <br>
<form action="cart" method="post">
    <input type="hidden" name="action" value="registerUser">
    <label class="pad_top">Email:</label>
    <input type="email" name="email" value="${user.email}"><br>
    <label class="pad_top">First Name:</label>
    <input type="text" name="firstName" value="${user.firstName}"><br>
    <label class="pad_top">Last Name:</label>
    <input type="text" name="lastName" value="${user.lastName}"><br>
    <label>&nbsp;</label>
    <input type="submit" value="Register" class="margin_left">
</form>
</body>
</html>
