const state = {
    token: localStorage.getItem("neural-cutting-token") || "",
    currentUser: readJson("neural-cutting-user"),
    selectedResumeId: localStorage.getItem("neural-cutting-selected-resume-id") || "",
    selectedVersionId: localStorage.getItem("neural-cutting-selected-version-id") || "",
    selectedVacancyId: localStorage.getItem("neural-cutting-selected-vacancy-id") || "",
    selectedJobId: localStorage.getItem("neural-cutting-selected-job-id") || ""
};

const elements = {
    authState: document.getElementById("auth-state"),
    currentUserId: document.getElementById("current-user-id"),
    currentUserEmail: document.getElementById("current-user-email"),
    tokenPreview: document.getElementById("token-preview"),
    selectedResumeId: document.getElementById("selected-resume-id"),
    selectedVersionId: document.getElementById("selected-version-id"),
    selectedVacancyId: document.getElementById("selected-vacancy-id"),
    selectedJobId: document.getElementById("selected-job-id"),
    responseOutput: document.getElementById("response-output"),
    resumesList: document.getElementById("resumes-list"),
    versionsList: document.getElementById("versions-list"),
    vacanciesList: document.getElementById("vacancies-list"),
    updateResumeId: document.getElementById("update-resume-id"),
    versionResumeId: document.getElementById("version-resume-id"),
    uploadResumeId: document.getElementById("upload-resume-id"),
    analysisVersionId: document.getElementById("analysis-version-id"),
    analysisVacancyId: document.getElementById("analysis-vacancy-id")
};

document.getElementById("register-form").addEventListener("submit", handleRegister);
document.getElementById("login-form").addEventListener("submit", handleLogin);
document.getElementById("logout-button").addEventListener("click", logout);
document.getElementById("me-button").addEventListener("click", () => callAndRender("GET", "/api/auth/me"));
document.getElementById("health-check-button").addEventListener("click", () => callAndRender("GET", "/actuator/health", null, false));

document.getElementById("create-resume-form").addEventListener("submit", handleCreateResume);
document.getElementById("update-resume-form").addEventListener("submit", handleUpdateResume);
document.getElementById("load-resumes-button").addEventListener("click", loadResumes);
document.getElementById("resume-detail-button").addEventListener("click", loadSelectedResumeDetail);
document.getElementById("delete-resume-button").addEventListener("click", handleDeleteResume);

document.getElementById("create-version-text-form").addEventListener("submit", handleCreateTextVersion);
document.getElementById("upload-version-form").addEventListener("submit", handleUploadVersion);
document.getElementById("load-versions-button").addEventListener("click", loadVersions);
document.getElementById("version-detail-button").addEventListener("click", loadSelectedVersionDetail);
document.getElementById("history-button").addEventListener("click", loadAnalysisHistory);

document.getElementById("create-vacancy-form").addEventListener("submit", handleCreateVacancy);
document.getElementById("load-vacancies-button").addEventListener("click", loadVacancies);
document.getElementById("vacancy-detail-button").addEventListener("click", loadSelectedVacancyDetail);

document.getElementById("create-analysis-form").addEventListener("submit", handleCreateAnalysisJob);
document.getElementById("job-detail-button").addEventListener("click", loadSelectedJob);
document.getElementById("job-result-button").addEventListener("click", loadSelectedJobResult);

renderState();
if (state.token) {
    bootstrapAuthenticatedView();
}

function readJson(key) {
    const raw = localStorage.getItem(key);
    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw);
    } catch {
        return null;
    }
}

function persistState() {
    localStorage.setItem("neural-cutting-token", state.token);
    if (state.currentUser) {
        localStorage.setItem("neural-cutting-user", JSON.stringify(state.currentUser));
    } else {
        localStorage.removeItem("neural-cutting-user");
    }
    localStorage.setItem("neural-cutting-selected-resume-id", state.selectedResumeId);
    localStorage.setItem("neural-cutting-selected-version-id", state.selectedVersionId);
    localStorage.setItem("neural-cutting-selected-vacancy-id", state.selectedVacancyId);
    localStorage.setItem("neural-cutting-selected-job-id", state.selectedJobId);
}

