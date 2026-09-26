package dev.portfolio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.portfolio.domain.*;
import dev.portfolio.dto.*;
import dev.portfolio.entity.*;
import dev.portfolio.integration.*;
import dev.portfolio.persistence.*;
import dev.portfolio.service.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class ProjectServiceTest {
  ProjectRepository repo;
  MemberDirectory directory;
  ProjectService service;

  @BeforeEach
  void setup() {
    repo = mock(ProjectRepository.class);
    directory = mock(MemberDirectory.class);
    service =
        new ProjectService(
            repo, directory, Clock.fixed(Instant.parse("2026-09-25T12:00:00Z"), ZoneOffset.UTC));
    when(directory.get(1L)).thenReturn(new MemberDto(1L, "Ana", "funcionário"));
    when(directory.get(2L)).thenReturn(new MemberDto(2L, "João", "gerente"));
    when(repo.snapshot(any()))
        .thenAnswer(
            i -> {
              MemberDto d = i.getArgument(0);
              return new Member(d.id, d.name, d.role);
            });
    doAnswer(
            i -> {
              ((Project) i.getArgument(0)).setId(7L);
              return null;
            })
        .when(repo)
        .insert(any(Project.class));
  }

  ProjectInput input() {
    ProjectInput d = new ProjectInput();
    d.name = "Projeto";
    d.description = "Descrição";
    d.startDate = "2026-01-01";
    d.expectedEndDate = "2026-04-01";
    d.budget = new BigDecimal("100000");
    d.managerId = 2L;
    d.memberIds = Arrays.asList(1L);
    d.version = 0;
    return d;
  }

  Project existing() {
    ProjectInput d = input();
    ProjectDto dto = service.create(d, "tester");
    Project p = new Project();
    p.setId(dto.id);
    p.setName(d.name);
    p.setDescription(d.description);
    p.setBudget(d.budget);
    p.setStartDate(ProjectMapper.sql(LocalDate.parse(d.startDate)));
    p.setExpectedEndDate(ProjectMapper.sql(LocalDate.parse(d.expectedEndDate)));
    p.setManager(new Member(2L, "João", "gerente"));
    p.getMembers().add(new Member(1L, "Ana", "funcionário"));
    when(repo.find(7)).thenReturn(p);
    return p;
  }

  @Test
  void createAndUpdate() {
    Project p = existing();
    ProjectInput d = input();
    d.name = " Alterado ";
    ProjectDto out = service.update(7, d, "ana");
    assertEquals("Alterado", out.name);
    assertEquals(Risk.BAIXO, out.risk);
    verify(repo).activeAllocations(1L, 7L);
    assertEquals("ana", p.getUserLastUpdate());
  }

  @Test
  void rejectFourthAllocation() {
    when(repo.activeAllocations(1L, null)).thenReturn(3L);
    assertThrows(BusinessException.class, () -> service.create(input(), "a"));
    verify(repo, never()).insert(any(Project.class));
  }

  @Test
  void transitionAndCompletion() {
    Project p = existing();
    for (ProjectStatus next :
        Arrays.asList(
            ProjectStatus.ANALISE_REALIZADA,
            ProjectStatus.ANALISE_APROVADA,
            ProjectStatus.INICIADO,
            ProjectStatus.PLANEJADO,
            ProjectStatus.EM_ANDAMENTO,
            ProjectStatus.ENCERRADO)) {
      StatusInput d = new StatusInput();
      d.status = next;
      d.version = 0;
      if (next == ProjectStatus.ENCERRADO) d.actualEndDate = "2026-09-01";
      assertEquals(next, service.transition(7, d, "a").status);
    }
    assertEquals("2026-09-01", service.get(7).actualEndDate);
    ProjectInput bad = input();
    bad.startDate = "2026-09-02";
    bad.expectedEndDate = "2026-10-01";
    assertThrows(BusinessException.class, () -> service.update(7, bad, "a"));
    service.update(7, input(), "a");
    StatusInput cancel = new StatusInput();
    cancel.status = ProjectStatus.CANCELADO;
    cancel.version = 0;
    service.transition(7, cancel, "a");
    assertEquals(ProjectStatus.CANCELADO, p.getStatus());
  }

  @Test
  void noOpStatusAndInvalidCompletion() {
    existing();
    StatusInput d = new StatusInput();
    d.version = 0;
    d.status = ProjectStatus.EM_ANALISE;
    service.transition(7, d, "a");
    d.actualEndDate = "2026-01-01";
    assertThrows(BusinessException.class, () -> service.transition(7, d, "a"));
    d.status = ProjectStatus.ANALISE_REALIZADA;
    assertThrows(BusinessException.class, () -> service.transition(7, d, "a"));
    assertThrows(BusinessException.class, () -> service.transition(7, null, "a"));
  }

  @Test
  void deleteAllowedAndProtected() {
    Project p = existing();
    service.delete(7, 0, "a");
    verify(repo).delete(p);
    p.setStatus(ProjectStatus.INICIADO);
    assertThrows(BusinessException.class, () -> service.delete(7, 0, "a"));
  }

  @Test
  void listReportAndBounds() {
    when(repo.list(null, null, 0, 20)).thenReturn(Collections.emptyList());
    assertEquals(0, service.list(null, null, 0, 20).total);
    service.report();
    verify(repo).report();
    for (int[] pair : new int[][] {{-1, 20}, {1000001, 20}, {0, 0}, {0, 101}})
      assertThrows(BusinessException.class, () -> service.list(null, null, pair[0], pair[1]));
    assertThrows(
        BusinessException.class,
        () -> service.list(String.join("", Collections.nCopies(161, "x")), null, 0, 20));
  }

  @Test
  void invalidInputs() {
    assertThrows(BusinessException.class, () -> service.create(null, "a"));
    ProjectInput d = input();
    d.name = " ";
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
    d.name = null;
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
    d.name = "ok";
    d.description = null;
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
    d.description = " ";
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
    d.description = "ok";
    d.managerId = null;
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
    d.managerId = 0L;
    assertThrows(BusinessException.class, () -> service.create(d, "a"));
  }

  @Test
  void invalidDirectoryAndRole() {
    when(directory.get(1)).thenReturn(null);
    assertThrows(BusinessException.class, () -> service.create(input(), "a"));
    when(directory.get(1)).thenReturn(new MemberDto(1L, "Ana", "gerente"));
    assertThrows(BusinessException.class, () -> service.create(input(), "a"));
    verify(repo, never()).insert(any(Project.class));
  }
}
