package dev.portfolio.entity;

import dev.portfolio.domain.ProjectStatus;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;
import org.jcompany.domain.PlcBaseMapEntity;

@Entity
@Table(name = "project")
public class Project extends PlcBaseMapEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq")
  @SequenceGenerator(name = "project_seq", sequenceName = "project_seq", allocationSize = 1)
  private Long id;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(nullable = false, length = 4000)
  private String description;

  @Temporal(TemporalType.DATE)
  @Column(name = "start_date", nullable = false)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  @Column(name = "expected_end_date", nullable = false)
  private Date expectedEndDate;

  @Temporal(TemporalType.DATE)
  @Column(name = "actual_end_date")
  private Date actualEndDate;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal budget;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ProjectStatus status = ProjectStatus.EM_ANALISE;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "manager_id")
  private Member manager;

  @ManyToMany
  @JoinTable(
      name = "project_member",
      joinColumns = @JoinColumn(name = "project_id"),
      inverseJoinColumns = @JoinColumn(name = "member_id"))
  private List<Member> members = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date d) {
    startDate = d;
  }

  public Date getExpectedEndDate() {
    return expectedEndDate;
  }

  public void setExpectedEndDate(Date d) {
    expectedEndDate = d;
  }

  public Date getActualEndDate() {
    return actualEndDate;
  }

  public void setActualEndDate(Date d) {
    actualEndDate = d;
  }

  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal b) {
    budget = b;
  }

  public ProjectStatus getStatus() {
    return status;
  }

  public void setStatus(ProjectStatus s) {
    status = s;
  }

  public Member getManager() {
    return manager;
  }

  public void setManager(Member m) {
    manager = m;
  }

  public List<Member> getMembers() {
    return members;
  }

  public void setMembers(List<Member> m) {
    members = m;
  }
}
