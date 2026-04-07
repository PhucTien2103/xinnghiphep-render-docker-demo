package nhom13.vn.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nhom13.vn.entity.LeaveBalance;
import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.Notification;
import nhom13.vn.entity.User;
import nhom13.vn.service.IDashboardSummaryService;
import nhom13.vn.service.ILeaveBalanceService;
import nhom13.vn.service.ILeaveRequestService;
import nhom13.vn.service.ILeaveTypeService;
import nhom13.vn.service.INotificationService;
import nhom13.vn.util.LeaveTypeDistributionUtil;

public class DashboardSummaryServiceImpl implements IDashboardSummaryService {

    private static final int TREND_WEEKS = 8;
    private static final int NOTIFICATION_PREVIEW = 8;
    private static final int RECENT_REQUESTS = 10;

    private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static DashboardSummaryServiceImpl instance;

    private final ILeaveRequestService leaveRequestService;
    private final INotificationService notificationService;
    private final ILeaveBalanceService leaveBalanceService;
    private final ILeaveTypeService leaveTypeService;

    private DashboardSummaryServiceImpl() {
        this.leaveRequestService = LeaveRequestServiceImpl.getInstance();
        this.notificationService = NotificationServiceImpl.getInstance();
        this.leaveBalanceService = LeaveBalanceServiceImpl.getInstance();
        this.leaveTypeService = LeaveTypeServiceImpl.getInstance();
    }

    public static DashboardSummaryServiceImpl getInstance() {
        if (instance == null) {
            instance = new DashboardSummaryServiceImpl();
        }
        return instance;
    }

    @Override
    public Map<String, Object> buildAdminManagerSummary(User user) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (user == null) {
            putEmptyAdminManager(m);
            return m;
        }
        String role = user.getRole();
        if (!"SUPER_ADMIN".equals(role) && !"MANAGER".equals(role)) {
            putEmptyAdminManager(m);
            return m;
        }

