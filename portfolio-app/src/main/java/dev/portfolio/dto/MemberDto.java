package dev.portfolio.dto;

public class MemberDto {
  public Long id;
  public String name, role;

  public MemberDto() {}

  public MemberDto(Long id, String name, String role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }
}
