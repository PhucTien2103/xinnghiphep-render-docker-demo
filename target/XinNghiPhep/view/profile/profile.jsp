<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
    <title>Profile</title>
</head>
<body class="app-page">
<main class="page-card">

<h2>Profile</h2>

<c:if test="${param.success == 1}">
    <p style="color:green">Update thành công!</p>
</c:if>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/update-profile" method="post" enctype="multipart/form-data">

    <br>
    <c:choose>
        <c:when test="${not empty user.avatarUrl}">
            <img src="${user.avatarUrl}" alt="Avatar" width="120" height="120" style="object-fit:cover;border-radius:50%;" />
        </c:when>
        <c:otherwise>
            <img src="https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg" alt="Avatar mac dinh" width="120" height="120" style="object-fit:cover;border-radius:50%;opacity:0.6;" />
        </c:otherwise>
    </c:choose>
    <br><br>

    Username: <b>${user.username}</b><br><br>

    Full Name:
    <input type="text" name="fullName" value="${user.fullName}" />
    <br><br>

    Email:
    <input type="text" name="email" value="${user.email}" />
    <br><br>

    Avatar:
    <input type="file" name="avatar" accept="image/*" />
    <small>(Tối đa 5MB)</small>
    <br><br>

    <button type="submit">Update</button>

</form>

<hr/>
<c:if test="${sessionScope.account.id == user.id}">
    <p>
        <a href="${pageContext.request.contextPath}/change-password">Change password</a>
    </p>
</c:if>

</main>
</body>
</html>