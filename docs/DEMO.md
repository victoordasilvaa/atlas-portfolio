# Apresentação técnica — roteiro de 8 minutos

## 1. Contexto e escopo — 45 segundos

Abra o README. Explique o problema: manter o ciclo de vida, os valores e as alocações consistentes. Mostre os três módulos e indique onde estão as adaptações do jCompany. Não descreva a solução como uma distribuição oficial ou como migração completa do framework.

## 2. Fluxo principal — 2 minutos

1. Entre como editor e cadastre um projeto com gerente 99, funcionários 1 e 2, orçamento 100.000 e duração de exatamente três meses.
2. Mostre risco baixo. Edite o orçamento para 100.000,01 e mostre risco médio.
3. Avance os status e demonstre que a interface oferece somente a próxima etapa.
4. Ao encerrar, informe a data real e confira o relatório consolidado.
5. Entre em outro perfil de navegador como viewer e mostre o acesso de leitura.

Prepare previamente as datas para que o encerramento não esteja no futuro.

## 3. Regra sob concorrência — 90 segundos

Abra `PersistenceIntegrationTest.concurrentFourthAllocationOnlyOneWins`. Dois clientes competem pela única vaga restante de um membro. Apenas um conclui a gravação, e a contagem final continua em três.

Explique por que `@Version` sozinho não protege uma regra que envolve várias linhas/projetos. A solução coordena gravações com uma trava de banco mantida até o commit. Explicite o compromisso: menor paralelismo de escrita em troca de correção simples para o escopo do exercício.

## 4. Uso concreto de jCompany — 1 minuto

Mostre a cadeia de herança: `PlcBaseMapEntity`, `PlcBaseAS`, `PlcBaseFacadeImpl` e `PlcBaseJpaDAO`. Abra a chamada `super.insert(project)`. O módulo de compatibilidade compila fontes reais, com manifesto e patch. Explique que JAX-RS é uma extensão permitida para REST e que o controlador encaminha a visualização para JSP em WEB-INF.

## 5. Qualidade verificável — 90 segundos

Execute `./mvnw verify` (ou `mvnw.cmd verify`). Mostre o relatório JaCoCo e destaque seu escopo: domínio e serviço, sem contar o código vendor. Mostre os testes de integração HTTP, autenticação do Tomcat e consulta ao diretório externo. Apresente o contrato OpenAPI e uma resposta 409 por versão desatualizada.

## 6. Decisões e limites — 1 minuto

Esteja pronto para justificar:

- Meses de calendário e prioridade do risco mais alto.
- A lacuna dos centavos acima de 100.000.
- Exclusão de planejado permitida pela regra literal.
- Gerente separado da equipe e contagem de membros únicos.
- Datas reais registradas no encerramento e DTOs de escrita sem status.
- Mock externo volátil, timeout HTTP e snapshots persistidos.
- Testes H2 locais versus execução do contrato em PostgreSQL no CI.
- Runtime legado adaptado: o que foi preservado, o que foi alterado e o que não foi validado.

Evite prometer alta escala ou compatibilidade irrestrita. A força da apresentação está em mostrar evidências e explicar os compromissos técnicos com precisão.
