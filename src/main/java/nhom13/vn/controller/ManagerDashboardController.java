package nhom13.vn.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nhom13.vn.entity.User;
import nhom13.vn.service.IDashboardSummaryService;
import nhom13.vn.service.impl.DashboardSummaryServiceImpl;
import nhom13.vn.util.HttpCacheUtil;

@WebServlet("/manager/dashboard")
public class ManagerDashboardController extends HttpServlet {

    private final IDashboardSummaryService dashboardSummary = DashboardSummaryServiceImpl.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpCacheUtil.disableCaching(resp);

        User user = (User) req.getSession().getAttribute("account");
        if (user != null) {
            Map<String, Object> data = dashboardSummary.buildAdminManagerSummary(user);
            if (data != null) {
                for (Map.Entry<String, Object> e : data.entrySet()) {
                    req.setAttribute(e.getKey(), e.getValue());
                }
            }
        }

        req.getRequestDispatcher("/view/manager/dashboard.jsp").forward(req, resp);
    }
}