        try {
            List<LeaveRequest> all = leaveRequestService.getAllForViewer(user, null);
            if (all == null) {
                all = List.of();
            }

            m.put("summaryTotal", all.size());
            m.put("summaryPending", countStatus(all, "PENDING"));
            m.put("summaryApproved", countStatus(all, "APPROVED"));
            m.put("summaryRejected", countStatus(all, "REJECTED"));

            LocalDate today = LocalDate.now();
            List<String> trendLabels = new ArrayList<>(TREND_WEEKS);
            List<Integer> trendData = new ArrayList<>(TREND_WEEKS);
            for (int w = TREND_WEEKS - 1; w >= 0; w--) {
                LocalDate ref = today.minusWeeks(w);
                LocalDate mon = ref.with(DayOfWeek.MONDAY);
                LocalDate sun = mon.plusDays(6);
                trendLabels.add(mon.format(PERIOD_FMT) + "–" + sun.format(PERIOD_FMT));
                final LocalDate fMon = mon;
                final LocalDate fSun = sun;
                int c = (int) all.stream()
                        .filter(lr -> lr.getStartDate() != null)
                        .map(lr -> toLocalDate(lr.getStartDate()))
                        .filter(s -> !s.isBefore(fMon) && !s.isAfter(fSun))
                        .count();
                trendData.add(c);
            }
            m.put("chartTrendLabels", trendLabels);
            m.put("chartTrendData", trendData);

            LinkedHashMap<String, Integer> byType = LeaveTypeDistributionUtil.distribution(all, leaveTypeService);
            m.put("chartReasonLabels", new ArrayList<>(byType.keySet()));
            m.put("chartReasonData", new ArrayList<>(byType.values()));

            LocalDate weekStart = today.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<LeaveRequest> onLeave = leaveRequestService.findApprovedLeavesOverlapping(user, weekStart, weekEnd);
            m.put("onLeaveThisWeek", onLeave != null ? onLeave : List.of());

            List<Notification> notifications = notificationService.getByViewer(user);
            if (notifications == null) {
                notifications = List.of();
            }
            int n = Math.min(NOTIFICATION_PREVIEW, notifications.size());
            m.put("notificationPreview", notifications.subList(0, n));

            m.put("dashSummaryOk", Boolean.TRUE);
        } catch (Exception e) {
            putEmptyAdminManager(m);
            m.put("dashSummaryError", "Không thể tải thống kê tổng quan.");
        }
        return m;
    }

    @Override
    public Map<String, Object> buildEmployeeSummary(User user) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (user == null || !"EMPLOYEE".equals(user.getRole())) {
            putEmptyEmployee(m);
            return m;
        }

        try {
            LeaveBalance lb = leaveBalanceService.ensureDefaultForUser(user);
            int rem = lb != null ? lb.getRemainingDays() : 0;
            int used = lb != null ? lb.getUsedDays() : 0;
            int total = lb != null ? lb.getTotalDays() : 0;
            m.put("empAnnualRemaining", rem);
            m.put("empAnnualUsed", used);
            m.put("empAnnualTotal", total);

            List<LeaveRequest> mine = leaveRequestService.getByUserWithReview(user.getId(), null);
            if (mine == null) {
                mine = List.of();
            }

            m.put("empPendingCount", mine.stream().filter(lr -> "PENDING".equals(lr.getStatus())).count());

            LocalDate today = LocalDate.now();
            List<LeaveRequest> todayLeaves = leaveRequestService.findApprovedLeavesOverlapping(user, today, today);
            if (todayLeaves == null) {
                todayLeaves = List.of();
            }
            List<String> colleagueNames = new ArrayList<>();
            for (LeaveRequest lr : todayLeaves) {
                if (lr.getUser() == null || lr.getUser().getId() == user.getId()) {
                    continue;
                }
                String name = lr.getUser().getFullName();
                if (name == null || name.isBlank()) {
                    name = lr.getUser().getUsername();
                }
                colleagueNames.add(name);
            }
            m.put("empTeamOutToday", colleagueNames.size());
            m.put("empTeamOutNames", colleagueNames);

            LeaveRequest last = mine.stream()
                    .filter(lr -> lr.getStartDate() != null)
                    .max(Comparator.comparing(lr -> toLocalDate(lr.getStartDate())))
                    .orElse(null);
            if (last != null) {
                m.put("empLastLeaveDays", calculateInclusiveDays(last));
                m.put("empLastLeaveReason", last.getReason() != null ? last.getReason() : "—");
                m.put("empLastLeavePeriod", formatPeriod(last));
            } else {
                m.put("empLastLeaveDays", 0);
                m.put("empLastLeaveReason", "—");
                m.put("empLastLeavePeriod", "—");
            }

            List<LeaveRequest> recentSorted = mine.stream()
                    .sorted(this::compareStartDesc)
                    .limit(RECENT_REQUESTS)
                    .collect(Collectors.toList());
            List<Map<String, Object>> rows = new ArrayList<>();
            for (LeaveRequest lr : recentSorted) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("type", LeaveTypeDistributionUtil.displayName(lr.getLeaveType()));
                row.put("period", formatPeriod(lr));
                row.put("duration", calculateInclusiveDays(lr));
                row.put("status", lr.getStatus() != null ? lr.getStatus() : "—");
                rows.add(row);
            }
            m.put("empRecentRows", rows);

            List<Notification> notifications = notificationService.getByViewer(user);
            if (notifications == null) {
                notifications = List.of();
            }
            int nn = Math.min(NOTIFICATION_PREVIEW, notifications.size());
            m.put("notificationPreview", notifications.subList(0, nn));

            m.put("dashSummaryOk", Boolean.TRUE);
        } catch (Exception e) {
            putEmptyEmployee(m);
            m.put("dashSummaryError", "Không thể tải thống kê tổng quan.");
        }
        return m;
    }

    private static int countStatus(List<LeaveRequest> all, String status) {
        return (int) all.stream().filter(lr -> status.equals(lr.getStatus())).count();
    }

    private int compareStartDesc(LeaveRequest a, LeaveRequest b) {
        if (a.getStartDate() == null && b.getStartDate() == null) {
            return 0;
        }
        if (a.getStartDate() == null) {
            return 1;
        }
        if (b.getStartDate() == null) {
            return -1;
        }
        return toLocalDate(b.getStartDate()).compareTo(toLocalDate(a.getStartDate()));
    }

    private static String formatPeriod(LeaveRequest lr) {
        if (lr.getStartDate() == null || lr.getEndDate() == null) {
            return "—";
        }
        LocalDate s = toLocalDate(lr.getStartDate());
        LocalDate e = toLocalDate(lr.getEndDate());
        return s.format(PERIOD_FMT) + " → " + e.format(PERIOD_FMT);
    }

    private static int calculateInclusiveDays(LeaveRequest lr) {
        if (lr.getStartDate() == null || lr.getEndDate() == null) {
            return 0;
        }
        LocalDate s = toLocalDate(lr.getStartDate());
        LocalDate e = toLocalDate(lr.getEndDate());
        if (e.isBefore(s)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(s, e) + 1;
    }

    private static LocalDate toLocalDate(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void putEmptyAdminManager(Map<String, Object> m) {
        m.put("summaryTotal", 0);
        m.put("summaryPending", 0);
        m.put("summaryApproved", 0);
        m.put("summaryRejected", 0);
        List<String> tl = new ArrayList<>();
        List<Integer> td = new ArrayList<>();
        for (int i = 0; i < TREND_WEEKS; i++) {
            tl.add("—");
            td.add(0);
        }
        m.put("chartTrendLabels", tl);
        m.put("chartTrendData", td);
        m.put("chartReasonLabels", List.of());
        m.put("chartReasonData", List.of());
        m.put("onLeaveThisWeek", List.of());
        m.put("notificationPreview", List.of());
        m.put("dashSummaryOk", Boolean.FALSE);
    }

    private void putEmptyEmployee(Map<String, Object> m) {
        m.put("empAnnualRemaining", 0);
        m.put("empAnnualUsed", 0);
        m.put("empAnnualTotal", 0);
        m.put("empPendingCount", 0L);
        m.put("empTeamOutToday", 0);
        m.put("empTeamOutNames", List.of());
        m.put("empLastLeaveDays", 0);
        m.put("empLastLeaveReason", "—");
        m.put("empLastLeavePeriod", "—");
        m.put("empRecentRows", List.of());
        m.put("notificationPreview", List.of());
        m.put("dashSummaryOk", Boolean.FALSE);
    }
}
