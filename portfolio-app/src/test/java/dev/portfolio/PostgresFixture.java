package dev.portfolio;

import java.nio.file.*;
import java.sql.*;
import java.util.*;
import javax.persistence.Persistence;

/** Runs the same persistence contract on PostgreSQL and validates the actual SQL migration. */
class PostgresFixture {
  static javax.persistence.EntityManagerFactory emf;
  static String url, user, password, schema;

  static javax.persistence.EntityManagerFactory start() {
    url = System.getenv("TEST_POSTGRES_URL");
    user = System.getenv("TEST_POSTGRES_USER");
    password = System.getenv("TEST_POSTGRES_PASSWORD");
    schema = "test_" + UUID.randomUUID().toString().replace("-", "");
    try (Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA " + schema);
      statement.execute("SET search_path TO " + schema);
      String migration =
          new String(
                  Files.readAllBytes(Paths.get("../database/001-schema.sql")),
                  java.nio.charset.StandardCharsets.UTF_8)
              .replaceAll("(?m)^--.*$", "");
      for (String sql : migration.split(";")) if (!sql.trim().isEmpty()) statement.execute(sql);
      Map<String, Object> p = new HashMap<>();
      p.put(
          "javax.persistence.jdbc.url",
          url + (url.contains("?") ? "&" : "?") + "currentSchema=" + schema);
      p.put("javax.persistence.jdbc.user", user);
      p.put("javax.persistence.jdbc.password", password);
      p.put("hibernate.hbm2ddl.auto", "validate");
      emf = Persistence.createEntityManagerFactory("portfolio", p);
      return emf;
    } catch (Exception e) {
      throw new IllegalStateException("PostgreSQL integration setup failed", e);
    }
  }

  static void stop() {
    if (emf != null) emf.close();
    if (schema != null)
      try (Connection c = DriverManager.getConnection(url, user, password);
          Statement s = c.createStatement()) {
        s.execute("DROP SCHEMA " + schema + " CASCADE");
      } catch (SQLException e) {
        throw new IllegalStateException(e);
      }
  }
}
