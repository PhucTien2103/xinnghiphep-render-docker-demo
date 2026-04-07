<!-- src/main/webapp/view/home -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body class="app-page">
<main class="page-card">
	<h2>Trang chu</h2>

	<c:choose>

		<c:when test="${sessionScope.account != null}">
			<p>Chao ban, <strong>${sessionScope.account.fullName}</strong>.</p>
			<div class="toolbar">
				<a class="chip-link" href="${pageContext.request.contextPath}/${sessionScope.account.role == 'SUPER_ADMIN' ? 'admin' : (sessionScope.account.role == 'MANAGER' ? 'manager' : 'employee')}/dashboard">Vao dashboard</a>
				<a class="chip-link" href="${pageContext.request.contextPath}/my-profile">Ho so ca nhan</a>
				<a class="chip-link" href="${pageContext.request.contextPath}/logout">Dang xuat</a>
			</div>

		</c:when>

		<c:otherwise>
			<p>Ban chua dang nhap.</p>
			<div class="toolbar">
				<a class="chip-link" href="${pageContext.request.contextPath}/login">Dang nhap</a>
				<a class="chip-link" href="${pageContext.request.contextPath}/signup">Dang ky tai khoan</a>
			</div>

		</c:otherwise>

	</c:choose>

</main>
</body>
</html>