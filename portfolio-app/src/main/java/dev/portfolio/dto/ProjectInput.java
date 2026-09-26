package dev.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProjectInput {
  public String name, description, startDate, expectedEndDate;
  public BigDecimal budget;
  public Long managerId;
  public List<Long> memberIds;
  public Integer version;
}
