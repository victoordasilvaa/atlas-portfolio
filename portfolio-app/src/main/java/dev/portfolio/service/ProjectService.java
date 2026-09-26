package dev.portfolio.service;

import dev.portfolio.domain.*;
import dev.portfolio.dto.*;
import dev.portfolio.entity.*;
import dev.portfolio.integration.MemberDirectory;
import dev.portfolio.persistence.ProjectRepository;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.jcompany.model.PlcBaseAS;

/** Application Service in the jCompany AS/DAO convention; transaction belongs to the facade. */
public class ProjectService extends PlcBaseAS {
  private final ProjectRepository repository;
  private final MemberDirectory directory;
  private final Clock clock;

  public ProjectService(ProjectRepository repository, MemberDirectory directory, Clock clock) {
    this.repository = repository;
    this.directory = directory;
    this.clock = clock;
  }

  public ProjectDto create(ProjectInput input, String actor) {
    validateInput(input);
    Map<Long, MemberDto> members = resolveMembers(input);
    repository.lockWrites();
    Project project = new Project();
    applyChanges(project, input, members);
    recordLastUpdate(project, actor);
    repository.insert(project);
    repository.audit(project, "CREATED", actor);
    repository.flush();
    return ProjectMapper.project(project);
  }

  public ProjectDto update(long id, ProjectInput input, String actor) {
    validateInput(input);
    Map<Long, MemberDto> members = resolveMembers(input);
    repository.lockWrites();
    Project project = repository.find(id);
    ProjectRules.requireVersion(project.getVersion(), input.version);
    if (project.getActualEndDate() != null)
      ProjectRules.validateCompletion(
          ProjectMapper.date(input.startDate),
          ProjectMapper.local(project.getActualEndDate()),
          LocalDate.now(clock));
    applyChanges(project, input, members);
    recordLastUpdate(project, actor);
    repository.audit(project, "UPDATED", actor);
    repository.flush();
    return ProjectMapper.project(project);
  }

  public ProjectDto transition(long id, StatusInput input, String actor) {
    if (input == null) throw BusinessException.invalid("Informe o corpo da requisição.");
    repository.lockWrites();
    Project project = repository.find(id);
    ProjectRules.requireVersion(project.getVersion(), input.version);
    project.getStatus().requireTransitionTo(input.status);
    if (input.status == project.getStatus()) {
      if (input.actualEndDate != null
          && (project.getActualEndDate() == null
              || !ProjectMapper.local(project.getActualEndDate())
                  .equals(ProjectMapper.date(input.actualEndDate))))
        throw BusinessException.invalid("A repetição de status não pode alterar a data real.");
      return ProjectMapper.project(project);
    }
    if (input.status == ProjectStatus.ENCERRADO) {
      LocalDate actual = ProjectMapper.date(input.actualEndDate);
      ProjectRules.validateCompletion(
          ProjectMapper.local(project.getStartDate()), actual, LocalDate.now(clock));
      project.setActualEndDate(ProjectMapper.sql(actual));
    } else if (input.actualEndDate != null)
      throw BusinessException.invalid("Data real deve ser informada no encerramento.");
    ProjectStatus previous = project.getStatus();
    project.setStatus(input.status);
    recordLastUpdate(project, actor);
    repository.audit(project, previous + " -> " + input.status, actor);
    repository.flush();
    return ProjectMapper.project(project);
  }

  public void delete(long id, Integer version, String actor) {
    repository.lockWrites();
    Project project = repository.find(id);
    ProjectRules.requireVersion(project.getVersion(), version);
    if (!project.getStatus().canDelete())
      throw BusinessException.conflict(
          "Projetos iniciados, em andamento ou encerrados não podem ser excluídos.");
    repository.audit(project, "DELETED", actor);
    repository.delete(project);
    repository.flush();
  }

  public ProjectDto get(long id) {
    return ProjectMapper.project(repository.find(id));
  }

  public PageDto<ProjectDto> list(String name, ProjectStatus status, int page, int size) {
    if (page < 0 || page > 1000000 || size < 1 || size > 100)
      throw BusinessException.invalid("Página deve ser de 0 a 1000000 e tamanho de 1 a 100.");
    if (name != null && name.length() > 160)
      throw BusinessException.invalid("Filtro de nome muito longo.");
    return new PageDto<>(
        repository.list(name, status, page, size).stream()
            .map(ProjectMapper::project)
            .collect(Collectors.toList()),
        repository.count(name, status),
        page,
        size);
  }

  public PortfolioReport report() {
    return repository.report();
  }

  private void validateInput(ProjectInput input) {
    if (input == null) throw BusinessException.invalid("Informe o corpo da requisição.");
    if (input.name == null || input.name.trim().isEmpty() || input.name.trim().length() > 160)
      throw BusinessException.invalid("Nome obrigatório, limitado a 160 caracteres.");
    if (input.description == null
        || input.description.trim().isEmpty()
        || input.description.length() > 4000)
      throw BusinessException.invalid("Descrição obrigatória, limitada a 4000 caracteres.");
    if (input.managerId == null || input.managerId <= 0)
      throw BusinessException.invalid("Informe o gerente responsável.");
    ProjectRules.validateDates(
        ProjectMapper.date(input.startDate), ProjectMapper.date(input.expectedEndDate));
    ProjectRules.validateBudget(input.budget);
    ProjectRules.validateTeam(input.memberIds);
  }

  private Map<Long, MemberDto> resolveMembers(ProjectInput input) {
    Set<Long> ids = new LinkedHashSet<>(input.memberIds);
    ids.add(input.managerId);
    Map<Long, MemberDto> result = new HashMap<>();
    for (Long id : ids) {
      MemberDto member = directory.get(id);
      if (member == null
          || !id.equals(member.id)
          || member.name == null
          || member.name.trim().isEmpty()
          || member.name.length() > 160
          || member.role == null
          || member.role.length() > 80)
        throw new BusinessException(
            "DIRECTORY_INVALID", "Resposta inválida do serviço de membros.", 502);
      if (input.memberIds.contains(id)) ProjectRules.requireEmployee(member.role);
      result.put(id, member);
    }
    return result;
  }

  private void applyChanges(Project project, ProjectInput input, Map<Long, MemberDto> members) {
    if (project.getStatus().isActive())
      for (Long id : input.memberIds)
        ProjectRules.requireCapacity(repository.activeAllocations(id, project.getId()));
    project.setName(input.name.trim());
    project.setDescription(input.description.trim());
    project.setStartDate(ProjectMapper.sql(ProjectMapper.date(input.startDate)));
    project.setExpectedEndDate(ProjectMapper.sql(ProjectMapper.date(input.expectedEndDate)));
    project.setBudget(input.budget.setScale(2));
    project.setManager(repository.snapshot(members.get(input.managerId)));
    project.getMembers().clear();
    for (Long id : input.memberIds) project.getMembers().add(repository.snapshot(members.get(id)));
  }

  private void recordLastUpdate(Project project, String actor) {
    project.setUserLastUpdate(actor);
    project.setDateLastUpdate(Date.from(clock.instant()));
  }
}
