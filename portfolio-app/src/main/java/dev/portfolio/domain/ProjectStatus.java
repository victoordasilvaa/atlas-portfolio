package dev.portfolio.domain;

public enum ProjectStatus {
  EM_ANALISE,
  ANALISE_REALIZADA,
  ANALISE_APROVADA,
  INICIADO,
  PLANEJADO,
  EM_ANDAMENTO,
  ENCERRADO,
  CANCELADO;

  public boolean isActive() {
    return this != ENCERRADO && this != CANCELADO;
  }

  public boolean canDelete() {
    return this != INICIADO && this != EM_ANDAMENTO && this != ENCERRADO;
  }

  public void requireTransitionTo(ProjectStatus next) {
    if (next == null) throw BusinessException.invalid("Informe o status de destino.");
    if (next == this || next == CANCELADO) return;
    if (!isActive() || next.ordinal() != ordinal() + 1)
      throw BusinessException.conflict("A transição deve respeitar a sequência de status.");
  }
}
