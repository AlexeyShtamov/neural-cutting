
# 📄 Neural Cutting — Backend Service

## 📌 Описание

**Neural Cutting** — это backend-сервис для анализа резюме и вакансий с использованием внешнего AI/аналитического сервиса.
Система позволяет:

* управлять резюме и их версиями
* создавать вакансии
* запускать анализ соответствия резюме вакансии
* получать проблемы и рекомендации по улучшению резюме
* хранить историю анализов

---

# 🏗 Архитектура

Проект построен по классической многослойной архитектуре:

```
Controller (REST API)
        ↓
Service (бизнес-логика)
        ↓
Repository (доступ к данным)
        ↓
Database (JPA/Hibernate)
```

Дополнительно:

* `integration` — работа с внешним AI сервисом
* `mapper` — преобразование Entity ↔ DTO
* `security` — JWT аутентификация
* `storage` — файловое хранилище

---

## 📦 Основные модули

### 1. Controller Layer (`controller/`)

Отвечает за REST API:

* `AuthController`
* `ResumeController`
* `ResumeVersionController`
* `VacancyController`
* `AnalysisJobController`

👉 Только:

* принимает HTTP-запрос
* валидирует входные данные
* вызывает сервис
* возвращает DTO

---

### 2. Service Layer (`service/`)

💡 **Ключевой слой системы (вся бизнес-логика здесь)**

* `AuthService`
* `ResumeService`
* `ResumeVersionService`
* `VacancyService`
* `AnalysisJobService`
* `AnalysisJobStateService`

👉 Именно здесь реализованы алгоритмы.

---

### 3. Repository Layer (`repository/`)

Spring Data JPA:

* `ResumeRepository`
* `VacancyRepository`
* `AnalysisJobRepository`
* `ProblemRepository`
* `RecommendationRepository`
* и др.

👉 Только CRUD + простые запросы

---

### 4. Domain Layer (`domain/`)

Сущности:

* `Resume`
* `ResumeVersion`
* `Vacancy`
* `AnalysisJob`
* `AnalysisResult`
* `Problem`
* `Recommendation`
* `Person`

---

### 5. Integration Layer (`integration/analysis`)

Работа с внешним сервисом анализа:

* `AnalysisClient` (интерфейс)
* `HttpAnalysisClient` (реальная интеграция)
* `StubAnalysisClient` (заглушка)

---

### 6. Security (`security/`)

* JWT авторизация
* фильтры Spring Security
* `JwtAuthenticationFilter`
* `CustomUserDetailsService`

---

# 🌐 REST API

## 🔐 Auth API

### POST `/auth/register`

Регистрация пользователя

```json
{
  "email": "user@mail.com",
  "password": "123456"
}
```

### POST `/auth/login`

Авторизация

```json
{
  "email": "user@mail.com",
  "password": "123456"
}
```

📥 Response:

```json
{
  "token": "jwt-token"
}
```

---

## 📄 Resume API

### POST `/resumes`

Создать резюме

### GET `/resumes`

Получить список

### GET `/resumes/{id}`

Детали резюме

---

## 📄 Resume Versions

### POST `/resume-versions`

Создать версию (текст/файл)

### GET `/resume-versions/{id}`

---

## 💼 Vacancy API

### POST `/vacancies`

Создать вакансию вручную

```json
{
  "title": "...",
  "description": "..."
}
```

### GET `/vacancies`

---

## 🤖 Analysis API

### POST `/analysis-jobs`

Создать задачу анализа:

```json
{
  "resumeVersionId": 1,
  "vacancyId": 2
}
```

---

### GET `/analysis-jobs/{id}`

Получить статус и результат:

```json
{
  "status": "COMPLETED",
  "result": {
    "problems": [...],
    "recommendations": [...]
  }
}
```

---

### GET `/analysis-jobs/history`

История анализов

---

# 🧠 Сервисный слой — алгоритмы

## 🔹 1. AnalysisJobService — основной алгоритм

### Шаги выполнения анализа:

```text
1. Проверка входных данных
2. Загрузка ResumeVersion и Vacancy
3. Создание AnalysisJob (status = CREATED)
4. Отправка данных во внешний сервис (AnalysisClient)
5. Получение результата
6. Сохранение:
   - AnalysisResult
   - Problem[]
   - Recommendation[]
7. Обновление статуса (COMPLETED / FAILED)
```

---

## 🔹 2. Алгоритм анализа (логика)

```text
Input:
- текст резюме
- описание вакансии

Process:
- отправка в AI сервис
- получение:
   - проблем (skills gap, formatting, etc.)
   - рекомендаций

Output:
- структурированный результат
```

---

## 🔹 3. AnalysisJobStateService

Отвечает за **жизненный цикл задачи**

Состояния:

```text
CREATED → IN_PROGRESS → COMPLETED
                       ↘ FAILED
```

---

## 🔹 4. ResumeService

Функции:

