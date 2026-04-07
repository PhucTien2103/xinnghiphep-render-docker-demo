<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Super Admin Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-theme.css">
</head>
<body class="dashboard-page">
	<div class="dashboard-shell">
		<aside class="dashboard-sidebar">
			<div class="dashboard-logo">XinNghiPhep</div>
			<p class="dashboard-role">SUPER ADMIN</p>
			<p class="menu-section-title">Overview</p>
			<ul class="sidebar-menu">
				<li><a class="active-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
			</ul>
			<p class="menu-section-title">Management</p>
			<ul class="sidebar-menu">
				<li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
				<li><a href="${pageContext.request.contextPath}/admin/leave-types">Manage Leave Types</a></li>
				<li><a href="${pageContext.request.contextPath}/admin/leave-statistics">Leave Statistics</a></li>
			</ul>
			<p class="menu-section-title">Workspace</p>
			<ul class="sidebar-menu">
				<li><a href="${pageContext.request.contextPath}/leave/list">Leave Requests</a></li>
				<li><a href="${pageContext.request.contextPath}/notifications">Notifications</a></li>
				<li><a href="${pageContext.request.contextPath}/my-profile">My Profile</a></li>
			</ul>
		</aside>

		<main class="dashboard-main">
			<header class="topbar">
				<h1>Super Admin Dashboard</h1>
				<p>Quan sat he thong nghi phep va cac thao tac quan tri quan trong.</p>
			</header>

			<section class="quick-grid">
				<article class="quick-card">
					<h3>User Management</h3>
					<p>Quan ly tai khoan va phan quyen cho nguoi dung.</p>
					<a href="${pageContext.request.contextPath}/admin/users">Mo danh sach</a>
				</article>
				<article class="quick-card">
					<h3>Leave Types</h3>
					<p>Cap nhat loai nghi va dinh nghia quy tac su dung.</p>
					<a href="${pageContext.request.contextPath}/admin/leave-types">Quan ly loai nghi</a>
				</article>
				<article class="quick-card">
					<h3>Notifications</h3>
					<p>Theo doi thong bao he thong va thong tin can xu ly.</p>
					<a href="${pageContext.request.contextPath}/notifications">Xem thong bao</a>
				</article>
			</section>

			<jsp:include page="/view/dashboard/summary-admin-manager.jsp" />
		</main>
	</div>
</body>
</html>