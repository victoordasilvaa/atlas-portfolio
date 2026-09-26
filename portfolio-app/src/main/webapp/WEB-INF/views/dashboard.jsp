<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Atlas · Portfólio de projetos</title>
    <link rel="stylesheet" href="assets/app.css">
    <script defer src="assets/app.js">
    </script>
  </head>
  <body data-edit="${canEdit}">
    <aside class="sidebar">
      <a class="brand" href="dashboard">
        <span class="brand-icon">A</span> atlas<span class="brand-dot">.</span>
      </a>
      <div class="workspace">WORKSPACE<br>
        <strong>Gestão de portfólio</strong>
      </div>
      <nav>
        <a class="active" href="dashboard">◈ <span>Visão geral</span>
        </a>
        <a href="openapi.yaml">↗ <span>Contrato da API</span>
        </a>
      </nav>
      <div class="sidebar-bottom">
        <span class="avatar">●</span>
        <div>
          <strong>
            <c:out value="${username}"/>
          </strong>
          <small>
            <c:choose>
              <c:when test="${canEdit}">Editor do portfólio</c:when>
              <c:otherwise>Somente leitura</c:otherwise>
            </c:choose>
          </small>
        </div>
      </div>
    </aside>
    <main>
      <header class="topbar">
        <span>Workspace <span class="separator">/</span> Portfólio</span>
        <span class="environment">● Ambiente de demonstração</span>
      </header>
      <section class="content">
        <div class="heading">
          <div>
            <div class="eyebrow">CLAREZA PARA CADA DECISÃO</div>
            <h1>Seu portfólio, em perspectiva.</h1>
            <p>Acompanhe investimentos, equipes e a evolução dos seus projetos.</p>
          </div>
          <button class="primary" id="new-project">+ Novo projeto</button>
        </div>
        <div id="notice" class="notice" role="status" aria-live="polite" hidden>
        </div>
        <div class="metrics">
          <article>
            <span>Projetos no portfólio</span>
            <strong id="metric-total">—</strong>
            <small>Todos os estágios</small>
          </article>
          <article>
            <span>Investimento previsto</span>
            <strong id="metric-budget">—</strong>
            <small>Orçamento total consolidado</small>
          </article>
          <article>
            <span>Pessoas alocadas</span>
            <strong id="metric-people">—</strong>
            <small>Membros únicos nas equipes</small>
          </article>
          <article>
            <span>Duração média</span>
            <strong id="metric-days">—</strong>
            <small>Dias · projetos encerrados</small>
          </article>
        </div>
        <div class="portfolio-panel">
          <div class="panel-title">
            <div>
              <h2>Projetos</h2>
              <p id="result-count">Carregando portfólio…</p>
            </div>
            <form id="filters">
              <label class="sr-only" for="search">Buscar projeto</label>
              <input id="search" placeholder="Buscar pelo nome…" maxlength="160">
              <label class="sr-only" for="status-filter">Filtrar status</label>
              <select id="status-filter">
                <option value="">Todos os status</option>
              </select>
              <button type="submit" class="secondary">Filtrar</button>
            </form>
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>PROJETO / GERENTE</th>
                  <th>STATUS</th>
                  <th>RISCO</th>
                  <th>ORÇAMENTO</th>
                  <th>PREVISÃO</th>
                  <th>EQUIPE</th>
                  <th>
                    <span class="sr-only">Ações</span>
                  </th>
                </tr>
              </thead>
              <tbody id="projects">
              </tbody>
            </table>
          </div>
          <div id="empty" class="empty" hidden>
            <span>◇</span>
            <h3>Um bom projeto começa aqui.</h3>
            <p>Cadastre o primeiro projeto ou ajuste os filtros para continuar.</p>
          </div>
          <footer class="pagination">
            <span id="page-label">Página 1</span>
            <div>
              <button class="secondary" id="previous">← Anterior</button>
              <button class="secondary" id="next">Próxima →</button>
            </div>
          </footer>
        </div>
        <div class="bottom-note">
          <span>● Dados atualizados a cada consulta</span>
          <span>Atlas / Portfolio Management</span>
        </div>
      </section>
    </main>
    <dialog id="editor">
      <form id="project-form">
        <header class="dialog-header">
          <div>
            <div class="eyebrow">PORTFÓLIO</div>
            <h2 id="editor-title">Novo projeto</h2>
          </div>
          <button type="button" class="icon-button close-dialog" aria-label="Fechar">×</button>
        </header>
        <div id="editor-error" class="notice error" role="alert" hidden>
        </div>
        <div class="form-grid">
          <label class="full">Nome do projeto<input name="name" required maxlength="160" placeholder="Ex.: Expansão da plataforma digital">
          </label>
          <label>Data de início<input name="startDate" type="date" required>
          </label>
          <label>Previsão de término<input name="expectedEndDate" type="date" required>
          </label>
          <label>Orçamento (R$)<input name="budget" type="number" min="0" step="0.01" required placeholder="0,00">
          </label>
          <label>Gerente responsável<select name="managerId" required id="manager">
            </select>
          </label>
          <label class="full">Descrição<textarea name="description" required maxlength="4000" rows="3" placeholder="Objetivo, contexto e resultado esperado.">
            </textarea>
          </label>
          <fieldset class="full">
            <legend>Equipe <span>Selecione de 1 a 10 funcionários</span>
            </legend>
            <div id="team" class="team-options">
            </div>
          </fieldset>
        </div>
        <footer class="dialog-footer">
          <span id="risk-preview">Risco calculado a partir de orçamento e prazo.</span>
          <button type="button" class="secondary close-dialog">Cancelar</button>
          <button class="primary" type="submit" id="save-project">Salvar projeto</button>
        </footer>
      </form>
    </dialog>
    <dialog id="detail">
      <header class="dialog-header">
        <div>
          <div class="eyebrow">DETALHES DO PROJETO</div>
          <h2 id="detail-title">
          </h2>
        </div>
        <button class="icon-button close-dialog" aria-label="Fechar">×</button>
      </header>
      <div id="detail-body">
      </div>
      <div id="detail-error" class="notice error" role="alert" hidden>
      </div>
      <footer class="dialog-footer" id="detail-actions">
      </footer>
    </dialog>
  </body>
</html>