function renderState() {
    elements.authState.textContent = state.token ? "авторизован" : "не авторизован";
    elements.currentUserId.textContent = state.currentUser?.id ?? "-";
    elements.currentUserEmail.textContent = state.currentUser?.email ?? "-";
    elements.tokenPreview.textContent = state.token ? `${state.token.slice(0, 24)}...` : "-";
    elements.selectedResumeId.textContent = state.selectedResumeId || "-";
    elements.selectedVersionId.textContent = state.selectedVersionId || "-";
    elements.selectedVacancyId.textContent = state.selectedVacancyId || "-";
    elements.selectedJobId.textContent = state.selectedJobId || "-";

    elements.updateResumeId.value = state.selectedResumeId;
    elements.versionResumeId.value = state.selectedResumeId;
    elements.uploadResumeId.value = state.selectedResumeId;
    elements.analysisVersionId.value = state.selectedVersionId;
    elements.analysisVacancyId.value = state.selectedVacancyId;
}

function setAuth(authResponse) {
    state.token = authResponse.accessToken;
    state.currentUser = authResponse.user;
    persistState();
    renderState();
}

function logout() {
    state.token = "";
    state.currentUser = null;
    state.selectedResumeId = "";
    state.selectedVersionId = "";
    state.selectedVacancyId = "";
    state.selectedJobId = "";
    persistState();
    renderState();
    elements.resumesList.innerHTML = "";
    elements.versionsList.innerHTML = "";
    elements.vacanciesList.innerHTML = "";
    renderOutput({ message: "Локальная сессия очищена" });
}

async function bootstrapAuthenticatedView() {
    try {
        const user = await apiFetch("GET", "/api/auth/me");
        state.currentUser = user;
        persistState();
        renderState();
        await Promise.all([loadResumes(false), loadVacancies(false)]);
    } catch (error) {
        renderError(error);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        name: form.name.value.trim(),
        email: form.email.value.trim(),
        password: form.password.value
    };

    try {
        const response = await apiFetch("POST", "/api/auth/register", payload, false);
        setAuth(response);
        renderOutput(response);
        await Promise.all([loadResumes(false), loadVacancies(false)]);
    } catch (error) {
        renderError(error);
    }
}

async function handleLogin(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        email: form.email.value.trim(),
        password: form.password.value
    };

    try {
        const response = await apiFetch("POST", "/api/auth/login", payload, false);
        setAuth(response);
        renderOutput(response);
        await Promise.all([loadResumes(false), loadVacancies(false)]);
    } catch (error) {
        renderError(error);
    }
}

