<%-- view/manager/dashboard.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manager Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
</head>
<body class="dashboard-page">
	<div class="dashboard-shell">
		<aside class="dashboard-sidebar">
			<div class="dashboard-logo">XinNghiPhep</div>
			<p class="dashboard-role">MANAGER</p>
			<p class="menu-section-title">Overview</p>
			<ul class="sidebar-menu">
				<li><a class="active-link" href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
			</ul>
			<p class="menu-section-title">Team</p>
			<ul class="sidebar-menu">
				<li><a href="${pageContext.request.contextPath}/manager/zemployeez">View Employees</a></li>
				<li><a href="${pageContext.request.contextPath}/manager/status">Leave Status</a></li>
				<li><a href="${pageContext.request.contextPath}/manager/balance">Leave Balance</a></li>
			</ul>
			<p class="menu-section-title">Workspace</p>
			<ul class="sidebar-menu">
				<li><a href="${pageContext.request.contextPath}/leave/create">Request Leave</a></li>
				<li><a href="${pageContext.request.contextPath}/leave/list">Leave Requests</a></li>
				<li><a href="${pageContext.request.contextPath}/notifications">Notifications</a></li>
				<li><a href="${pageContext.request.contextPath}/my-profile">My Profile</a></li>
			</ul>
		</aside>

		<main class="dashboard-main">
			<header class="topbar">
				<h1>Manager Dashboard</h1>
				<p>Theo doi tinh hinh nghi phep cua doi nhom va xu ly cong viec nhanh.</p>
			</header>

			<section class="quick-grid">
				<article class="quick-card">
					<h3>Employee Directory</h3>
					<p>Mo danh sach nhan su de cap nhat thong tin va kiem tra trang thai.</p>
					<a href="${pageContext.request.contextPath}/manager/zemployeez">Mo danh sach</a>
				</article>
				<article class="quick-card">
					<h3>Request Leave</h3>
					<p>Tao don xin nghi moi hoac cap nhat yeu cau nhanh.</p>
					<a href="${pageContext.request.contextPath}/leave/create">Tao don</a>
				</article>
				<article class="quick-card">
					<h3>Team Notifications</h3>
					<p>Xem thong bao moi va theo doi cac thay doi quan trong.</p>
					<a href="${pageContext.request.contextPath}/notifications">Xem thong bao</a>
				</article>
			</section>

			<jsp:include page="/view/dashboard/summary-admin-manager.jsp" />

			<c:if test="${message != null}">
				<div class="flash-message">${message}</div>
				<c:remove var="message" scope="session" />
			</c:if>
		</main>
	</div>
</body>
</html>
