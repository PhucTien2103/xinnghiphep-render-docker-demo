package nhom13.vn.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nhom13.vn.entity.LeaveRequest;
import nhom13.vn.entity.User;
import nhom13.vn.service.ILeaveRequestService;
import nhom13.vn.service.impl.LeaveRequestServiceImpl;
import nhom13.vn.utils.PDFReportGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/leave/report")
public class LeaveReportController extends HttpServlet {

    private ILeaveRequestService service = LeaveRequestServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("account");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = user.getRole();
        if (!"MANAGER".equals(role) && !"SUPER_ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String status = req.getParameter("status");
        List<LeaveRequest> list = service.getAllForViewer(user, status);

        resp.setContentType("application/pdf");
        String filename = "leave-report.pdf";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (OutputStream out = resp.getOutputStream()) {
            PDFReportGenerator.generateLeaveReport(list, out);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate PDF: " + e.getMessage());
        }
    }
}