* CRUD резюме
* проверка прав доступа
* связь с пользователем

---

## 🔹 5. ResumeVersionService

Алгоритм:

```text
1. Получение файла или текста
2. Сохранение в storage
3. Создание новой версии
4. Привязка к Resume
```

---

## 🔹 6. VacancyService

* создание вакансий
* хранение требований

---

## 🔹 7. AuthService

Алгоритм:

```text
Register:
- хэширование пароля
- сохранение пользователя

Login:
- проверка пароля
- генерация JWT
```

---

# 🔌 Интеграция с AI сервисом

## AnalysisClient

Интерфейс:

```java
ExternalAnalysisResponse analyze(ExternalAnalysisRequest request);
```

---

## HttpAnalysisClient

Алгоритм:

```text
1. Формирование HTTP запроса
2. Отправка во внешний сервис
3. Получение JSON ответа
4. Маппинг в доменную модель
```

---

## StubAnalysisClient

Используется для:

* локальной разработки
* тестирования

---

# 🗄 Хранение данных

## Основные связи:

```
Person
  └── Resume
        └── ResumeVersion

Vacancy

AnalysisJob
  ├── ResumeVersion
  ├── Vacancy
  └── AnalysisResult
         ├── Problem[]
         └── Recommendation[]
```

---

# 📁 Файловое хранилище

## FileStorageService

Реализация:

* `LocalFileStorageService`

Алгоритм:

```text
1. Приём файла
2. Генерация уникального имени
3. Сохранение на диск
4. Возврат descriptor
```

---

# 🔐 Безопасность

* JWT-based authentication
* Stateless
* Spring Security filter chain

Flow:

```text
Request → JwtFilter → SecurityContext → Controller
```

---

# 🚀 Запуск

## Docker

```bash
docker-compose up --build
```

---

## Локально

```bash
./mvnw spring-boot:run
```

---

# 📌 Итог

## Архитектурные плюсы

✔ Чистое разделение слоёв
✔ Расширяемость (через AnalysisClient)
✔ Изоляция бизнес-логики в сервисах
✔ DTO + Mapper слой
✔ Готовность к микросервисам

---

# 🔌 HH.ru Integration

## Архитектура

Интеграция с HH.ru реализована через паттерн **Adapter**:

```
VacancyProvider (interface)
        ↓
HhRuVacancyProvider (adapter)
        ↓
HH.ru API (https://api.hh.ru)
```

### Компоненты

| Компонент | Описание |
|-----------|----------|
| `VacancyProvider` | Интерфейс адаптера для внешних источников вакансий |
| `HhRuVacancyProvider` | Реализация адаптера для HH.ru API |
| `HhRuVacancy` | DTO ответа вакансии от HH.ru |
| `HhRuSearchResponse` | DTO ответа поиска HH.ru |
| `HhRuProperties` | Конфигурация подключения |

---

## REST API

### POST `/api/vacancies/import/hh-ru`

Импорт вакансии из HH.ru по ID или URL.

**Request:**
```json
{
  "vacancyIdOrUrl": "https://hh.ru/vacancy/12345678"
}
```

или просто ID:
```json
{
  "vacancyIdOrUrl": "12345678"
}
```

