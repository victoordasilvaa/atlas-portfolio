package dev.portfolio.dto;

import dev.portfolio.domain.ProjectStatus;
import java.math.BigDecimal;
import java.util.*;

public class PortfolioReport {
  public final Map<ProjectStatus, Long> projectsByStatus = new LinkedHashMap<>();

  @com.fasterxml.jackson.databind.annotation.JsonSerialize(
      contentUsing = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
  public final Map<ProjectStatus, BigDecimal> budgetByStatus = new LinkedHashMap<>();

  public BigDecimal averageCompletedDurationDays;
  public long uniqueAllocatedMembers;

  public PortfolioReport() {
    for (ProjectStatus s : ProjectStatus.values()) {
      projectsByStatus.put(s, 0L);
      budgetByStatus.put(s, BigDecimal.ZERO.setScale(2));
    }
  }

  @com.fasterxml.jackson.databind.annotation.JsonSerialize(
      using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
  public BigDecimal getTotalBudget() {
    return budgetByStatus.values().stream().reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
  }
}
