package nhom13.vn.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nhom13.vn.entity.LeaveBalance;
import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.entity.User;
import nhom13.vn.service.ILeaveBalanceService;
import nhom13.vn.service.ILeaveRequestService;
import nhom13.vn.service.ILeaveTypeService;
import nhom13.vn.service.impl.LeaveBalanceServiceImpl;
import nhom13.vn.service.impl.LeaveRequestServiceImpl;
import nhom13.vn.service.impl.LeaveTypeServiceImpl;

@WebServlet({ "/leave/edit", "/leave/update" })
public class LeaveRequestEditController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ILeaveRequestService leaveRequestService = LeaveRequestServiceImpl.getInstance();
    private final ILeaveBalanceService leaveBalanceService = LeaveBalanceServiceImpl.getInstance();
    private final ILeaveTypeService leaveTypeService = LeaveTypeServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/leave/edit".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        User user = (User) req.getSession().getAttribute("account");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!canEditOwnLeave(user)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only employees or managers can edit own leave requests");
            return;
        }

        int leaveId = parseLeaveId(req.getParameter("id"));
        if (leaveId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid leave request id");
            return;
        }

        LeaveRequest leaveRequest = leaveRequestService.getDetailForViewer(leaveId, user);
        if (leaveRequest == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Leave request not found");
            return;
        }
        if (!"PENDING".equals(leaveRequest.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/leave/detail?id=" + leaveId + "&msg=cannotedit");
            return;
        }

        leaveTypeService.ensureSystemDefaults();

        LeaveBalance leaveBalance = leaveBalanceService.ensureDefaultForUser(user);
        req.setAttribute("leaveRequest", leaveRequest);
        req.setAttribute("leaveBalance", leaveBalance);
        req.setAttribute("leaveTypes", leaveTypeService.findAllActive());
        req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/leave/update".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("account");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!canEditOwnLeave(user)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only employees or managers can edit own leave requests");
            return;
        }

        int leaveId = parseLeaveId(req.getParameter("id"));
        if (leaveId <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid leave request id");
            return;
        }

        LeaveRequest leaveRequest = leaveRequestService.getDetailForViewer(leaveId, user);
        if (leaveRequest == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Leave request not found");
            return;
        }
        if (!"PENDING".equals(leaveRequest.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/leave/detail?id=" + leaveId + "&msg=cannotedit");
            return;
        }

        String start = req.getParameter("startDate");
        String end = req.getParameter("endDate");
        String reason = req.getParameter("reason");
        Integer leaveTypeId = parseOptionalLeaveTypeId(req.getParameter("leaveTypeId"));

        leaveTypeService.ensureSystemDefaults();

        LeaveBalance leaveBalance = leaveBalanceService.ensureDefaultForUser(user);
        req.setAttribute("leaveRequest", leaveRequest);
        req.setAttribute("leaveBalance", leaveBalance);
        req.setAttribute("leaveTypes", leaveTypeService.findAllActive());

        if (start == null || end == null || reason == null
                || start.isEmpty() || end.isEmpty() || reason.isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập đầy đủ!");
            req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
            return;
        }

        if (leaveBalance == null) {
            req.setAttribute("alert", "Tai khoan nay khong ap dung nghi phep.");
            req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            if (endDate.isBefore(startDate)) {
                req.setAttribute("alert", "Ngay ket thuc phai >= ngay bat dau.");
                req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
                return;
            }

            long requestedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (requestedDays <= 0) {
                req.setAttribute("alert", "So ngay nghi khong hop le.");
                req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
                return;
            }

            LeaveType effectiveType = resolveEffectiveLeaveType(leaveRequest, leaveTypeId);
            if (leaveTypeId != null && (effectiveType == null || !effectiveType.isActive())) {
                req.setAttribute("alert", "Loại nghỉ không hợp lệ.");
                req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
                return;
            }

            boolean consumesBalance = effectiveType == null || effectiveType.isConsumesBalance();
            if (consumesBalance && requestedDays > leaveBalance.getRemainingDays()) {
                req.setAttribute("alert", "So ngay nghi vuot qua so ngay con lai.");
                req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
                return;
            }

            boolean ok = leaveRequestService.updatePendingForEmployee(leaveId, user, startDate, endDate, reason,
                    leaveTypeId);
            if (ok) {
                req.getSession().setAttribute("message", "Cập nhật đơn nghỉ thành công!");
                resp.sendRedirect(req.getContextPath() + "/leave/detail?id=" + leaveId);
                return;
            }

            req.setAttribute("alert", "Không thể cập nhật đơn. Vui lòng thử lại.");
            req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("alert", "Lỗi dữ liệu!");
            req.getRequestDispatcher("/view/leave/edit.jsp").forward(req, resp);
        }
    }

    private static Integer parseOptionalLeaveTypeId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            int id = Integer.parseInt(raw.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LeaveType resolveEffectiveLeaveType(LeaveRequest leaveRequest, Integer leaveTypeId) {
        if (leaveTypeId != null) {
            return leaveTypeService.findById(leaveTypeId);
        }
        return leaveRequest.getLeaveType();
    }

    private static int parseLeaveId(String idRaw) {
        if (idRaw == null) {
            return -1;
        }
        try {
            int id = Integer.parseInt(idRaw.trim());
            return id > 0 ? id : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean canEditOwnLeave(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        String role = user.getRole().trim().toUpperCase();
        return "EMPLOYEE".equals(role) || "MANAGER".equals(role);
    }
}
