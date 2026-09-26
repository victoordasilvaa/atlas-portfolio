package dev.portfolio.facade;

import dev.portfolio.integration.MemberDirectory;
import dev.portfolio.persistence.ProjectJpaDAO;
import dev.portfolio.service.ProjectService;
import java.time.Clock;
import java.util.function.Function;
import javax.persistence.*;
import org.jcompany.facade.PlcBaseFacadeImpl;

/** Transaction boundary: a fresh persistence context per request, closed before serialization. */
public final class PortfolioFacade extends PlcBaseFacadeImpl {
  private final EntityManagerFactory factory;
  private final MemberDirectory directory;
  private final Clock clock;

  public PortfolioFacade(EntityManagerFactory f, MemberDirectory d, Clock c) {
    factory = f;
    directory = d;
    clock = c;
  }

  public <T> T execute(Function<ProjectService, T> work) {
    return transaction(work, false);
  }

  public <T> T snapshot(Function<ProjectService, T> work) {
    return transaction(work, true);
  }

  private <T> T transaction(Function<ProjectService, T> work, boolean snapshot) {
    EntityManager em = factory.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      ((org.hibernate.Session) em.getDelegate())
          .doWork(
              connection ->
                  connection.setTransactionIsolation(
                      snapshot
                          ? java.sql.Connection.TRANSACTION_REPEATABLE_READ
                          : java.sql.Connection.TRANSACTION_READ_COMMITTED));
      tx.begin();
      T result = work.apply(new ProjectService(new ProjectJpaDAO(em), directory, clock));
      tx.commit();
      return result;
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }
}
