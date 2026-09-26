package dev.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import dev.portfolio.domain.BusinessException;
import dev.portfolio.integration.HttpMemberDirectory;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;

class HttpMemberDirectoryTest {
  HttpServer server;
  HttpMemberDirectory directory;
  int status;
  String body;

  @BeforeEach
  void start() throws Exception {
    status = 200;
    body = "{\"id\":1,\"name\":\"Ana\",\"role\":\"funcionário\"}";
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/members",
        e -> {
          if (!"Basic dXNlcjpwYXNz".equals(e.getRequestHeaders().getFirst("Authorization")))
            throw new AssertionError("Missing credentials");
          byte[] b = body.getBytes(StandardCharsets.UTF_8);
          e.sendResponseHeaders(status, b.length);
          e.getResponseBody().write(b);
          e.close();
        });
    server.start();
    directory =
        new HttpMemberDirectory(
            "http://127.0.0.1:" + server.getAddress().getPort(), "user", "pass");
  }

  @AfterEach
  void stop() {
    server.stop(0);
  }

  @Test
  void readsMember() {
    assertEquals("Ana", directory.get(1).name);
  }

  @Test
  void readsList() {
    body = "[" + body + "]";
    assertEquals(1, directory.list().size());
  }

  @Test
  void missingMember() {
    status = 404;
    assertEquals(
        "MEMBER_NOT_FOUND",
        assertThrows(BusinessException.class, () -> directory.get(1)).getCode());
  }

  @Test
  void upstreamFailure() {
    status = 500;
    assertEquals(503, assertThrows(BusinessException.class, () -> directory.get(1)).getStatus());
  }

  @Test
  void malformedUpstream() {
    body = "not json";
    assertEquals(503, assertThrows(BusinessException.class, () -> directory.get(1)).getStatus());
  }

  @Test
  void oversizedResponse() {
    body = String.join("", java.util.Collections.nCopies(1048577, "x"));
    assertThrows(BusinessException.class, () -> directory.get(1));
  }
}
