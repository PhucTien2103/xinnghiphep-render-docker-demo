<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
<meta charset="UTF-8">
<title>Manage Leave Types</title>
</head>
<body class="app-page">
<main class="page-card">
	<h2>Manage Leave Types</h2>

	<c:if test="${not empty sessionScope.message}">
		<p>${sessionScope.message}</p>
		<c:remove var="message" scope="session" />
	</c:if>

	<p>
		<a href="${pageContext.request.contextPath}/admin/dashboard">Admin dashboard</a> |
		<a href="${pageContext.request.contextPath}/admin/leave-types/add">Add leave type</a>
	</p>

	<table border="1" cellpadding="6">
		<tr>
			<th>Code</th>
			<th>Name</th>
			<th>Consumes balance</th>
			<th>Default days / year</th>
			<th>Active</th>
			<th></th>
		</tr>
		<c:forEach var="t" items="${list}">
			<tr>
				<td><c:out value="${t.code}" /></td>
				<td><c:out value="${t.name}" /></td>
				<td>${t.consumesBalance ? 'Yes' : 'No'}</td>
				<td>${t.defaultDaysPerYear}</td>
				<td>${t.active ? 'Yes' : 'No'}</td>
				<td>
					<a href="${pageContext.request.contextPath}/admin/leave-types/edit?id=${t.id}">Edit</a>
					<c:choose>
						<c:when test="${t.active}">
							| <span style="color:#888;cursor:help" title="Tắt Active trước; sau đó mới xóa được (và không được có đơn nào dùng loại này).">Delete</span>
						</c:when>
						<c:otherwise>
							|
							<a href="${pageContext.request.contextPath}/admin/leave-types/delete?id=${t.id}"
							   onclick="return confirm('Xóa loại nghỉ này? Chỉ được phép khi không còn đơn nào tham chiếu.');">Delete</a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</table>
</main>
</body>
</html>
