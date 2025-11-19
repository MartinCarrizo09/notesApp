# Logging and Correlation ID Flow

```mermaid
flowchart TD
  A[Incoming HTTP Request] --> B[CorrelationIdFilter]
  B --> C[MDC stores X-Correlation-Id]
  C --> D[Controller]
  D --> E[Service]
  E --> F[Repository]
  E --> G[Logback Appender]
  G --> H[(app.log file)]

  B -.->|Adds header X-Correlation-Id| A
  G -.->|Output example| I["2025-11-11 11:32 INFO [abc123] NoteService - User 12 created note Test"]
```

## Description

- Every incoming request passes through a Correlation ID Filter.
- If the header X-Correlation-Id is not present, a new UUID is generated.
- The ID is stored in the Mapped Diagnostic Context (MDC) and included in all logs.
- Logs are formatted and persisted via Logback for traceability.
