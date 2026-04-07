package nhom13.vn.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nhom13.vn.entity.LeaveType;
import nhom13.vn.entity.User;
import nhom13.vn.service.ILeaveTypeService;
import nhom13.vn.service.impl.LeaveTypeServiceImpl;

@WebServlet({
        "/admin/leave-types",
        "/admin/leave-types/add",
        "/admin/leave-types/insert",
        "/admin/leave-types/edit",
        "/admin/leave-types/update",
        "/admin/leave-types/delete"
})
public class AdminLeaveTypeController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ILeaveTypeService leaveTypeService = LeaveTypeServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("account");
        if (user == null || !"SUPER_ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        leaveTypeService.ensureSystemDefaults();

        String uri = req.getRequestURI();

        if (uri.contains("/add")) {
            req.getRequestDispatcher("/view/admin/leave-type-form.jsp").forward(req, resp);
            return;
        }

        if (uri.contains("/edit")) {
            int id = parsePositiveInt(req.getParameter("id"), -1);
            if (id <= 0) {
                resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                return;
            }
            LeaveType leaveType = leaveTypeService.findById(id);
            if (leaveType == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                return;
            }
            req.setAttribute("leaveType", leaveType);
            req.getRequestDispatcher("/view/admin/leave-type-form.jsp").forward(req, resp);
            return;
        }

        if (uri.contains("/delete")) {
            int id = parsePositiveInt(req.getParameter("id"), -1);
            boolean deleted = id > 0 && leaveTypeService.deleteById(id);
            if (!deleted) {
                req.getSession().setAttribute("message",
                        "Không thể xóa: loại nghỉ còn đang active, hoặc đang được dùng trong đơn nghỉ, hoặc không tồn tại.");
            } else {
                req.getSession().setAttribute("message", "Đã xóa loại nghỉ.");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
            return;
        }

        req.setAttribute("list", leaveTypeService.findAll());
        req.getRequestDispatcher("/view/admin/leave-types.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("account");
        if (user == null || !"SUPER_ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        leaveTypeService.ensureSystemDefaults();

        String uri = req.getRequestURI();

        try {
            if (uri.contains("/insert")) {
                LeaveType lt = new LeaveType();
                lt.setCode(req.getParameter("code"));
                lt.setName(req.getParameter("name"));
                lt.setConsumesBalance(req.getParameter("consumesBalance") != null);
                lt.setDefaultDaysPerYear(parseNonNegativeInt(req.getParameter("defaultDaysPerYear"), 0));
                lt.setActive(req.getParameter("active") != null);

                leaveTypeService.create(lt);
                req.getSession().setAttribute("message", "Đã thêm loại nghỉ.");
                resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                return;
            }

            if (uri.contains("/update")) {
                int id = parsePositiveInt(req.getParameter("id"), -1);
                if (id <= 0) {
                    resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                    return;
                }
                LeaveType lt = leaveTypeService.findById(id);
                if (lt == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                    return;
                }
                lt.setName(req.getParameter("name"));
                lt.setConsumesBalance(req.getParameter("consumesBalance") != null);
                lt.setDefaultDaysPerYear(parseNonNegativeInt(req.getParameter("defaultDaysPerYear"), 0));
                lt.setActive(req.getParameter("active") != null);

                leaveTypeService.update(lt);
                req.getSession().setAttribute("message", "Đã cập nhật loại nghỉ.");
                resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
                return;
            }
        } catch (Exception e) {
            req.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/leave-types");
    }

    private static int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int v = Integer.parseInt(raw.trim());
            return v > 0 ? v : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int parseNonNegativeInt(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int v = Integer.parseInt(raw.trim());
            return Math.max(0, v);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
