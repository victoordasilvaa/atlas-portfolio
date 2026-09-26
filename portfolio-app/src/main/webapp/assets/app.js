"use strict";
const $ = (id) => document.getElementById(id);
const labels = {
  EM_ANALISE: "Em análise",
  ANALISE_REALIZADA: "Análise realizada",
  ANALISE_APROVADA: "Análise aprovada",
  INICIADO: "Iniciado",
  PLANEJADO: "Planejado",
  EM_ANDAMENTO: "Em andamento",
  ENCERRADO: "Encerrado",
  CANCELADO: "Cancelado",
};
const order = Object.keys(labels),
  money = (value) => {
    const [whole, fraction = "00"] = String(value).split(".");
    return (
      "R$ " +
      whole.replace(/\B(?=(\d{3})+(?!\d))/g, ".") +
      "," +
      fraction.padEnd(2, "0")
    );
  },
  date = (d) => (d ? d.split("-").reverse().join("/") : "—");
const canEdit = document.body.dataset.edit === "true";
let page = 0,
  current = null,
  directory = [],
  latest = 0;
function node(tag, text, cls) {
  const el = document.createElement(tag);
  if (text != null) el.textContent = text;
  if (cls) el.className = cls;
  return el;
}
function notify(message, error = false, target = "notice") {
  const el = $(target);
  el.textContent = message;
  el.className = "notice" + (error ? " error" : "");
  el.hidden = false;
}
async function api(path, options = {}) {
  const response = await fetch("api/" + path, {
    credentials: "same-origin",
    ...options,
    headers: {
      Accept: "application/json",
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...options.headers,
    },
  });
  let body = null;
  if (response.status !== 204) {
    const type = response.headers.get("Content-Type") || "";
    body = type.includes("json") ? await response.json() : null;
  }
  if (!response.ok)
    throw new Error(
      body?.message ||
        (response.status === 401
          ? "Sessão não autenticada. Recarregue a página."
          : response.status === 403
            ? "Seu perfil não permite esta operação."
            : "Não foi possível concluir a operação (" +
              response.status +
              ")."),
    );
  return body;
}
function badge(status) {
  return node(
    "span",
    labels[status],
    "pill " +
      (status === "ENCERRADO" || status === "EM_ANDAMENTO"
        ? "green"
        : status === "CANCELADO"
          ? "red"
          : status === "EM_ANALISE"
            ? "amber"
            : ""),
  );
}
async function load() {
  const token = ++latest;
  try {
    const params = new URLSearchParams({ page, size: 8 });
    if ($("search").value) params.set("name", $("search").value);
    if ($("status-filter").value)
      params.set("status", $("status-filter").value);
    const [data, report] = await Promise.all([
      api("projects?" + params),
      api("portfolio/report"),
    ]);
    if (token !== latest) return;
    $("metric-total").textContent = Object.values(
      report.projectsByStatus,
    ).reduce((a, b) => a + b, 0);
    $("metric-budget").textContent = money(report.totalBudget);
    $("metric-people").textContent = report.uniqueAllocatedMembers;
    $("metric-days").textContent =
      report.averageCompletedDurationDays == null
        ? "—"
        : new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 1 }).format(
            report.averageCompletedDurationDays,
          );
    $("projects").replaceChildren();
    for (const p of data.items) {
      const row = node("tr"),
        name = node("td");
      name.append(
        node("span", p.name, "project-name"),
        node("span", p.manager.name, "project-manager"),
      );
      row.append(name);
      const status = node("td");
      status.append(badge(p.status));
      row.append(status);
      const risk = node("td");
      risk.append(
        node(
          "span",
          "● " + { BAIXO: "Baixo", MEDIO: "Médio", ALTO: "Alto" }[p.risk],
          "risk " + p.risk,
        ),
      );
      row.append(
        risk,
        node("td", money(p.budget)),
        node("td", date(p.expectedEndDate)),
        node(
          "td",
          p.members.length + (p.members.length === 1 ? " pessoa" : " pessoas"),
        ),
      );
      const action = node("td"),
        button = node("button", "↗", "open-project");
      button.setAttribute("aria-label", "Abrir " + p.name);
      button.addEventListener("click", () => openDetail(p.id));
      action.append(button);
      row.append(action);
      $("projects").append(row);
    }
    $("empty").hidden = data.items.length !== 0;
    $("result-count").textContent = data.total + " projeto(s) encontrado(s)";
    $("page-label").textContent =
      "Página " + (page + 1) + " de " + Math.max(1, Math.ceil(data.total / 8));
    $("previous").disabled = page === 0;
    $("next").disabled = (page + 1) * 8 >= data.total;
  } catch (e) {
    notify(e.message, true);
  }
}
async function openEditor(project = null) {
  try {
    directory = await api("members");
    current = project;
    $("project-form").reset();
    $("editor-error").hidden = true;
    $("editor-title").textContent = project ? "Editar projeto" : "Novo projeto";
    $("manager").replaceChildren(node("option", "Selecione o gerente"));
    $("manager").firstChild.value = "";
    $("team").replaceChildren();
    for (const m of directory) {
      const option = node("option", m.name + " · " + m.role);
      option.value = m.id;
      $("manager").append(option);
      if (m.role.trim().toLowerCase() === "funcionário") {
        const label = node("label"),
          input = node("input");
        input.type = "checkbox";
        input.value = m.id;
        input.name = "memberIds";
        label.append(input, document.createTextNode(m.name));
        $("team").append(label);
      }
    }
    if (project) {
      for (const key of [
        "name",
        "description",
        "startDate",
        "expectedEndDate",
        "budget",
      ])
        $("project-form").elements[key].value = project[key];
      $("manager").value = project.manager.id;
      for (const check of $("team").querySelectorAll("input"))
        check.checked = project.members.some(
          (m) => String(m.id) === check.value,
        );
    }
    $("editor").showModal();
  } catch (e) {
    notify(e.message, true);
  }
}
async function openDetail(id) {
  try {
    current = await api("projects/" + id);
    const p = current;
    $("detail-title").textContent = p.name;
    $("detail-error").hidden = true;
    const body = $("detail-body");
    body.replaceChildren(
      badge(p.status),
      node("p", p.description, "detail-description"),
    );
    const grid = node("dl", null, "detail-grid");
    for (const [title, value] of [
      ["Orçamento", money(p.budget)],
      ["Risco", { BAIXO: "Baixo", MEDIO: "Médio", ALTO: "Alto" }[p.risk]],
      ["Início", date(p.startDate)],
      ["Previsão", date(p.expectedEndDate)],
      ["Conclusão real", date(p.actualEndDate)],
      ["Gerente", p.manager.name],
    ]) {
      const item = node("div");
      item.append(node("dt", title), node("dd", value));
      grid.append(item);
    }
    body.append(
      grid,
      node("h3", "Equipe"),
      node("p", p.members.map((m) => m.name).join(", ")),
    );
    const actions = $("detail-actions");
    actions.replaceChildren();
    if (canEdit) {
      const edit = node("button", "Editar", "secondary");
      edit.onclick = () => {
        $("detail").close();
        openEditor(p);
      };
      actions.append(edit);
      if (!["INICIADO", "EM_ANDAMENTO", "ENCERRADO"].includes(p.status)) {
        const del = node("button", "Excluir", "danger");
        del.onclick = async () => {
          if (!confirm("Excluir definitivamente este projeto?")) return;
          await mutate(del, () =>
            api("projects/" + p.id, {
              method: "DELETE",
              headers: { "If-Match": '"' + p.version + '"' },
            }),
          );
        };
        actions.append(del);
      }
      if (p.status !== "CANCELADO") {
        const cancel = node("button", "Cancelar projeto", "secondary");
        cancel.onclick = () => {
          if (confirm("Cancelar este projeto?"))
            transition(cancel, p, "CANCELADO");
        };
        actions.append(cancel);
      }
      if (!["CANCELADO", "ENCERRADO"].includes(p.status)) {
        const next = order[order.indexOf(p.status) + 1],
          button = node("button", "Avançar: " + labels[next], "primary");
        button.onclick = () => transition(button, p, next);
        actions.append(button);
      }
    }
    $("detail").showModal();
  } catch (e) {
    notify(e.message, true);
  }
}
async function transition(button, p, status) {
  let actualEndDate;
  if (status === "ENCERRADO") {
    actualEndDate = prompt(
      "Data real de término (AAAA-MM-DD):",
      new Date().toISOString().slice(0, 10),
    );
    if (actualEndDate === null) return;
  }
  await mutate(button, () =>
    api("projects/" + p.id + "/status", {
      method: "PATCH",
      body: JSON.stringify({
        status,
        version: p.version,
        ...(actualEndDate ? { actualEndDate } : {}),
      }),
    }),
  );
}
async function mutate(button, work) {
  button.disabled = true;
  try {
    await work();
    $("detail").close();
    notify("Projeto atualizado com sucesso.");
    await load();
  } catch (e) {
    notify(e.message, true, "detail-error");
  } finally {
    button.disabled = false;
  }
}
$("project-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.target),
    memberIds = form.getAll("memberIds").map(Number);
  if (memberIds.length < 1 || memberIds.length > 10) {
    notify("Selecione de 1 a 10 funcionários.", true, "editor-error");
    return;
  }
  const payload = {
    name: form.get("name"),
    description: form.get("description"),
    startDate: form.get("startDate"),
    expectedEndDate: form.get("expectedEndDate"),
    budget: form.get("budget"),
    managerId: Number(form.get("managerId")),
    memberIds,
  };
  if (current) payload.version = current.version;
  $("save-project").disabled = true;
  try {
    // Keep decimal digits intact in JSON instead of rounding monetary values through binary floating point.
    if (!/^\d+(?:\.\d{1,2})?$/.test(payload.budget))
      throw new Error("Informe um orçamento com até duas casas decimais.");
    const budget = payload.budget;
    delete payload.budget;
    const body =
      JSON.stringify(payload).slice(0, -1) + ',"budget":' + budget + "}";
    await api(current ? "projects/" + current.id : "projects", {
      method: current ? "PUT" : "POST",
      body,
    });
    $("editor").close();
    notify("Projeto salvo com sucesso.");
    page = 0;
    await load();
  } catch (e) {
    notify(e.message, true, "editor-error");
  } finally {
    $("save-project").disabled = false;
  }
});
for (const [value, label] of Object.entries(labels)) {
  const option = node("option", label);
  option.value = value;
  $("status-filter").append(option);
}
$("filters").onsubmit = (e) => {
  e.preventDefault();
  page = 0;
  load();
};
$("new-project").hidden = !canEdit;
$("new-project").onclick = () => openEditor();
$("previous").onclick = () => {
  page--;
  load();
};
$("next").onclick = () => {
  page++;
  load();
};
for (const b of document.querySelectorAll(".close-dialog"))
  b.onclick = () => b.closest("dialog").close();
load();
