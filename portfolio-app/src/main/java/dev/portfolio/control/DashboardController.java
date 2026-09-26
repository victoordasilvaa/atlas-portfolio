package dev.portfolio.control;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class DashboardController extends HttpServlet {
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    req.setAttribute("username", req.getRemoteUser());
    req.setAttribute("canEdit", req.isUserInRole("editor"));
    req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
  }
}
