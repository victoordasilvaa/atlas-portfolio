package dev.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.persistence.*;
import org.apache.catalina.Context;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.*;

/** Exercises the actual deployment descriptor, realm, JAX-RS stack and JSP on Tomcat. */
class HttpApiIntegrationTest {
  static Tomcat tomcat;
  static HttpServer directory;
  static EntityManagerFactory emf;
  static String base;
  static HttpClient client = HttpClient.newHttpClient();
  static ObjectMapper json = new ObjectMapper();

  @BeforeAll
  static void start() throws Exception {
    System.setProperty("javax.el.ExpressionFactory", "org.apache.el.ExpressionFactoryImpl");
    String url = "jdbc:h2:mem:http-test;DB_CLOSE_DELAY=-1";
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
    properties.put("javax.persistence.jdbc.url", url);
    properties.put("javax.persistence.jdbc.user", "sa");
    properties.put("javax.persistence.jdbc.password", "test");
    properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.put("hibernate.hbm2ddl.auto", "create-drop");
    emf = Persistence.createEntityManagerFactory("portfolio", properties);
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    em.createNativeQuery("create table portfolio_guard(id integer primary key)").executeUpdate();
    em.createNativeQuery("insert into portfolio_guard values(1)").executeUpdate();
    em.createNativeQuery(
            "create table project_event(id bigint auto_increment primary key,project_id"
                + " bigint,action varchar(100),actor varchar(150),occurred_at timestamp)")
        .executeUpdate();
    em.getTransaction().commit();
    em.close();
    directory = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    directory.createContext(
        "/members",
        exchange -> {
          String path = exchange.getRequestURI().getPath();
          String body =
              path.equals("/members")
                  ? "[{\"id\":1,\"name\":\"Ana\",\"role\":\"funcionário\"}]"
                  : "{\"id\":1,\"name\":\"Ana\",\"role\":\"funcionário\"}";
          byte[] data = body.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().set("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, data.length);
          exchange.getResponseBody().write(data);
          exchange.close();
        });
    directory.start();
    System.setProperty("DB_URL", url);
    System.setProperty("DB_USER", "sa");
    System.setProperty("DB_PASSWORD", "test");
    System.setProperty("DB_DIALECT", "org.hibernate.dialect.H2Dialect");
    System.setProperty("MEMBERS_URL", "http://127.0.0.1:" + directory.getAddress().getPort());
    System.setProperty("MEMBERS_USER", "directory");
    System.setProperty("MEMBERS_PASSWORD", "test");
    // Driver is selected explicitly for the test context, never shipped as an application
    // dependency.
    System.setProperty("DB_DRIVER", "org.h2.Driver");
    Path temp = Files.createTempDirectory("portfolio-tomcat-");
    tomcat = new Tomcat();
    tomcat.setBaseDir(temp.toString());
    tomcat.setPort(0);
    tomcat.getConnector();
    Path users = temp.resolve("users.xml");
    Files.write(
        users,
        ("<tomcat-users><role rolename=\"editor\"/><role rolename=\"viewer\"/><user"
                + " username=\"editor\" password=\"test\" roles=\"editor,viewer\"/><user"
                + " username=\"viewer\" password=\"test\" roles=\"viewer\"/></tomcat-users>")
            .getBytes(StandardCharsets.UTF_8));
    Context context =
        tomcat.addWebapp("/portfolio", Path.of("src/main/webapp").toAbsolutePath().toString());
    context.setParentClassLoader(HttpApiIntegrationTest.class.getClassLoader());
    MemoryRealm realm = new MemoryRealm();
    realm.setPathname(users.toString());
    context.setRealm(realm);
    tomcat.start();
    base = "http://localhost:" + tomcat.getConnector().getLocalPort() + "/portfolio/";
  }

  @AfterAll
  static void stop() throws Exception {
    if (tomcat != null) {
      tomcat.stop();
      tomcat.destroy();
    }
    if (directory != null) directory.stop(0);
    if (emf != null) emf.close();
    for (String key :
        Arrays.asList(
            "javax.el.ExpressionFactory",
            "DB_URL",
            "DB_USER",
            "DB_PASSWORD",
            "DB_DIALECT",
            "DB_DRIVER",
            "MEMBERS_URL",
            "MEMBERS_USER",
            "MEMBERS_PASSWORD")) System.clearProperty(key);
  }

  static HttpResponse<String> request(String path, String method, String body, String user)
      throws Exception {
    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + path));
    if (user != null)
      b.header(
          "Authorization",
          "Basic "
              + Base64.getEncoder()
                  .encodeToString((user + ":test").getBytes(StandardCharsets.UTF_8)));
    if (body != null) b.header("Content-Type", "application/json");
    return client.send(
        b.method(
                method,
                body == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(body))
            .build(),
        HttpResponse.BodyHandlers.ofString());
  }

  static final String INPUT =
      "{\"name\":\"HTTP"
          + " project\",\"description\":\"Integration\",\"startDate\":\"2026-01-01\",\"expectedEndDate\":\"2026-04-01\",\"budget\":100000.01,\"managerId\":1,\"memberIds\":[1]}";

  @Test
  void securityAndReadOnlyRole() throws Exception {
    assertEquals(401, request("api/projects", "GET", null, null).statusCode());
    assertEquals(200, request("api/projects", "GET", null, "viewer").statusCode());
    assertEquals(403, request("api/projects", "POST", INPUT, "viewer").statusCode());
  }

  @Test
  void lifecycleOverRealHttp() throws Exception {
    HttpResponse<String> created = request("api/projects", "POST", INPUT, "editor");
    assertEquals(201, created.statusCode(), created.body());
    JsonNode d = json.readTree(created.body());
    String path = "api/projects/" + d.get("id").asLong();
    assertEquals("MEDIO", d.get("risk").asText());
    assertTrue(created.headers().firstValue("ETag").isPresent());
    HttpResponse<String> changed =
        request(
            path + "/status",
            "PATCH",
            "{\"status\":\"ANALISE_REALIZADA\",\"version\":" + d.get("version") + "}",
            "editor");
    assertEquals(200, changed.statusCode(), changed.body());
    assertEquals(
        409,
        request(
                path + "/status",
                "PATCH",
                "{\"status\":\"ENCERRADO\",\"version\":"
                    + json.readTree(changed.body()).get("version")
                    + "}",
                "editor")
            .statusCode());
    assertEquals(428, request(path, "DELETE", null, "editor").statusCode());
    assertEquals(200, request("api/portfolio/report", "GET", null, "viewer").statusCode());
  }

  @Test
  void invalidRequestsAndJsp() throws Exception {
    assertEquals(400, request("api/projects", "POST", "{", "editor").statusCode());
    assertEquals(422, request("api/projects", "POST", "{}", "editor").statusCode());
    assertEquals(404, request("api/projects/999999", "GET", null, "editor").statusCode());
    HttpResponse<String> page = request("dashboard", "GET", null, "editor");
    assertEquals(200, page.statusCode(), page.body());
    assertTrue(page.body().contains("Seu portfólio"));
    assertTrue(page.headers().firstValue("Content-Security-Policy").isPresent());
  }

  @Test
  void rejectsUnknownFieldsWithProblemJson() throws Exception {
    HttpResponse<String> result =
        request(
            "api/projects",
            "POST",
            INPUT.replace("\"budget\":", "\"status\":\"ENCERRADO\",\"budget\":"),
            "editor");
    assertEquals(400, result.statusCode(), result.body());
    assertTrue(
        result
            .headers()
            .firstValue("Content-Type")
            .orElse("")
            .startsWith("application/problem+json"));
    assertEquals("INVALID_REQUEST", json.readTree(result.body()).get("code").asText());
  }

  @Test
  void rejectsFractionalIdentifiers() throws Exception {
    HttpResponse<String> response =
        request(
            "api/projects",
            "POST",
            INPUT.replace("\"managerId\":1", "\"managerId\":1.5"),
            "editor");
    assertEquals(400, response.statusCode(), response.body());
  }

  @Test
  void preservesLargeMonetaryValuesInResponses() throws Exception {
    String payload = INPUT.replace("100000.01", "9999999999999999.99");
    // Isolate capacity from other tests by using a new project with the same employee only if a
    // slot exists.
    HttpResponse<String> response = request("api/projects", "POST", payload, "editor");
    assertEquals(201, response.statusCode(), response.body());
    JsonNode amount = json.readTree(response.body()).get("budget");
    assertTrue(amount.isTextual());
    assertEquals("9999999999999999.99", amount.asText());
  }
}
