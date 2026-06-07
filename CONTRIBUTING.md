# Contributing to ETXCenter / SAVETX

Thank you for your interest in contributing to SAVETX! This guide is designed to help you set up
your development environment, understand our hybrid Kotlin Multiplatform (KMP) architecture, and
ensure your changes integrate seamlessly.

---

## Table of Contents

- [Project Goals](#project-goals)
- [Technology Stack](#technology-stack)
- [Architecture and Package Structure](#architecture-and-package-structure)
- [Dependency Rules and Conventions](#dependency-rules-and-conventions)
- [Project Setup and Running](#project-setup-and-running)
- [Branching Strategy (Git Flow)](#branching-strategy-git-flow)
- [Commit and PR Convention](#commit-and-pr-convention)
- [Pull Request Guidelines](#pull-request-guidelines)

---

## Project Goals

SAVETX is a mobile and desktop application developed with Kotlin Multiplatform designed to
visualize real-time competitive data collected by the SAVETX ecosystem.

The application's primary goals are:

- Real-time match visualization (Live Match State).
- Detailed history of completed matches.
- Global and individual Team and Player statistics.
- Native multiplatform support sharing the UI (Android as the main TFG target, ready
  for Desktop and iOS).

---

## Technology Stack

| Layer / Component             | Technology                                             |
|-------------------------------|--------------------------------------------------------|
| **Core Multiplatform**        | Kotlin 2.x                                             |
| **User Interface (UI)**       | Compose Multiplatform (Material 3)                     |
| **Dependency Injection**      | Koin Core + Koin Compose                               |
| **Database / Auth**           | GitLive Firebase Kotlin SDK (Auth & Realtime Database) |
| **Navigation**                | Jetpack Navigation Compose (KMP Type-Safe)             |
| **Serialization**             | `kotlinx.serialization` (JSON)                         |
| **Concurrency**               | Kotlin Coroutines + Asynchronous Flows                 |

---

## Architecture and Package Structure

To ensure maintainability and scalability of the TFG, the project uses a **Hybrid** approach: a
clean layer separation for the data infrastructure, combined with a *Feature-Driven* design for the
user interface.

All core development takes place in the shared module (
`shared/src/commonMain/kotlin/com/kingsaul22/etxcenter/`).

```text
com.kingsaul22.etxcenter
├── core/                  # Cross-cutting shared infrastructure
│   ├── di/                # Koin modules
│   ├── navigation/        # NavHost definition and type-safe routes
│   └── theme/             # Material 3 design system (Colors, Typography)
│
├── data/                  # Data layer (External world access)
│   ├── datasource/        # Direct low-level connections
│   ├── dto/               # Firebase data transfer objects (@Serializable)
│   ├── mapper/            # Extensions to map from DTO to Domain Model
│   └── repository/        # Domain interface implementations
│
├── domain/                # Pure business rules (pure Kotlin)
│   ├── model/             # Clean data entities (Player, Team, Match)
│   └── repository/        # Repository contracts/interfaces
│
└── feature/               # UI and user flows grouped by functionality
    ├── auth/              # Authentication screens, ViewModels, and state
    ├── home/              # Main app dashboard
    ├── live/              # Interactive live state visualization
    └── stats/             # Historical and statistical listings

```

---

## Dependency Rules and Conventions

To avoid coupling and ensure the application can compile on multiple platforms without side effects,
the following rules must be strictly followed:

### 1. Dependency flow direction

The application layers can only look in one direction:

```text
feature (UI/ViewModel)  ──>  data (Repositories/DTOs)  ──>  domain (Pure Models)
   │                           │
   └───────────> [core] <──────┘

```

* **Domain is sacred:** `domain/` cannot import anything from `data/`, `feature/`, or libraries
  like Firebase or Compose. It is pure Kotlin.
* **UI does not talk to Firebase:** Using direct Firebase references (e.g.
  `Firebase.database.reference(...)`) inside composables or ViewModels in `feature/` is strictly
  prohibited. All data access must be encapsulated behind a domain repository interface.

### 2. Strict separation between DTOs and Domain Models

* Objects within `data/dto/` faithfully reflect the JSON structure of the Firebase Realtime
  Database and must be annotated with `@Serializable`.
* Objects within `domain/model/` are what the UI consumes. They carry no persistence annotations
  and use clean data types (such as `Instant` or `LocalDateTime` from `kotlinx-datetime` instead of
  plain `Long` timestamps).

### 3. Feature Structure

Each package within `feature/` must follow a clean, predictable MVVM structure exposed in 3 files:

* `*Screen.kt`: Contains exclusively the `@Composable` UI components.
* `*ViewModel.kt`: Manages screen events and processes data flows.
* `*UiState.kt`: Immutable data class representing the exact UI state at any given moment.

---

## Project Setup and Running

### Prerequisites

* Android Studio Jellyfish (or higher) with the *Kotlin Multiplatform* plugin installed.
* JDK 17 or higher configured in the Gradle runtime environment.

### Common Development Commands

Use the following commands from the project root to build and run specific environments:

```bash
# Run the application on a connected Android Emulator/Device
./gradlew :androidApp:installDebug

# Build the Android debug binary
./gradlew :androidApp:assembleDebug

# Run the application in Desktop Environment (Desktop/JVM)
./gradlew :desktopApp:run

# Run the full set of automated tests in the shared module
./gradlew :shared:allTests

```

*To run the iOS module (`iosApp/`), open the `/iosApp` directory in Xcode and launch it using a
compatible simulator.*

---

## Branching Strategy (Git Flow)

This project strictly follows the **GitHub Flow** model:

* `main` is the protected, production branch. Direct pushes to `main` are prohibited.
* Every new feature, fix, or change must be made in a dedicated branch created from the latest
  state of `main`.
* Code integration is done solely and exclusively through reviewed and approved **Pull Requests
  (PR)**.

### Branch Naming Convention

Branches must be named using the lowercase format `type/context-short-description`:

| Prefix  | Purpose                                               | Example                            |
|---------|-------------------------------------------------------|------------------------------------|
| `feat/` | Development of new app features                       | `feat/live-state-stream`           |
| `fix/`  | Bug and error fixes                                   | `fix/navigation-backstack-crash`   |
| `core/` | Global architecture, DI, or navigation changes        | `core/koin-viewmodel-refactor`     |
| `chore/`| Maintenance tasks or dependency updates               | `chore/bump-compose-multiplatform` |

---

## Commit and PR Convention

Pull Request titles and main commit messages must strictly follow the **Conventional Commits**
specification:

```text
type(optional-scope): short description in imperative mood

```

The description must begin with an infinitive or imperative verb ("add", "fix", "update", avoid
past tense).

**Valid examples for this project:**

* `feat(auth): implement anonymous login with GitLive`
* `fix(live): fix live match scoreboard desync`
* `core(di): register FirebasePlayerRepository in Koin module`
* `chore(deps): update kotlinx-serialization library to v1.9`

---

## Pull Request Guidelines

When opening a Pull Request, ensure you meet the following quality checks:

1. **Single responsibility:** The PR must solve a single problem or implement a single feature.
   Do not mix global refactors with a visual bug fix.
2. **Green tests:** Ensure `./gradlew :shared:allTests` compiles and passes locally before pushing
   your changes to the remote repository.
3. **No secrets committed:** Never include credentials, private keys, or private Firebase
   configuration `.json` files in commits. Use the native environment property injection mechanisms.
