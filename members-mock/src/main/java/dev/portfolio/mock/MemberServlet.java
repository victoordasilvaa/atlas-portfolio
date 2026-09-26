package dev.portfolio.mock;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.*;

public class MemberServlet extends HttpServlet {
  private final Map<Long, Map<String, Object>> members = new ConcurrentSkipListMap<>();
  private final AtomicLong sequence = new AtomicLong(100);
  private final ObjectMapper json = new ObjectMapper();

  public void init() {
    for (long id = 1; id <= 12; id++)
      members.put(id, member(id, "Profissional " + id, "funcionário"));
    members.put(99L, member(99L, "Marina Costa", "gerente"));
  }

  private Map<String, Object> member(long id, String name, String role) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", id);
    m.put("name", name);
    m.put("role", role);
    return m;
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String path = req.getPathInfo();
    if (path == null || "/".equals(path)) {
      send(res, 200, members.values());
      return;
    }
    try {
      Map<String, Object> m = members.get(Long.valueOf(path.substring(1)));
      if (m == null) {
        send(res, 404, Collections.singletonMap("message", "Membro não encontrado."));
        return;
      }
      send(res, 200, m);
    } catch (NumberFormatException e) {
      send(res, 400, Collections.singletonMap("message", "Identificador inválido."));
    }
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    if (req.getPathInfo() != null && !"/".equals(req.getPathInfo())) {
      res.sendError(405);
      return;
    }
    if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
      res.sendError(415);
      return;
    }
    if (req.getContentLengthLong() > 4096) {
      res.sendError(413);
      return;
    }
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int n;
      while ((n = req.getInputStream().read(buf)) != -1) {
        if (out.size() + n > 4096) {
          res.sendError(413);
          return;
        }
        out.write(buf, 0, n);
      }
      JsonNode body = json.readTree(out.toByteArray());
      if (body == null
          || !body.isObject()
          || body.size() != 2
          || !body.path("name").isTextual()
          || !body.path("role").isTextual()) throw new IllegalArgumentException();
      String name = body.get("name").asText().trim(), role = body.get("role").asText().trim();
      if (name.isEmpty() || name.length() > 160 || role.isEmpty() || role.length() > 80)
        throw new IllegalArgumentException();
      long id = sequence.getAndIncrement();
      Map<String, Object> m = member(id, name, role);
      members.put(id, m);
      res.setHeader("Location", req.getRequestURI() + "/" + id);
      send(res, 201, m);
    } catch (IllegalArgumentException | com.fasterxml.jackson.core.JsonProcessingException e) {
      send(
          res,
          422,
          Collections.singletonMap("message", "Informe somente name (1–160) e role (1–80)."));
    }
  }

  private void send(HttpServletResponse res, int status, Object body) throws IOException {
    res.setStatus(status);
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-store");
    json.writeValue(res.getOutputStream(), body);
  }
}
