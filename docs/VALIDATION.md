# Evidências de validação

Data: 25/09/2026. Java OpenJDK 17.0.20, Maven 3.9.9.

## Executado nesta entrega

- `mvn verify`: **BUILD SUCCESS**.
- **40 testes executados**, zero falhas, zero erros, zero testes ignorados.
- JaCoCo em domínio + serviço: **98.93% das linhas** (185/187) e **92.47% das decisões**.
- Matriz completa de transições, orçamento e datas de fronteira, alocação, bloqueio de exclusão e conflito de versão.
- CRUD e relatório com JPA/Hibernate reais, rollback e disputa concorrente pela última alocação disponível, usando H2.
- HTTP/JAX-RS em Tomcat: autenticação, perfil viewer, criação, status, erros JSON, versão obrigatória e compilação da JSP.
- Diretório HTTP: sucesso, inexistente, erro remoto, resposta inválida e limite de tamanho. Mock: criação, consulta, validação e dados iniciais.
- WAR implantado em Tomcat com classpath do contêiner isolado das classes da aplicação/testes. Hibernate validou o esquema de teste; H2 usado como infraestrutura local.
- Navegador Chromium: criação e edição pela interface contra o WAR empacotado, sem erros JavaScript. Layout conferido em desktop e 390 px, sem rolagem horizontal da página. A tabela mantém sua própria rolagem horizontal no celular.
- Sintaxe dos arquivos YAML, XML e JavaScript verificada.

## Validação no GitHub Actions — 26/09/2026

Execução [Verify #3](https://github.com/victoordasilvaa/atlas-portfolio/actions/runs/36246659358), commit `b1ed0cff9fe20a8f73f7711c675038b0762df53b`:

- **PostgreSQL 16.6 real: aprovado.** `./mvnw verify` concluiu com 40 testes, zero falhas, erros ou testes ignorados. A suíte de persistência aplicou `database/001-schema.sql` em schema isolado e verificou CRUD, versão, relatório e alocação concorrente.
- **Docker Compose: aprovado.** As imagens foram construídas a partir do código; os dois WARs e o PostgreSQL subiram juntos. `docker/smoke.py` confirmou criação, consulta e exclusão, restrição do perfil viewer, orçamento decimal, relatório e resposta da JSP. Os contêineres e volumes temporários foram removidos ao final.
- **Publicação:** código disponível em [victoordasilvaa/atlas-portfolio](https://github.com/victoordasilvaa/atlas-portfolio), com commits separados por implementação, testes, documentação e CI.

O build das imagens também executa os 40 testes com H2. Isso complementa a suíte PostgreSQL do job `test`; não a substitui. Esta validação funcional não representa teste de carga ou certificação de segurança.

## Relatórios reproduzíveis

O ZIP inclui `dist/coverage/index.html`, os WARs e `docs/verification.json`. Após recompilar, os relatórios ficam em `portfolio-app/target/site/jacoco` e nos diretórios Surefire de cada módulo. O gate exige 70% de linhas de domínio e serviço. O código do framework não entra nesse cálculo.

`docs/dashboard.png` é uma captura da interface executada, com dados sintéticos criados pela API exclusivamente para a demonstração; não é imagem de uma aplicação externa nem seed permanente do banco.