**Response:**
```json
{
  "id": "uuid",
  "title": "Java Developer",
  "company": "Company Name",
  "url": "https://hh.ru/vacancy/12345678",
  "text": "Описание вакансии...",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

---

### POST `/api/vacancies/search/hh-ru`

Поиск вакансий на HH.ru без импорта.

**Request:**
```json
{
  "query": "Java developer",
  "areaId": "1",
  "limit": 20
}
```

**Response:**
```json
[
  {
    "id": "12345678",
    "name": "Java Developer",
    "employerName": "Company",
    "employerLogoUrl": "https://...",
    "salary": {
      "from": 150000,
      "to": 250000,
      "currency": "RUR",
      "gross": false
    },
    "areaName": "Москва",
    "alternateUrl": "https://hh.ru/vacancy/12345678",
    "publishedAt": "2024-01-15T10:30:00Z"
  }
]
```

---

## Кэширование

Используется **Caffeine** с TTL 1 час:

- `hh-ru-vacancies` — кэш отдельных вакансий
- `hh-ru-search` — кэш результатов поиска

---

## Конфигурация

```yaml
app:
  hh-ru:
    base-url: ${HH_RU_BASE_URL:https://api.hh.ru}
    user-agent: ${HH_RU_USER_AGENT:NeuralCutting/1.0}
    connect-timeout: ${HH_RU_CONNECT_TIMEOUT:PT5S}
    read-timeout: ${HH_RU_READ_TIMEOUT:PT10S}
    enabled: ${HH_RU_ENABLED:true}
```

---

# 🧠 Skill Extraction Algorithm

## Архитектура

Извлечение навыков работает в три этапа:

```
Input: text (резюме или вакансия)
        ↓
Step 1: Dictionary Matching
        ↓
Step 2: Context-aware Extraction
        ↓
Step 3: Normalization
        ↓
Output: Set<ExtractedSkill>
```

---

## Step 1: Dictionary Matching

Проверка по предопределённым словарям:

### Категории навыков

| Категория | Примеры |
|-----------|---------|
| `PROGRAMMING_LANGUAGE` | Java, Python, JavaScript, Kotlin, Go, Rust, C++, C#, Scala |
| `FRAMEWORK` | Spring Boot, Django, React, Vue.js, Angular, Node.js, FastAPI |
| `DATABASE` | PostgreSQL, MySQL, MongoDB, Redis, Elasticsearch, Oracle |
| `TOOL` | Docker, Kubernetes, Git, Jenkins, Maven, Gradle, AWS, GCP, Azure |
| `SOFT_SKILL` | Teamwork, Communication, Leadership, Problem Solving, Agile, Scrum |

---

## Step 2: Context-aware Extraction

Regex patterns для контекста:

### Русские паттерны
- `опыт работы с {skill}`
- `знание {skill}`
- `навыки: {skill}`
- `стек: {skill}`
- `технологии: {skill}`

### Английские паттерны
- `experience with {skill}`
- `knowledge of {skill}`
- `skills: {skill}`
- `stack: {skill}`
- `proficient in {skill}`

---

## Step 3: Normalization

Автоматическое приведение к каноническому виду:

| Input | Normalized |
|-------|------------|
| java8, java 8, JDK 8 | Java 8 |
| SpringBoot, spring-boot | Spring Boot |
| postgres, PostgreSQL | PostgreSQL |
| k8s | Kubernetes |
| js | JavaScript |
| TS | TypeScript |
| Golang | Go |
| Postgres | PostgreSQL |

---

## Skill Gap Analysis

Сравнение навыков резюме с требованиями вакансии:

```
Input: resumeText, vacancyText
        ↓
1. Extract skills from resume → Set<Skill>
2. Extract skills from vacancy → Set<Skill>
3. gapSkills = vacancySkills - resumeSkills
4. matchedSkills = resumeSkills ∩ vacancySkills
5. matchPercentage = (matchedSkills / vacancySkills) * 100
        ↓
Output: SkillGapResult
```

### SkillGapResult

```java
record SkillGapResult(
    int matchPercent,           // 0-100
    Set<ExtractedSkill> matchedSkills,
    Set<ExtractedSkill> missingSkills,
    Set<ExtractedSkill> resumeSkills,
    Set<ExtractedSkill> vacancySkills
)
```

---

## Интеграция с анализом

Результаты извлечения навыков включаются в `AnalysisResult`:

```json
{
  "score": 78,
  "gradeLabel": "GOOD",
  "summary": "...",
  "overallFitPercent": 82,
  "skillMatchPercent": 65,
  "matchedSkills": ["Java", "Spring Boot", "PostgreSQL"],
  "missingSkills": ["Kubernetes", "AWS"],
  "problems": [...],
  "recommendations": [...]
}
```

---

# ⚙️ Configuration

## Переменные окружения

### База данных
```bash
DB_URL=jdbc:postgresql://localhost:5432/neural_cutting
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### JWT
```bash
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=PT12H
```

### Storage
```bash
UPLOAD_PATH=./uploads
MULTIPART_MAX_FILE_SIZE=10MB
MULTIPART_MAX_REQUEST_SIZE=10MB
```

### Analysis Client
```bash
ANALYSIS_CLIENT_MODE=stub        # stub или http
ANALYSIS_BASE_URL=http://analysis-service:8000
ANALYSIS_ENDPOINT=/api/v1/analyze
ANALYSIS_CONNECT_TIMEOUT=PT3S
ANALYSIS_READ_TIMEOUT=PT10S
```

### HH.ru Integration
```bash
HH_RU_BASE_URL=https://api.hh.ru
HH_RU_USER_AGENT=NeuralCutting/1.0
HH_RU_CONNECT_TIMEOUT=PT5S
HH_RU_READ_TIMEOUT=PT10S
HH_RU_ENABLED=true
```

---

## Профили

### local
```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

### docker
```bash
docker-compose up --build
```

---

# 🧪 Testing

## Unit Tests

```bash
./mvnw test -Dtest=SkillExtractorServiceTest
./mvnw test -Dtest=HhRuVacancyProviderTest
```

## Integration Tests

```bash
./mvnw test -Dtest=VacancyControllerIntegrationTest
```

## Manual Testing

```bash
# Запуск приложения
./mvnw spring-boot:run

# Импорт вакансии
curl -X POST http://localhost:8080/api/vacancies/import/hh-ru \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"vacancyIdOrUrl": "https://hh.ru/vacancy/12345678"}'

# Поиск вакансий
curl -X POST http://localhost:8080/api/vacancies/search/hh-ru \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query": "Java developer", "limit": 10}'
```
