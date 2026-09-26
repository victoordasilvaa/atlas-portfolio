package dev.portfolio.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Policies are shared by HTTP, MVC and persistence orchestration. */
public final class ProjectRules {
  private ProjectRules() {}

  public static Risk risk(BigDecimal budget, LocalDate start, LocalDate expected) {
    validateDates(start, expected);
    if (budget == null || budget.signum() < 0)
      throw BusinessException.invalid("Orçamento deve ser não negativo.");
    if (budget.compareTo(new BigDecimal("500000")) > 0 || expected.isAfter(start.plusMonths(6)))
      return Risk.ALTO;
    if (budget.compareTo(new BigDecimal("100000")) > 0 || expected.isAfter(start.plusMonths(3)))
      return Risk.MEDIO;
    return Risk.BAIXO;
  }

  public static void validateDates(LocalDate start, LocalDate expected) {
    if (start == null || expected == null || expected.isBefore(start))
      throw BusinessException.invalid("Datas obrigatórias; previsão não pode anteceder o início.");
  }

  public static void validateBudget(BigDecimal budget) {
    if (budget == null
        || budget.signum() < 0
        || budget.compareTo(new BigDecimal("9999999999999999.99")) > 0)
      throw BusinessException.invalid("Orçamento fora do intervalo permitido.");
    try {
      budget.setScale(2, java.math.RoundingMode.UNNECESSARY);
    } catch (ArithmeticException e) {
      throw BusinessException.invalid("Orçamento admite no máximo duas casas decimais.");
    }
  }

  public static void validateTeam(List<Long> ids) {
    if (ids == null || ids.size() < 1 || ids.size() > 10)
      throw BusinessException.invalid("A equipe deve conter de 1 a 10 membros.");
    if (ids.stream().anyMatch(id -> id == null || id <= 0)
        || new HashSet<>(ids).size() != ids.size())
      throw BusinessException.invalid(
          "Membros devem possuir identificadores positivos e distintos.");
  }

  public static void requireEmployee(String role) {
    if (role == null || !"funcionário".equalsIgnoreCase(role.trim()))
      throw BusinessException.invalid(
          "Apenas membros com atribuição funcionário podem integrar a equipe.");
  }

  public static void requireCapacity(long otherActiveProjects) {
    if (otherActiveProjects >= 3)
      throw BusinessException.conflict("Membro já alocado em três projetos ativos.");
  }

  public static void requireVersion(int actual, Integer expected) {
    if (expected == null)
      throw new BusinessException("VERSION_REQUIRED", "Informe a versão atual do projeto.", 428);
    if (actual != expected)
      throw new BusinessException(
          "STALE_VERSION", "O projeto foi alterado. Atualize os dados antes de salvar.", 409);
  }

  public static void validateCompletion(LocalDate start, LocalDate actual, LocalDate today) {
    if (actual == null || actual.isBefore(start) || actual.isAfter(today))
      throw BusinessException.invalid("Data real deve estar entre o início e hoje.");
  }
}
