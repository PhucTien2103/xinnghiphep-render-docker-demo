package nhom13.vn.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nhom13.vn.dao.INotificationDao;
import nhom13.vn.dao.impl.NotificationDaoImpl;
import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.Notification;
import nhom13.vn.entity.User;
import nhom13.vn.service.INotificationService;
import nhom13.vn.service.IUserService;

public class NotificationServiceImpl implements INotificationService {

    private static NotificationServiceImpl instance;

    private final INotificationDao notificationDao;
    private final IUserService userService;

    private NotificationServiceImpl() {
        this.notificationDao = NotificationDaoImpl.getInstance();
        this.userService = new UserServiceImpl();
    }

    public static NotificationServiceImpl getInstance() {
        if (instance == null) {
            instance = new NotificationServiceImpl();
        }
        return instance;
    }

    @Override
    public void create(Notification notification) {
        if (notification == null || notification.getReceiver() == null || notification.getReceiver().getId() <= 0) {
            return;
        }

        if (notification.getSentTime() == null) {
            notification.setSentTime(new Date());
        }

        notificationDao.insert(notification);
    }

    @Override
    public List<Notification> getByViewer(User viewer) {
        if (viewer == null || viewer.getId() <= 0) {
            return List.of();
        }

        return notificationDao.findByReceiver(viewer.getId());
    }

    @Override
    public boolean markAsReadForViewer(int notificationId, User viewer) {
        if (viewer == null || viewer.getId() <= 0 || notificationId <= 0) {
            return false;
        }

        return notificationDao.markAsRead(notificationId, viewer.getId());
    }

    @Override
    public void notifyManagersAboutSubmittedLeaveRequest(User requester, LeaveRequest leaveRequest) {
        if (requester == null || leaveRequest == null || requester.getId() <= 0) {
            return;
        }

        String requesterRole = requester.getRole();
        if (!"EMPLOYEE".equals(requesterRole) && !"MANAGER".equals(requesterRole)) {
            return;
        }

        if (requester.getCompany() == null || requester.getCompany().getId() <= 0) {
            return;
        }

        List<User> managers = userService.findByRole("MANAGER");
        if (managers == null || managers.isEmpty()) {
            return;
        }

        int companyId = requester.getCompany().getId();
        String content = buildSubmittedRequestMessage(requester, leaveRequest);

        for (User manager : managers) {
            if (manager == null || manager.getId() <= 0 || manager.getCompany() == null) {
                continue;
            }

            if (manager.getCompany().getId() != companyId) {
                continue;
            }
            if ("MANAGER".equals(requesterRole) && manager.getId() == requester.getId()) {
                continue;
            }

            Notification notification = new Notification();
            notification.setReceiver(manager);
            notification.setContent(content);
            notification.setRead(false);
            notification.setSentTime(new Date());
            create(notification);
        }
    }

    private String buildSubmittedRequestMessage(User requester, LeaveRequest leaveRequest) {
        String requesterName = requester.getFullName();
        if (requesterName == null || requesterName.isBlank()) {
            requesterName = requester.getUsername();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String title = "MANAGER".equals(requester.getRole()) ? "Quản lý " : "Nhân viên ";
        return title + requesterName
                + " vừa gửi đơn nghỉ phép từ "
                + dateFormat.format(leaveRequest.getStartDate())
                + " đến "
                + dateFormat.format(leaveRequest.getEndDate()) + ".";
    }

    @Override
    public void notifySuperAdminsAboutSubmittedLeaveRequest(User requester, LeaveRequest leaveRequest) {
        if (requester == null || leaveRequest == null || requester.getId() <= 0) {
            return;
        }

        if (!"EMPLOYEE".equals(requester.getRole())) {
            return;
        }

        List<User> superAdmins = userService.findByRole("SUPER_ADMIN");
        if (superAdmins == null || superAdmins.isEmpty()) {
            return;
        }

        String content = buildSubmittedRequestMessage(requester, leaveRequest);
        for (User admin : superAdmins) {
            if (admin == null || admin.getId() <= 0) {
                continue;
            }
            Notification notification = new Notification();
            notification.setReceiver(admin);
            notification.setContent(content);
            notification.setRead(false);
            notification.setSentTime(new Date());
            create(notification);
        }
    }

    @Override
    public void notifyEmployeeAndSuperAdminsOnManagerDecision(User manager, LeaveRequest leaveRequest, boolean approved,
            String note) {
        if (manager == null || leaveRequest == null || leaveRequest.getId() <= 0) {
            return;
        }

        String reviewerRole = manager.getRole();
        if (!"MANAGER".equals(reviewerRole) && !"SUPER_ADMIN".equals(reviewerRole)) {
            return;
        }

        User requester = leaveRequest.getUser();
        if (requester == null || requester.getId() <= 0) {
            return;
        }
        String requesterRole = requester.getRole();
        if (!"EMPLOYEE".equals(requesterRole) && !"MANAGER".equals(requesterRole)) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String range = dateFormat.format(leaveRequest.getStartDate()) + " đến " + dateFormat.format(leaveRequest.getEndDate());
        String noteSuffix = formatOptionalNote(note);

        String statusWord = approved ? "ĐÃ DUYỆT" : "TỪ CHỐI";
        String actorLabel = "MANAGER".equals(reviewerRole) ? "quản lý" : "super admin";
        String requesterBody = approved
                ? ("Đơn nghỉ phép của bạn (từ " + range + ") đã được " + actorLabel
                        + " phê duyệt. Trạng thái: " + statusWord + ".")
                : ("Đơn nghỉ phép của bạn (từ " + range + ") đã bị " + actorLabel
                        + " từ chối. Trạng thái: " + statusWord + ".");
        requesterBody += noteSuffix;

        Notification toRequester = new Notification();
        toRequester.setReceiver(requester);
        toRequester.setContent(requesterBody);
        toRequester.setRead(false);
        toRequester.setSentTime(new Date());
        create(toRequester);

        if (!"MANAGER".equals(reviewerRole)) {
            return;
        }

        String managerName = displayName(manager);
        String requesterRoleLabel = "MANAGER".equals(requesterRole) ? "quản lý " : "nhân viên ";
        String requesterName = displayName(requester);
        String adminBody = approved
                ? ("Quản lý " + managerName + " đã phê duyệt đơn nghỉ phép của " + requesterRoleLabel + requesterName
                        + " (từ " + range + "). Trạng thái đơn: ĐÃ DUYỆT.")
                : ("Quản lý " + managerName + " đã từ chối đơn nghỉ phép của " + requesterRoleLabel + requesterName
                        + " (từ " + range + "). Trạng thái đơn: TỪ CHỐI.");
        adminBody += noteSuffix;

        List<User> superAdmins = userService.findByRole("SUPER_ADMIN");
        if (superAdmins == null || superAdmins.isEmpty()) {
            return;
        }
        for (User admin : superAdmins) {
            if (admin == null || admin.getId() <= 0) {
                continue;
            }
            Notification n = new Notification();
            n.setReceiver(admin);
            n.setContent(adminBody);
            n.setRead(false);
            n.setSentTime(new Date());
            create(n);
        }
    }

    private static String formatOptionalNote(String note) {
        if (note == null) {
            return "";
        }
        String t = note.trim();
        if (t.isEmpty()) {
            return "";
        }
        return " Ghi chú quản lý: " + t;
    }

    private static String displayName(User user) {
        if (user == null) {
            return "";
        }
        String name = user.getFullName();
        if (name != null && !name.isBlank()) {
            return name.trim();
        }
        return user.getUsername() != null ? user.getUsername() : "";
    }
}
