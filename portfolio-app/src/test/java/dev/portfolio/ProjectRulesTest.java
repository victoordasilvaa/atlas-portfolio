package dev.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import dev.portfolio.domain.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ProjectRulesTest {
  private final LocalDate start = LocalDate.of(2026, 1, 31);

  @ParameterizedTest
  @CsvSource({
    "100000,2026-04-30,BAIXO",
    "100000.01,2026-04-30,MEDIO",
    "500000,2026-07-31,MEDIO",
    "500000.01,2026-02-01,ALTO",
    "1,2026-08-01,ALTO",
    "0,2026-05-01,MEDIO",
    "0,2026-01-31,BAIXO"
  })
  void riskBoundaries(String budget, String end, Risk expected) {
    assertEquals(expected, ProjectRules.risk(new BigDecimal(budget), start, LocalDate.parse(end)));
  }

  @Test
  void transitionsExhaustive() {
    for (ProjectStatus a : ProjectStatus.values())
      for (ProjectStatus b : ProjectStatus.values()) {
        boolean allowed =
            a == b
                || b == ProjectStatus.CANCELADO
                || a.isActive() && b.ordinal() == a.ordinal() + 1;
        if (allowed) assertDoesNotThrow(() -> a.requireTransitionTo(b), a + " -> " + b);
        else assertThrows(BusinessException.class, () -> a.requireTransitionTo(b), a + " -> " + b);
      }
  }

  @Test
  void deletionAndActiveStates() {
    for (ProjectStatus s : ProjectStatus.values()) {
      assertEquals(
          !EnumSet.of(ProjectStatus.INICIADO, ProjectStatus.EM_ANDAMENTO, ProjectStatus.ENCERRADO)
              .contains(s),
          s.canDelete());
      assertEquals(
          !EnumSet.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO).contains(s), s.isActive());
    }
  }

  @Test
  void invalidDatesAndBudget() {
    assertThrows(BusinessException.class, () -> ProjectRules.risk(null, start, start));
    assertThrows(
        BusinessException.class, () -> ProjectRules.risk(new BigDecimal("-1"), start, start));
    assertThrows(BusinessException.class, () -> ProjectRules.validateDates(null, start));
    assertThrows(BusinessException.class, () -> ProjectRules.validateDates(start, null));
    assertThrows(
        BusinessException.class, () -> ProjectRules.validateDates(start, start.minusDays(1)));
    for (BigDecimal b :
        Arrays.asList(
            null,
            new BigDecimal("-1"),
            new BigDecimal("0.001"),
            new BigDecimal("10000000000000000")))
      assertThrows(BusinessException.class, () -> ProjectRules.validateBudget(b));
    assertDoesNotThrow(() -> ProjectRules.validateBudget(new BigDecimal("10.000")));
  }

  @Test
  void teamAndCapacity() {
    ProjectRules.validateTeam(Arrays.asList(1L));
    ProjectRules.validateTeam(LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList()));
    for (List<Long> ids :
        Arrays.asList(
            Collections.<Long>emptyList(),
            Arrays.asList(1L, 1L),
            Arrays.asList(0L),
            Arrays.asList((Long) null),
            LongStream.rangeClosed(1, 11).boxed().collect(Collectors.toList())))
      assertThrows(BusinessException.class, () -> ProjectRules.validateTeam(ids));
    assertThrows(BusinessException.class, () -> ProjectRules.validateTeam(null));
    ProjectRules.requireCapacity(2);
    assertThrows(BusinessException.class, () -> ProjectRules.requireCapacity(3));
    ProjectRules.requireEmployee(" FUNCIONÁRIO ");
    assertThrows(BusinessException.class, () -> ProjectRules.requireEmployee("gerente"));
    assertThrows(BusinessException.class, () -> ProjectRules.requireEmployee(null));
  }

  @Test
  void versionAndCompletion() {
    ProjectRules.requireVersion(1, 1);
    assertThrows(BusinessException.class, () -> ProjectRules.requireVersion(1, null));
    assertThrows(BusinessException.class, () -> ProjectRules.requireVersion(1, 0));
    ProjectRules.validateCompletion(start, start, start);
    assertThrows(
        BusinessException.class, () -> ProjectRules.validateCompletion(start, null, start));
    assertThrows(
        BusinessException.class,
        () -> ProjectRules.validateCompletion(start, start.minusDays(1), start));
    assertThrows(
        BusinessException.class,
        () -> ProjectRules.validateCompletion(start, start.plusDays(1), start));
    assertThrows(BusinessException.class, () -> ProjectStatus.EM_ANALISE.requireTransitionTo(null));
  }
}
