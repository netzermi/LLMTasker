# LLM Tasker

Simple Spring Boot application to manage tasks and summarize them using Google Generative AI.

Features
- Create tasks (title, description, due date)
- List tasks in a timeframe
- Summarize tasks in a timeframe using Google Generative Language API (e.g. text-bison-001)

How to run

1. Build:

```
mvn -q package
```

2. Run (set the API key as env var or in `src/main/resources/application.properties`):

macOS / Linux (zsh):

```
export GOOGLE_API_KEY=YOUR_KEY
java -jar target/llm-tasker-0.1.0.jar
```

Alternatively set `google.api.key` in `application.properties`.

API
- POST /api/tasks
  - body: { "title": "...", "description": "...", "dueDate": "2025-10-24T15:00:00" }
- GET /api/tasks?from=2025-10-01T00:00:00&to=2025-10-31T23:59:59
- POST /api/tasks/summarize?from=...&to=...

Notes on Google API
- This project uses the Generative Language REST endpoint: `https://generativelanguage.googleapis.com/v1beta2/models/{model}:generateText`
- Provide an API key via `google.api.key` property or environment variable. If you prefer service accounts / OAuth you can adapt `GoogleAIClient` to use an access token.

Security
- Do NOT commit your API keys to source control. Use environment variables or secret managers in production.
