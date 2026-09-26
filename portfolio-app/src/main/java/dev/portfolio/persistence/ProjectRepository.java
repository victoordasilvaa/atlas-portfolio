package dev.portfolio.persistence;

import dev.portfolio.domain.ProjectStatus;
import dev.portfolio.dto.*;
import dev.portfolio.entity.*;
import java.util.List;

public interface ProjectRepository {
  void lockWrites();

  Project find(long id);

  void insert(Project project);

  void delete(Project project);

  void flush();

  Member snapshot(MemberDto member);

  long activeAllocations(long memberId, Long excludedProjectId);

  List<Project> list(String name, ProjectStatus status, int page, int size);

  long count(String name, ProjectStatus status);

  PortfolioReport report();

  void audit(Project project, String action, String actor);
}
