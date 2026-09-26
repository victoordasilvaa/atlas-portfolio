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

## Ainda depende do ambiente de destino

- **PostgreSQL real:** não foi executado nesta sessão. A suíte de persistência aceita `TEST_POSTGRES_URL`, aplica a migração SQL real em schema isolado e roda o mesmo contrato. O workflow GitHub Actions configura esse banco.
- **Docker Compose:** configuração entregue, mas não executada aqui; este ambiente não disponibiliza Docker.
- **GitHub:** nenhum repositório foi criado ou publicado na conta pessoal. Instruções no README.

Esses limites não estão escondidos atrás de testes H2. Antes do envio definitivo, confirme o workflow com PostgreSQL e a subida por Compose.

## Relatórios reproduzíveis

O ZIP inclui `dist/coverage/index.html`, os WARs e `docs/verification.json`. Após recompilar, os relatórios ficam em `portfolio-app/target/site/jacoco` e nos diretórios Surefire de cada módulo. O gate exige 70% de linhas de domínio e serviço. O código do framework não entra nesse cálculo.

`docs/dashboard.png` é uma captura da interface executada, com dados sintéticos criados pela API exclusivamente para a demonstração; não é imagem de uma aplicação externa nem seed permanente do banco.
