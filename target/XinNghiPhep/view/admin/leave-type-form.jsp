<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
<meta charset="UTF-8">
<title>Leave type</title>
</head>
<body class="app-page">
<main class="page-card">
	<h2><c:choose><c:when test="${not empty leaveType}">Edit</c:when><c:otherwise>Add</c:otherwise></c:choose> leave type</h2>

	<p><a href="${pageContext.request.contextPath}/admin/leave-types">Back to list</a></p>

	<c:choose>
		<c:when test="${not empty leaveType}">
			<form method="post" action="${pageContext.request.contextPath}/admin/leave-types/update">
				<input type="hidden" name="id" value="${leaveType.id}" />
				<p>Code: <strong><c:out value="${leaveType.code}" /></strong> (cannot be changed)</p>
				<p>Name: <input type="text" name="name" value="${leaveType.name}" required /></p>
				<p>
					<label>
						<input type="checkbox" name="consumesBalance" ${leaveType.consumesBalance ? 'checked' : ''} />
						Consumes annual leave balance
					</label>
				</p>
				<p>
					Default days / year:
					<input type="number" name="defaultDaysPerYear" min="0" value="${leaveType.defaultDaysPerYear}" />
					<small>(For code ANNUAL: new employee default and policy adjustments for all balances.)</small>
				</p>
				<p>
					<label>
						<input type="checkbox" name="active" ${leaveType.active ? 'checked' : ''} />
						Active (shown when creating requests)
					</label>
				</p>
				<button type="submit">Save</button>
			</form>
		</c:when>
		<c:otherwise>
			<form method="post" action="${pageContext.request.contextPath}/admin/leave-types/insert">
				<p>Code: <input type="text" name="code" required maxlength="50" placeholder="e.g. MATERNITY" /></p>
				<p>Name: <input type="text" name="name" required /></p>
				<p>
					<label>
						<input type="checkbox" name="consumesBalance" />
						Consumes annual leave balance
					</label>
				</p>
				<p>
					Default days / year: <input type="number" name="defaultDaysPerYear" min="0" value="0" />
				</p>
				<p>
					<label>
						<input type="checkbox" name="active" checked />
						Active
					</label>
				</p>
				<button type="submit">Create</button>
			</form>
		</c:otherwise>
	</c:choose>
</main>
</body>
</html>
