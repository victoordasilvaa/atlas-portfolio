package dev.portfolio.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.jupiter.api.*;

class MemberServletTest {
  MemberServlet servlet;
  HttpServletRequest request;
  HttpServletResponse response;
  ByteArrayOutputStream output;

  @BeforeEach
  void setup() throws Exception {
    servlet = new MemberServlet();
    servlet.init();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    output = new ByteArrayOutputStream();
    when(response.getOutputStream())
        .thenReturn(
            new ServletOutputStream() {
              public void write(int b) {
                output.write(b);
              }

              public boolean isReady() {
                return true;
              }

              public void setWriteListener(WriteListener l) {}
            });
  }

  void body(String value) throws Exception {
    ByteArrayInputStream bytes = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    when(request.getContentType()).thenReturn("application/json");
    when(request.getRequestURI()).thenReturn("/members-mock/members");
    when(request.getInputStream())
        .thenReturn(
            new ServletInputStream() {
              public int read() {
                return bytes.read();
              }

              public boolean isFinished() {
                return bytes.available() == 0;
              }

              public boolean isReady() {
                return true;
              }

              public void setReadListener(ReadListener l) {}
            });
  }

  @Test
  void createsAndReadsMember() throws Exception {
    body("{\"name\":\" Victor \",\"role\":\"funcionário\"}");
    servlet.doPost(request, response);
    verify(response).setStatus(201);
    JsonNode created = new ObjectMapper().readTree(output.toByteArray());
    assertEquals("Victor", created.get("name").asText());
    output.reset();
    when(request.getPathInfo()).thenReturn("/" + created.get("id").asLong());
    servlet.doGet(request, response);
    assertEquals(created, new ObjectMapper().readTree(output.toByteArray()));
  }

  @Test
  void rejectsIncompletePayload() throws Exception {
    body("{\"name\":\"Ana\"}");
    servlet.doPost(request, response);
    verify(response).setStatus(422);
  }

  @Test
  void missingMemberIs404() throws Exception {
    when(request.getPathInfo()).thenReturn("/9999");
    servlet.doGet(request, response);
    verify(response).setStatus(404);
  }

  @Test
  void seedHasEmployeesAndManager() throws Exception {
    servlet.doGet(request, response);
    JsonNode list = new ObjectMapper().readTree(output.toByteArray());
    assertEquals(13, list.size());
    assertEquals("gerente", list.get(12).get("role").asText());
  }
}
