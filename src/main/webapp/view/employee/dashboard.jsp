<%-- view/employee/dashboard.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Employee Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
</head>
<body class="dashboard-page">
	<div class="dashboard-shell">
		<aside class="dashboard-sidebar">
			<div class="dashboard-logo">XinNghiPhep</div>
			<p class="dashboard-role">EMPLOYEE</p>
			<p class="menu-section-title">Overview</p>
			<ul class="sidebar-menu">
				<li><a class="active-link" href="${pageContext.request.contextPath}/employee/dashboard">Dashboard</a></li>
			</ul>
			<p class="menu-section-title">Workspace</p>
			<ul class="sidebar-menu">
				<li><a href="${pageContext.request.contextPath}/leave/create">Request Leave</a></li>
				<li><a href="${pageContext.request.contextPath}/employee/status">Leave Status</a></li>
				<li><a href="${pageContext.request.contextPath}/employee/balance">Leave Balance</a></li>
				<li><a href="${pageContext.request.contextPath}/notifications">Notifications</a></li>
				<li><a href="${pageContext.request.contextPath}/my-profile">My Profile</a></li>
			</ul>
		</aside>

		<main class="dashboard-main">
			<header class="topbar">
				<h1>Employee Dashboard</h1>
				<p>Theo doi trang thai don nghi va quan ly quy phep ca nhan.</p>
			</header>

			<section class="quick-grid">
				<article class="quick-card">
					<h3>Create Leave Request</h3>
					<p>Gui don nghi phep moi ngay tren he thong.</p>
					<a href="${pageContext.request.contextPath}/leave/create">Tao don</a>
				</article>
				<article class="quick-card">
					<h3>My Leave Status</h3>
					<p>Kiem tra tinh trang duyet cac don da gui.</p>
					<a href="${pageContext.request.contextPath}/employee/status">Xem trang thai</a>
				</article>
				<article class="quick-card">
					<h3>Profile & Notifications</h3>
					<p>Cap nhat thong tin ca nhan va thong bao moi nhat.</p>
					<a href="${pageContext.request.contextPath}/my-profile">Mo ho so</a>
				</article>
			</section>

			<jsp:include page="/view/dashboard/summary-employee.jsp" />

			<c:if test="${message != null}">
				<div class="flash-message">${message}</div>
				<c:remove var="message" scope="session" />
			</c:if>
		</main>
	</div>
</body>
</html>
