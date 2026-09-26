package dev.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import dev.portfolio.domain.*;
import dev.portfolio.dto.*;
import dev.portfolio.facade.*;
import dev.portfolio.integration.*;
import dev.portfolio.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import javax.persistence.*;
import org.junit.jupiter.api.*;

class PersistenceIntegrationTest {
  static EntityManagerFactory emf;
  PortfolioFacade facade;
  static MemberDirectory directory =
      new MemberDirectory() {
        public MemberDto get(long id) {
          return new MemberDto(id, "Membro " + id, id == 99 ? "gerente" : "funcionário");
        }

        public List<MemberDto> list() {
          return Collections.emptyList();
        }
      };

  @BeforeAll
  static void start() {
    if (System.getenv("TEST_POSTGRES_URL") != null) {
      emf = PostgresFixture.start();
      return;
    }
    Map<String, Object> p = new HashMap<>();
    p.put("javax.persistence.jdbc.driver", "org.h2.Driver");
    p.put(
        "javax.persistence.jdbc.url",
        "jdbc:h2:mem:portfolio-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000");
    p.put("javax.persistence.jdbc.user", "sa");
    p.put("javax.persistence.jdbc.password", "");
    p.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    p.put("hibernate.hbm2ddl.auto", "create-drop");
    emf = Persistence.createEntityManagerFactory("portfolio", p);
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
  }

  @BeforeEach
  void reset() {
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    for (String table :
        Arrays.asList("project_member", "project", "member_snapshot", "project_event"))
      em.createNativeQuery("delete from " + table).executeUpdate();
    em.getTransaction().commit();
    em.close();
    facade =
        new PortfolioFacade(
            emf, directory, Clock.fixed(Instant.parse("2026-09-25T12:00:00Z"), ZoneOffset.UTC));
  }

  @AfterAll
  static void stop() {
    if (System.getenv("TEST_POSTGRES_URL") != null) {
      PostgresFixture.stop();
      return;
    }
    if (emf != null) emf.close();
  }

  ProjectInput input() {
    ProjectInput d = new ProjectInput();
    d.name = "Alpha 100%";
    d.description = "Projeto de teste";
    d.startDate = "2026-01-01";
    d.expectedEndDate = "2026-04-01";
    d.budget = new BigDecimal("100000.00");
    d.managerId = 99L;
    d.memberIds = Arrays.asList(1L);
    return d;
  }

  ProjectDto create() {
    return facade.execute(s -> s.create(input(), "tester"));
  }

  @Test
  void realJpaCrudVersionAndFilters() {
    ProjectDto d = create();
    assertNotNull(d.id);
    assertEquals(1, facade.execute(s -> s.list("100%", null, 0, 20)).total);
    assertEquals(0, facade.execute(s -> s.list("100_", null, 0, 20)).total);
    ProjectInput change = input();
    change.version = d.version;
    change.name = "Beta";
    ProjectDto updated = facade.execute(s -> s.update(d.id, change, "tester"));
    assertTrue(updated.version > d.version);
    assertThrows(
        BusinessException.class, () -> facade.execute(s -> s.update(d.id, change, "tester")));
    facade.execute(
        s -> {
          s.delete(d.id, updated.version, "tester");
          return null;
        });
    assertThrows(BusinessException.class, () -> facade.execute(s -> s.get(d.id)));
  }

  @Test
  void completeLifecycleReportAndCancelFreesSlot() {
    ProjectDto d = create();
    for (ProjectStatus st :
        Arrays.asList(
            ProjectStatus.ANALISE_REALIZADA,
            ProjectStatus.ANALISE_APROVADA,
            ProjectStatus.INICIADO,
            ProjectStatus.PLANEJADO,
            ProjectStatus.EM_ANDAMENTO,
            ProjectStatus.ENCERRADO)) {
      StatusInput change = new StatusInput();
      change.version = d.version;
      change.status = st;
      if (st == ProjectStatus.ENCERRADO) change.actualEndDate = "2026-04-01";
      long id = d.id;
      d = facade.execute(s -> s.transition(id, change, "tester"));
    }
    PortfolioReport r = facade.execute(s -> s.report());
    assertEquals(1L, r.projectsByStatus.get(ProjectStatus.ENCERRADO));
    assertEquals(new BigDecimal("90.00"), r.averageCompletedDurationDays);
    assertEquals(1, r.uniqueAllocatedMembers);
    create();
    create();
    create();
    assertThrows(BusinessException.class, this::create);
  }

  @Test
  void concurrentFourthAllocationOnlyOneWins() throws Exception {
    create();
    create();
    ExecutorService pool = Executors.newFixedThreadPool(2);
    CountDownLatch ready = new CountDownLatch(2), go = new CountDownLatch(1);
    Callable<Boolean> task =
        () -> {
          ready.countDown();
          go.await();
          try {
            create();
            return true;
          } catch (BusinessException e) {
            assertEquals("CONFLICT", e.getCode());
            return false;
          }
        };
    try {
      Future<Boolean> a = pool.submit(task), b = pool.submit(task);
      assertTrue(ready.await(5, TimeUnit.SECONDS));
      go.countDown();
      assertNotEquals(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS));
      assertEquals(3, facade.execute(s -> s.list(null, null, 0, 20)).total);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void cancellationAndRollback() {
    ProjectDto d = create();
    create();
    create();
    ProjectInput fourth = input();
    fourth.name = "Should rollback";
    assertThrows(BusinessException.class, () -> facade.execute(s -> s.create(fourth, "tester")));
    StatusInput cancel = new StatusInput();
    cancel.version = d.version;
    cancel.status = ProjectStatus.CANCELADO;
    facade.execute(s -> s.transition(d.id, cancel, "tester"));
    create();
    assertEquals(4, facade.execute(s -> s.list(null, null, 0, 20)).total);
  }
}
