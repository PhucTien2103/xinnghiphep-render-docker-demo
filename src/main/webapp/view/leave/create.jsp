<%-- /view/leave/create.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
<meta charset="UTF-8">
<title>Create Leave Request</title>
</head>
<body class="app-page">
<main class="page-card">
	<h2>Create Leave Request</h2>
	<c:if test="${not empty leaveBalance}">
		<p>Remaining leave days: <strong>${leaveBalance.remainingDays}</strong> / ${leaveBalance.totalDays}</p>
	</c:if>

	<c:if test="${alert != null}">
		<p style="color: red">${alert}</p>
	</c:if>

	<form method="post"
		action="${pageContext.request.contextPath}/leave/insert">

		Leave type:
		<select name="leaveTypeId" required>
			<option value="">-- Chọn --</option>
			<c:forEach var="t" items="${leaveTypes}">
				<option value="${t.id}"><c:out value="${t.name}" /> (${t.code})</option>
			</c:forEach>
		</select>
		<br /><br />

		Start Date: <input type="date" name="startDate" /> <br />
		<br /> End Date: <input type="date" name="endDate" /> <br />
		<br /> Reason:
		<textarea name="reason"></textarea>
		<br />
		<br />

		<button type="submit">Submit</button>
	</form>
</main>
</body>
</html>