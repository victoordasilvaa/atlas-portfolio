package dev.portfolio.dto;

import dev.portfolio.domain.*;
import java.math.BigDecimal;
import java.util.List;

public class ProjectDto {
  public Long id;
  public int version;
  public String name, description, startDate, expectedEndDate, actualEndDate;

  @com.fasterxml.jackson.databind.annotation.JsonSerialize(
      using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
  public BigDecimal budget;

  public ProjectStatus status;
  public Risk risk;
  public MemberDto manager;
  public List<MemberDto> members;
}