async function handleCreateResume(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        title: form.title.value.trim(),
        language: form.language.value,
        targetRole: form.targetRole.value.trim()
    };

    try {
        const resume = await apiFetch("POST", "/api/resumes", payload);
        selectResume(resume.id);
        renderOutput(resume);
        await loadResumes(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleUpdateResume(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const resumeId = form.id.value.trim();
    if (!resumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }

    const payload = {};
    if (form.title.value.trim()) {
        payload.title = form.title.value.trim();
    }
    if (form.language.value) {
        payload.language = form.language.value;
    }
    if (form.targetRole.value.trim()) {
        payload.targetRole = form.targetRole.value.trim();
    }

    try {
        const resume = await apiFetch("PATCH", `/api/resumes/${resumeId}`, payload);
        renderOutput(resume);
        await loadResumes(false);
        await loadSelectedResumeDetail(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleDeleteResume() {
    if (!state.selectedResumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }

    if (!window.confirm(`Удалить resume ${state.selectedResumeId}?`)) {
        return;
    }

    try {
        const response = await apiFetch("DELETE", `/api/resumes/${state.selectedResumeId}`);
        state.selectedResumeId = "";
        state.selectedVersionId = "";
        state.selectedJobId = "";
        persistState();
        renderState();
        elements.versionsList.innerHTML = "";
        renderOutput({ message: "Resume удалено", response });
        await loadResumes(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleCreateTextVersion(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const resumeId = form.resumeId.value.trim();
    if (!resumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }

    try {
        const version = await apiFetch("POST", `/api/resumes/${resumeId}/versions/text`, {
            text: form.text.value.trim()
        });
        selectResume(resumeId);
        selectVersion(version.id);
        renderOutput(version);
        await loadVersions(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleUploadVersion(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const resumeId = form.resumeId.value.trim();
    const file = form.file.files[0];

    if (!resumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }
    if (!file) {
        renderOutput({ message: "Выбери файл для загрузки" });
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
        const version = await apiFetch("POST", `/api/resumes/${resumeId}/versions/upload`, formData);
        selectResume(resumeId);
        selectVersion(version.id);
        renderOutput(version);
        form.reset();
        elements.uploadResumeId.value = state.selectedResumeId;
        await loadVersions(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleCreateVacancy(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = {
        title: form.title.value.trim(),
        company: emptyToNull(form.company.value),
        url: emptyToNull(form.url.value),
        text: form.text.value.trim()
    };

    try {
        const vacancy = await apiFetch("POST", "/api/vacancies/manual", payload);
        selectVacancy(vacancy.id);
        renderOutput(vacancy);
        await loadVacancies(false);
    } catch (error) {
        renderError(error);
    }
}

async function handleCreateAnalysisJob(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const resumeVersionId = form.resumeVersionId.value.trim();
    const vacancyId = form.vacancyId.value.trim();

    if (!resumeVersionId || !vacancyId) {
        renderOutput({ message: "Нужны и versionId, и vacancyId" });
        return;
    }

    try {
        const job = await apiFetch("POST", "/api/analysis-jobs", {
            resumeVersionId,
            vacancyId
        });
        selectJob(job.id);
        renderOutput(job);
    } catch (error) {
        renderError(error);
    }
}

async function loadResumes(shouldRenderOutput = true) {
    try {
        const page = await apiFetch("GET", "/api/resumes?page=0&size=20");
        renderResumes(page.content || []);
        if (shouldRenderOutput) {
            renderOutput(page);
        }
    } catch (error) {
        renderError(error);
    }
}

async function loadSelectedResumeDetail(shouldRenderOutput = true) {
    if (!state.selectedResumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }

    try {
        const resume = await apiFetch("GET", `/api/resumes/${state.selectedResumeId}`);
        renderVersions(resume.versions || []);
        if (shouldRenderOutput) {
            renderOutput(resume);
        }
    } catch (error) {
        renderError(error);
    }
}

async function loadVersions(shouldRenderOutput = true) {
    if (!state.selectedResumeId) {
        renderOutput({ message: "Сначала выбери резюме" });
        return;
    }

    try {
        const versions = await apiFetch("GET", `/api/resumes/${state.selectedResumeId}/versions`);
        renderVersions(versions);
        if (shouldRenderOutput) {
            renderOutput(versions);
        }
    } catch (error) {
        renderError(error);
    }
}

async function loadSelectedVersionDetail() {
    if (!state.selectedVersionId) {
        renderOutput({ message: "Сначала выбери версию" });
        return;
    }

    await callAndRender("GET", `/api/resume-versions/${state.selectedVersionId}`);
}

async function loadAnalysisHistory() {
    if (!state.selectedVersionId) {
        renderOutput({ message: "Сначала выбери версию" });
        return;
    }

    await callAndRender("GET", `/api/resume-versions/${state.selectedVersionId}/analysis-history?page=0&size=20`);
}

async function loadVacancies(shouldRenderOutput = true) {
    try {
        const page = await apiFetch("GET", "/api/vacancies?page=0&size=20");
        renderVacancies(page.content || []);
        if (shouldRenderOutput) {
            renderOutput(page);
        }
    } catch (error) {
        renderError(error);
    }
}

async function loadSelectedVacancyDetail() {
    if (!state.selectedVacancyId) {
        renderOutput({ message: "Сначала выбери vacancy" });
        return;
    }

    await callAndRender("GET", `/api/vacancies/${state.selectedVacancyId}`);
}

async function loadSelectedJob() {
    if (!state.selectedJobId) {
        renderOutput({ message: "Сначала выбери analysis job" });
        return;
    }

    await callAndRender("GET", `/api/analysis-jobs/${state.selectedJobId}`);
}

async function loadSelectedJobResult() {
    if (!state.selectedJobId) {
        renderOutput({ message: "Сначала выбери analysis job" });
        return;
    }

    await callAndRender("GET", `/api/analysis-jobs/${state.selectedJobId}/result`);
}

async function callAndRender(method, path, body = null, authRequired = true) {
    try {
        const response = await apiFetch(method, path, body, authRequired);
        renderOutput(response);
        return response;
    } catch (error) {
        renderError(error);
        throw error;
    }
}

async function apiFetch(method, path, body = null, authRequired = true) {
    const headers = {
        Accept: "application/json"
    };

    if (authRequired && !state.token) {
        throw new Error("Нужна авторизация. Выполни login или register.");
    }

    if (authRequired && state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }

    const options = { method, headers };
    if (body instanceof FormData) {
        options.body = body;
    } else if (body !== null && body !== undefined) {
        headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    const response = await fetch(path, options);
    const text = await response.text();
    const data = text ? safeParse(text) : null;

    if (!response.ok) {
        const message = data?.message || data?.error || `HTTP ${response.status}`;
        const error = new Error(message);
        error.payload = data;
        error.status = response.status;
        throw error;
    }

    return data;
}

function safeParse(value) {
    try {
        return JSON.parse(value);
    } catch {
        return value;
    }
}

function renderOutput(data) {
    elements.responseOutput.textContent = typeof data === "string"
        ? data
        : JSON.stringify(data, null, 2);
}

function renderError(error) {
    renderOutput({
        message: error.message,
        status: error.status || null,
        payload: error.payload || null
    });
}

function renderResumes(items) {
    if (!items.length) {
        elements.resumesList.innerHTML = `<div class="entity-card"><p>Резюме пока нет.</p></div>`;
        return;
    }

    elements.resumesList.innerHTML = items.map((resume) => `
        <article class="entity-card">
            <h3>${escapeHtml(resume.title)}</h3>
            <p><strong>ID:</strong> <code>${resume.id}</code></p>
            <p><strong>Language:</strong> ${escapeHtml(resume.language)}</p>
            <p><strong>Target role:</strong> ${escapeHtml(resume.targetRole)}</p>
            <div class="entity-actions">
                <button type="button" data-action="select-resume" data-id="${resume.id}">Выбрать</button>
                <button type="button" data-action="detail-resume" data-id="${resume.id}">Детали</button>
                <button type="button" data-action="versions-resume" data-id="${resume.id}">Версии</button>
            </div>
        </article>
    `).join("");
}

function renderVersions(items) {
    if (!items.length) {
        elements.versionsList.innerHTML = `<div class="entity-card"><p>Версий пока нет.</p></div>`;
        return;
    }

    elements.versionsList.innerHTML = items.map((version) => `
        <article class="entity-card">
            <h3>Version ${version.versionNumber}</h3>
            <p><strong>ID:</strong> <code>${version.id}</code></p>
            <p><strong>Source:</strong> ${escapeHtml(version.sourceType)}</p>
            <p><strong>File:</strong> ${escapeHtml(version.originalFileName || "-")}</p>
            <p><strong>Text preview:</strong> ${escapeHtml((version.textContent || "").slice(0, 140) || "-")}</p>
            <div class="entity-actions">
                <button type="button" data-action="select-version" data-id="${version.id}">Выбрать</button>
                <button type="button" data-action="detail-version" data-id="${version.id}">Детали</button>
                <button type="button" data-action="history-version" data-id="${version.id}">History</button>
            </div>
        </article>
    `).join("");
}

function renderVacancies(items) {
    if (!items.length) {
        elements.vacanciesList.innerHTML = `<div class="entity-card"><p>Vacancy пока нет.</p></div>`;
        return;
    }

    elements.vacanciesList.innerHTML = items.map((vacancy) => `
        <article class="entity-card">
            <h3>${escapeHtml(vacancy.title)}</h3>
            <p><strong>ID:</strong> <code>${vacancy.id}</code></p>
            <p><strong>Company:</strong> ${escapeHtml(vacancy.company || "-")}</p>
            <p><strong>URL:</strong> ${escapeHtml(vacancy.url || "-")}</p>
            <div class="entity-actions">
                <button type="button" data-action="select-vacancy" data-id="${vacancy.id}">Выбрать</button>
                <button type="button" data-action="detail-vacancy" data-id="${vacancy.id}">Детали</button>
            </div>
        </article>
    `).join("");
}

document.addEventListener("click", async (event) => {
    const target = event.target.closest("button[data-action]");
    if (!target) {
        return;
    }

    const { action, id } = target.dataset;

    try {
        switch (action) {
            case "select-resume":
                selectResume(id);
                await loadVersions(false);
                renderOutput({ message: `Выбрано resume ${id}` });
                break;
            case "detail-resume":
                selectResume(id);
                await loadSelectedResumeDetail();
                break;
            case "versions-resume":
                selectResume(id);
                await loadVersions();
                break;
            case "select-version":
                selectVersion(id);
                renderOutput({ message: `Выбрана version ${id}` });
                break;
            case "detail-version":
                selectVersion(id);
                await loadSelectedVersionDetail();
                break;
            case "history-version":
                selectVersion(id);
                await loadAnalysisHistory();
                break;
            case "select-vacancy":
                selectVacancy(id);
                renderOutput({ message: `Выбрана vacancy ${id}` });
                break;
            case "detail-vacancy":
                selectVacancy(id);
                await loadSelectedVacancyDetail();
                break;
            default:
                break;
        }
    } catch (error) {
        renderError(error);
    }
});

function selectResume(id) {
    state.selectedResumeId = id;
    persistState();
    renderState();
}

function selectVersion(id) {
    state.selectedVersionId = id;
    persistState();
    renderState();
}

function selectVacancy(id) {
    state.selectedVacancyId = id;
    persistState();
    renderState();
}

function selectJob(id) {
    state.selectedJobId = id;
    persistState();
    renderState();
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

function emptyToNull(value) {
    const trimmed = value.trim();
    return trimmed ? trimmed : null;
}
