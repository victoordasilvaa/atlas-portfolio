package dev.portfolio.persistence;

import dev.portfolio.domain.*;
import dev.portfolio.dto.*;
import dev.portfolio.entity.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.*;
import org.jcompany.persistence.jpa.PlcBaseJpaDAO;

/** Real jCompany DAO extension: inherited insert, inherited EntityManager, JPA queries. */
public class ProjectJpaDAO extends PlcBaseJpaDAO implements ProjectRepository {
  public ProjectJpaDAO(EntityManager entityManager) {
    this.em = entityManager;
  }

  public void lockWrites() {
    em.createNativeQuery("select id from portfolio_guard where id=1 for update").getSingleResult();
  }

  public Project find(long id) {
    Project project = em.find(Project.class, id);
    if (project == null) throw new BusinessException("NOT_FOUND", "Projeto não encontrado.", 404);
    return project;
  }

  public void insert(Project project) {
    try {
      super.insert(project);
    } catch (org.jcompany.commons.PlcException e) {
      throw new PersistenceException("Falha na inserção jCompany", e);
    }
  }

  public void delete(Project project) {
    em.remove(project);
  }

  public void flush() {
    em.flush();
  }

  public Member snapshot(MemberDto memberData) {
    Member member = em.find(Member.class, memberData.id);
    if (member == null) {
      member = new Member(memberData.id, memberData.name, memberData.role);
      em.persist(member);
    } else {
      member.setName(memberData.name);
      member.setRole(memberData.role);
      member.setDateLastUpdate(new Date());
    }
    return member;
  }

  public long activeAllocations(long memberId, Long excluded) {
    Query query =
        em.createQuery(
            "select count(p) from Project p join p.members m where m.id=:member and p.status not in"
                + " (:closed,:cancelled)"
                + (excluded == null ? "" : " and p.id<>:excluded"));
    query
        .setParameter("member", memberId)
        .setParameter("closed", ProjectStatus.ENCERRADO)
        .setParameter("cancelled", ProjectStatus.CANCELADO);
    if (excluded != null) query.setParameter("excluded", excluded);
    return ((Number) query.getSingleResult()).longValue();
  }

  private Query filteredQuery(boolean countOnly, String name, ProjectStatus status) {
    Query query =
        em.createQuery(
            (countOnly ? "select count(p)" : "select p")
                + " from Project p where 1=1"
                + (name == null ? "" : " and lower(p.name) like :name escape '!' ")
                + (status == null ? "" : " and p.status=:status")
                + (countOnly ? "" : " order by p.id desc"));
    if (name != null)
      query.setParameter(
          "name",
          "%"
              + name.toLowerCase(Locale.ROOT)
                  .replace("!", "!!")
                  .replace("%", "!%")
                  .replace("_", "!_")
              + "%");
    if (status != null) query.setParameter("status", status);
    return query;
  }

  @SuppressWarnings("unchecked")
  public List<Project> list(String name, ProjectStatus status, int page, int size) {
    return filteredQuery(false, name, status)
        .setFirstResult(page * size)
        .setMaxResults(size)
        .getResultList();
  }

  public long count(String name, ProjectStatus status) {
    return ((Number) filteredQuery(true, name, status).getSingleResult()).longValue();
  }

  public PortfolioReport report() {
    PortfolioReport report = new PortfolioReport();
    for (Object row :
        em.createQuery("select p.status,count(p),sum(p.budget) from Project p group by p.status")
            .getResultList()) {
      Object[] a = (Object[]) row;
      report.projectsByStatus.put((ProjectStatus) a[0], ((Number) a[1]).longValue());
      report.budgetByStatus.put((ProjectStatus) a[0], (BigDecimal) a[2]);
    }
    long days = 0;
    List<?> rows =
        em.createQuery("select p.startDate,p.actualEndDate from Project p where p.status=:status")
            .setParameter("status", ProjectStatus.ENCERRADO)
            .getResultList();
    for (Object row : rows) {
      Object[] a = (Object[]) row;
      days +=
          ChronoUnit.DAYS.between(
              ProjectMapper.local((Date) a[0]), ProjectMapper.local((Date) a[1]));
    }
    report.averageCompletedDurationDays =
        rows.isEmpty()
            ? null
            : BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(rows.size()), 2, RoundingMode.HALF_UP);
    report.uniqueAllocatedMembers =
        ((Number)
                em.createQuery("select count(distinct m.id) from Project p join p.members m")
                    .getSingleResult())
            .longValue();
    return report;
  }

  public void audit(Project project, String action, String actor) {
    em.createNativeQuery(
            "insert into project_event(project_id,action,actor,occurred_at)"
                + " values(:id,:action,:actor,CURRENT_TIMESTAMP)")
        .setParameter("id", project.getId())
        .setParameter("action", action)
        .setParameter("actor", actor)
        .executeUpdate();
  }
}
