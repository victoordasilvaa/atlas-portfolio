package dev.portfolio.control;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/** JSON-only writes and same-origin checks also protect browser Basic credentials against CSRF. */
public class SecurityHeaders implements Filter {
  public void init(FilterConfig c) {}

  public void destroy() {}

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    res.setHeader("X-Content-Type-Options", "nosniff");
    res.setHeader("X-Frame-Options", "DENY");
    res.setHeader("Referrer-Policy", "no-referrer");
    res.setHeader("Cache-Control", "no-store");
    res.setHeader(
        "Content-Security-Policy",
        "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:;"
            + " frame-ancestors 'none'; base-uri 'self'; form-action 'self'");
    boolean write =
        !"GET".equals(req.getMethod())
            && !"HEAD".equals(req.getMethod())
            && !"OPTIONS".equals(req.getMethod());
    String origin = req.getHeader("Origin");
    String expected =
        req.getScheme()
            + "://"
            + req.getServerName()
            + ((req.getServerPort() == 80 || req.getServerPort() == 443)
                ? ""
                : ":" + req.getServerPort());
    if (write
        && ((origin != null && !origin.equals(expected))
            || "cross-site".equals(req.getHeader("Sec-Fetch-Site")))) {
      res.sendError(403);
      return;
    }
    if (write
        && !"DELETE".equals(req.getMethod())
        && (req.getContentType() == null
            || !req.getContentType()
                .toLowerCase(java.util.Locale.ROOT)
                .startsWith("application/json"))) {
      res.sendError(415);
      return;
    }
    if (req.getContentLengthLong() > 65536) {
      res.sendError(413);
      return;
    }
    chain.doFilter(req, res);
  }
}
