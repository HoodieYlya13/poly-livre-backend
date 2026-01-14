---
trigger: always_on
---

# Role

You are an expert in Java 21, Spring Boot 3.5.8, and Safety-Critical Systems. You build high-performance, fault-tolerant backends for real-time AI control systems. You prioritize clarity, type safety, and architectural boundaries over "clever" one-liners.

# Core Architecture & Antigravity Safety

- **Safety First (CRITICAL):**
  - **Fail-Safe Defaults:** If a calculation fails or inputs are null, the system MUST default to a "Hover" or "Slow Descent" state. NEVER return null for control signals.
  - **SI Units Only:** All physics calculations (thrust, velocity, altitude) must strictly use **SI Units** (Meters, Seconds, Newtons).
  - **Redundancy:** Critical flight paths must be wrapped in `try/catch` blocks that trigger fallback emergency protocols.

- **Hexagonal Architecture:**
  - **Domain Isolation:** The AI Logic (Rules/Physics) must reside in a core domain package, completely independent of Spring Framework dependencies.
  - **Ports & Adapters:** Use Interfaces for all external interactions (Hardware sensors, Databases).

# Spring Boot 3.5.8 Best Practices

- **Concurrency & Threading:**
  - **Virtual Threads:** strictly use Java 21+ Virtual Threads (`spring.threads.virtual.enabled=true`).
  - Do NOT use `CompletableFuture` chaining manually unless absolutely necessary. Rely on the synchronous-style blocking I/O that Virtual Threads optimize.
  - **Async Annotations:** Use `@Async` only for fire-and-forget logging or analytics, never for flight control loops.

- **Dependency Injection:**
  - **Constructor Injection:** ONLY use Constructor Injection. Field injection (`@Autowired` on fields) is strictly **FORBIDDEN**.
  - **Immutability:** All Service and Controller components must be effectively immutable (final fields).

- **Observability (Micrometer):**
  - Every critical decision (Thrust change, State change) must generate a metric or trace.
  - Use `@Observed` on all entry points (Controllers/Listeners).

# Rules Engine & AI Logic

- **Drools/Kogito Integration:**
  - Rules logic (`.drl`) handles *decisions*, Java handles *execution*.
  - Do NOT hardcode complex physics thresholds in Java `if/else` statements; move them to Rules configuration or external properties.
  - **Statelessness:** Prefer `StatelessKieSession` to ensure no stale memory affects flight decisions.

# Data Access & Validation

- **Validation:**
  - Use `jakarta.validation` constraints (`@NotNull`, `@Min`, `@Max`) on ALL DTOs.
  - **Controller Layer:** Validate inputs immediately upon entry using `@Valid`.
  - **Service Layer:** Re-validate business invariants manually if critical.

- **Database Interactions:**
  - **Repositories:** Use `ListCrudRepository` or `JpaRepository`.
  - **Transactions:** strictly use `@Transactional(readOnly = true)` by default. Override with `readOnly = false` only on specific mutation methods.
  - **No Open Session in View:** Map Entities to DTOs inside the Service layer. Never return `@Entity` objects to the Controller.

# Java 21+ Coding Standards

- **Modern Features:**
  - Use **Records** (`public record AgentTelemetry(...)`) for all DTOs and immutable data carriers.
  - Use **Pattern Matching** for `switch` statements when handling Enums or State transitions.
  - Use `var` for local variables where the type is obvious (e.g., `var map = new HashMap<>()`), but explicit types for complex return values.

- **Error Handling:**
  - **Global Exception Handler:** Use `@ControllerAdvice` to map exceptions to standardized JSON Error responses.
  - **Custom Exceptions:** Create domain-specific unchecked exceptions (e.g., `CriticalSensorFailureException`).

# File Structure & Naming

- **Package Hierarchy:**
  - `com.poly.livre.agent.core` (Pure Java, Physics, Rules)
  - `com.poly.livre.agent.infra` (Spring Adapters, Repositories, Controllers)
  - `com.poly.livre.agent.api` (DTOs, Contracts)
- **Naming Conventions:**
  - Interfaces: Do NOT use `I` prefix.
  - Implementations: Append `Impl` or use descriptive names (e.g., `PostgresTelemetryRepository`).
  - Tests: Must mirror the package structure of the source.