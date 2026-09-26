package dev.portfolio.config;

import dev.portfolio.facade.PortfolioFacade;
import dev.portfolio.integration.*;
import java.time.Clock;
import java.util.*;
import javax.persistence.*;
import javax.servlet.*;

public class Bootstrap implements ServletContextListener {
  private EntityManagerFactory factory;

  public void contextInitialized(ServletContextEvent event) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.jdbc.url", Environment.required("DB_URL"));
    properties.put("javax.persistence.jdbc.user", Environment.required("DB_USER"));
    properties.put("javax.persistence.jdbc.password", Environment.required("DB_PASSWORD"));
    properties.put(
        "javax.persistence.jdbc.driver", Environment.value("DB_DRIVER", "org.postgresql.Driver"));
    properties.put(
        "hibernate.dialect",
        Environment.value("DB_DIALECT", "org.hibernate.dialect.PostgreSQLDialect"));
    factory = Persistence.createEntityManagerFactory("portfolio", properties);
    MemberDirectory directory =
        new HttpMemberDirectory(
            Environment.required("MEMBERS_URL"),
            Environment.required("MEMBERS_USER"),
            Environment.required("MEMBERS_PASSWORD"));
    event.getServletContext().setAttribute(MemberDirectory.class.getName(), directory);
    event
        .getServletContext()
        .setAttribute(
            PortfolioFacade.class.getName(),
            new PortfolioFacade(factory, directory, Clock.systemUTC()));
  }

  public void contextDestroyed(ServletContextEvent event) {
    if (factory != null) factory.close();
  }
}
