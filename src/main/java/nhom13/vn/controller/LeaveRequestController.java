package nhom13.vn.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import nhom13.vn.entity.LeaveBalance;
import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.entity.User;
import nhom13.vn.factory.LeaveRequestFactory;
import nhom13.vn.service.ILeaveBalanceService;
import nhom13.vn.service.ILeaveRequestService;
import nhom13.vn.service.ILeaveTypeService;
import nhom13.vn.service.INotificationService;
import nhom13.vn.service.impl.LeaveBalanceServiceImpl;
import nhom13.vn.service.impl.LeaveRequestServiceImpl;
import nhom13.vn.service.impl.LeaveTypeServiceImpl;
import nhom13.vn.service.impl.NotificationServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@WebServlet({
    "/leave/create",
    "/leave/insert"
})
public class LeaveRequestController extends HttpServlet {

    ILeaveRequestService service = new LeaveRequestServiceImpl();
    ILeaveBalanceService leaveBalanceService = LeaveBalanceServiceImpl.getInstance();
    ILeaveTypeService leaveTypeService = LeaveTypeServiceImpl.getInstance();
    INotificationService notificationService = NotificationServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("account");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = user.getRole();
        if (!"EMPLOYEE".equals(role) && !"MANAGER".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Role does not have leave requests");
            return;
        }

        leaveTypeService.ensureSystemDefaults();

        LeaveBalance leaveBalance = leaveBalanceService.ensureDefaultForUser(user);
        req.setAttribute("leaveBalance", leaveBalance);
        req.setAttribute("leaveTypes", leaveTypeService.findAllActive());

        req.getRequestDispatcher("/view/leave/create.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String start = req.getParameter("startDate");
        String end = req.getParameter("endDate");
        String reason = req.getParameter("reason");
        String leaveTypeIdRaw = req.getParameter("leaveTypeId");

        leaveTypeService.ensureSystemDefaults();

        User user = (User) req.getSession().getAttribute("account");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = user.getRole();
        if (!"EMPLOYEE".equals(role) && !"MANAGER".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Role does not have leave requests");
            return;
        }

        LeaveBalance leaveBalanceForForm = leaveBalanceService.ensureDefaultForUser(user);
        req.setAttribute("leaveBalance", leaveBalanceForForm);
        req.setAttribute("leaveTypes", leaveTypeService.findAllActive());

        // validation
        if (start == null || end == null || reason == null ||
            start.isEmpty() || end.isEmpty() || reason.isEmpty()) {

            req.setAttribute("alert", "Vui lòng nhập đầy đủ!");
            req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
            return;
        }

        int leaveTypeId;
        try {
            leaveTypeId = Integer.parseInt(leaveTypeIdRaw != null ? leaveTypeIdRaw.trim() : "");
        } catch (NumberFormatException e) {
            req.setAttribute("alert", "Vui lòng chọn loại nghỉ.");
            req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
            return;
        }

        LeaveType leaveType = leaveTypeService.findById(leaveTypeId);
        if (leaveType == null || !leaveType.isActive()) {
            req.setAttribute("alert", "Loại nghỉ không hợp lệ.");
            req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
            return;
        }

        LeaveBalance leaveBalance = leaveBalanceForForm;

        if (leaveBalance == null) {
            req.setAttribute("alert", "Tai khoan nay khong ap dung nghi phep.");
            req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            if (endDate.isBefore(startDate)) {
                req.setAttribute("leaveBalance", leaveBalance);
                req.setAttribute("leaveTypes", leaveTypeService.findAllActive());
                req.setAttribute("alert", "Ngay ket thuc phai >= ngay bat dau.");
                req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
                return;
            }

            long requestedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (requestedDays <= 0) {
                req.setAttribute("leaveBalance", leaveBalance);
                req.setAttribute("leaveTypes", leaveTypeService.findAllActive());
                req.setAttribute("alert", "So ngay nghi khong hop le.");
                req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
                return;
            }

            boolean consumesBalance = leaveType.isConsumesBalance();
            if (consumesBalance && requestedDays > leaveBalance.getRemainingDays()) {
                req.setAttribute("leaveBalance", leaveBalance);
                req.setAttribute("leaveTypes", leaveTypeService.findAllActive());
                req.setAttribute("alert", "So ngay nghi vuot qua so ngay con lai.");
                req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
                return;
            }

            LeaveRequest lr = LeaveRequestFactory.create(user, start, end, reason, leaveType);

            service.create(lr);
            notificationService.notifyManagersAboutSubmittedLeaveRequest(user, lr);
            notificationService.notifySuperAdminsAboutSubmittedLeaveRequest(user, lr);

            HttpSession session = req.getSession();

            session.setAttribute("message", "Tạo đơn nghỉ thành công!");


            if ("MANAGER".equals(role)) {
                resp.sendRedirect(req.getContextPath() + "/manager/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/employee/dashboard");
            }

        } catch (Exception e) {
            req.setAttribute("leaveBalance", leaveBalanceService.ensureDefaultForUser(user));
            req.setAttribute("leaveTypes", leaveTypeService.findAllActive());
            req.setAttribute("alert", "Lỗi dữ liệu!");
            req.getRequestDispatcher("/view/leave/create.jsp").forward(req, resp);
        }
    }
}
