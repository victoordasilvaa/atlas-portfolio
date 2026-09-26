package dev.portfolio.dto;

import dev.portfolio.domain.*;
import dev.portfolio.entity.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public final class ProjectMapper {
  private ProjectMapper() {}

  public static LocalDate date(String text) {
    try {
      if (text == null) throw BusinessException.invalid("Informe a data no formato AAAA-MM-DD.");
      return LocalDate.parse(text);
    } catch (DateTimeParseException e) {
      throw BusinessException.invalid("Data inválida. Use AAAA-MM-DD.");
    }
  }

  public static LocalDate local(java.util.Date date) {
    return new java.sql.Date(date.getTime()).toLocalDate();
  }

  public static java.util.Date sql(LocalDate date) {
    return java.sql.Date.valueOf(date);
  }

  public static MemberDto member(Member member) {
    return new MemberDto(member.getId(), member.getName(), member.getRole());
  }

  public static ProjectDto project(Project project) {
    ProjectDto dto = new ProjectDto();
    dto.id = project.getId();
    dto.version = project.getVersion();
    dto.name = project.getName();
    dto.description = project.getDescription();
    dto.startDate = local(project.getStartDate()).toString();
    dto.expectedEndDate = local(project.getExpectedEndDate()).toString();
    dto.actualEndDate =
        project.getActualEndDate() == null ? null : local(project.getActualEndDate()).toString();
    dto.budget = project.getBudget();
    dto.status = project.getStatus();
    dto.risk =
        ProjectRules.risk(
            dto.budget, local(project.getStartDate()), local(project.getExpectedEndDate()));
    dto.manager = member(project.getManager());
    dto.members =
        project.getMembers().stream().map(ProjectMapper::member).collect(Collectors.toList());
    return dto;
  }
}
