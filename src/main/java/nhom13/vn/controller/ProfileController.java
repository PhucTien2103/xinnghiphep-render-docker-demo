package nhom13.vn.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

import nhom13.vn.entity.User;
import nhom13.vn.service.IUserService;
import nhom13.vn.service.impl.CloudinaryImageService;
import nhom13.vn.service.impl.UserServiceImpl;

@WebServlet(urlPatterns = {"/my-profile", "/update-profile"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class ProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	IUserService userService = new UserServiceImpl();

	// Hien thi profile
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("account");

        String idParam = req.getParameter("id");
        User user;

        if (idParam == null) {
            user = currentUser;
        } else {
            int id = Integer.parseInt(idParam);
            user = userService.findById(id);

            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            if (!isAllowed(currentUser, user)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/view/profile/profile.jsp")
                .forward(req, resp);
    }

	// Update profile
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("account");
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		String fullName = req.getParameter("fullName");
		String email = req.getParameter("email");

		user.setFullName(fullName);
		user.setEmail(email);

		Part avatarPart = req.getPart("avatar");
		if (avatarPart != null && avatarPart.getSize() > 0) {
			try {
				String avatarUrl = new CloudinaryImageService().uploadProfileImage(avatarPart, user.getId());
				user.setAvatarUrl(avatarUrl);
      } catch (Exception e) {
				req.setAttribute("error", e.getMessage());
				req.setAttribute("user", user);
				req.getRequestDispatcher("/view/profile/profile.jsp").forward(req, resp);
				return;
			}
		}

		userService.update(user);

		session.setAttribute("account", user);

		resp.sendRedirect(req.getContextPath() + "/my-profile?success=1");
	}

    private boolean isAllowed(User currentUser, User targetUser) {

        String role = currentUser.getRole();
        String targetRole = targetUser.getRole();

        // Xem chính mình
        if (currentUser.getId() == targetUser.getId()) {
            return true;
        }

        // Manager xem employee
        if ("MANAGER".equals(role) && "EMPLOYEE".equals(targetRole)) {
            return true;
        }

        // Admin xem employee + manager
        if ("SUPER_ADMIN".equals(role) &&
                ("EMPLOYEE".equals(targetRole) || "MANAGER".equals(targetRole))) {
            return true;
        }

        return false;
    }
}
