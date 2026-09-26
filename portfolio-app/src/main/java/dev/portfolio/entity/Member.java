package dev.portfolio.entity;

import javax.persistence.*;
import org.jcompany.domain.PlcBaseMapEntity;

@Entity
@Table(name = "member_snapshot")
public class Member extends PlcBaseMapEntity {
  @Id private Long id;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(nullable = false, length = 80)
  private String role;

  public Member() {}

  public Member(Long id, String name, String role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }

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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
