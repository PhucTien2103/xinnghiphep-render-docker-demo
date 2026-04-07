package nhom13.vn.service;

import java.util.List;

import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.Notification;
import nhom13.vn.entity.User;

public interface INotificationService {
    void create(Notification notification);

    List<Notification> getByViewer(User viewer);

    boolean markAsReadForViewer(int notificationId, User viewer);

    void notifyManagersAboutSubmittedLeaveRequest(User requester, LeaveRequest leaveRequest);

    /** Super admin: khi nhân viên gửi đơn nghỉ phép. */
    void notifySuperAdminsAboutSubmittedLeaveRequest(User requester, LeaveRequest leaveRequest);

    /**
     * Khi quản lý phê duyệt hoặc từ chối đơn của nhân viên: gửi cho nhân viên (đúng trạng thái) và cho super admin.
     */
    void notifyEmployeeAndSuperAdminsOnManagerDecision(User manager, LeaveRequest leaveRequest, boolean approved,
            String note);
}
