<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<section class="summary-panel">
	<h3>Tong quan cua ban</h3>

	<c:if test="${not empty dashSummaryError}">
		<p class="text-danger">${dashSummaryError}</p>
	</c:if>

	<div class="summary-metric-row">
		<div class="metric-card">
			<div class="metric-label">Phep nam con lai</div>
			<div class="metric-value">${empAnnualRemaining}</div>
		</div>
		<div class="metric-card">
			<div class="metric-label">Don cho duyet</div>
			<div class="metric-value">${empPendingCount}</div>
		</div>
		<div class="metric-card">
			<div class="metric-label">Dong nghiep dang nghi hom nay</div>
			<div class="metric-value">${empTeamOutToday}</div>
			<c:if test="${not empty empTeamOutNames}">
				<div class="metric-label" style="margin-top: 6px;">
					<c:forEach var="nm" items="${empTeamOutNames}" varStatus="st">
						<c:if test="${st.index < 8}">
							<c:if test="${st.index > 0}">, </c:if>
							<c:out value="${nm}" />
						</c:if>
					</c:forEach>
					<c:if test="${fn:length(empTeamOutNames) > 8}"> …</c:if>
				</div>
			</c:if>
		</div>
		<div class="metric-card" style="min-width: 240px;">
			<div class="metric-label">Lan nghi gan nhat</div>
			<div><strong>${empLastLeavePeriod}</strong> — <strong>${empLastLeaveDays}</strong> ngày</div>
			<div class="metric-label" style="margin-top: 4px;"><c:out value="${empLastLeaveReason}" /></div>
		</div>
	</div>

	<div class="chart-grid" style="align-items: flex-start;">
		<div class="chart-card" style="max-width: 360px;">
			<h4>Quy phep nam</h4>
			<canvas id="empBalanceChart" width="260" height="260"></canvas>
			<p class="metric-label">Tong: ${empAnnualTotal} &nbsp;|&nbsp; Da dung: ${empAnnualUsed}</p>
		</div>
		<div class="chart-card">
			<h4>Don gan day</h4>
			<table class="table-shell">
				<tr>
					<th>Loại nghỉ</th>
					<th>Thời gian</th>
					<th>Số ngày</th>
					<th>Trạng thái</th>
				</tr>
				<c:forEach var="row" items="${empRecentRows}">
					<tr>
						<td><c:out value="${row['type']}" /></td>
						<td><c:out value="${row['period']}" /></td>
						<td>${row['duration']}</td>
						<td>${row['status']}</td>
					</tr>
				</c:forEach>
				<c:if test="${empty empRecentRows}">
					<tr><td colspan="4">Chưa có đơn nghỉ.</td></tr>
				</c:if>
			</table>
		</div>
	</div>

	<h4 style="margin-top: 20px;">Thong bao gan day</h4>
	<ul class="notice-list">
		<c:forEach var="note" items="${notificationPreview}">
			<li>
				<c:if test="${not note.read}"><strong>[Chưa đọc]</strong> </c:if>
				<c:out value="${note.content}" />
				<c:if test="${note.sentTime != null}">
					<small> — <fmt:formatDate value="${note.sentTime}" pattern="yyyy-MM-dd HH:mm" /></small>
				</c:if>
			</li>
		</c:forEach>
		<c:if test="${empty notificationPreview}">
			<li>Không có thông báo.</li>
		</c:if>
	</ul>
	<p><a class="summary-link" href="${pageContext.request.contextPath}/notifications">Xem tat ca thong bao</a></p>
</section>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
(function () {
	const used = ${empAnnualUsed};
	const rem = ${empAnnualRemaining};
	const ctx = document.getElementById('empBalanceChart');
	if (ctx && typeof Chart !== 'undefined') {
		new Chart(ctx, {
			type: 'doughnut',
			data: {
				labels: ['Đã dùng', 'Còn lại'],
				datasets: [{
					data: [used, rem],
					backgroundColor: ['#e74c3c', '#27ae60']
				}]
			},
			options: {
				responsive: true,
				plugins: { legend: { position: 'bottom' } }
			}
		});
	}
})();
</script>
