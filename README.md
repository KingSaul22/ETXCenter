# SAVETX / ETXCenter

Kotlin Multiplatform application for real-time competitive Rocket League data visualization — live matches, match history, team and player statistics, with a shared UI across **Android**, **Desktop (JVM)**, and **iOS**.

---

## Features

- **Live Match State** — real-time scoreboard, events, and match timeline
- **Match History** — detailed match results with per-player breakdowns
- **Team Profiles** — roster, stats, and match history per team
- **Player Profiles** — individual performance stats across matches
- **Admin Dashboard** — manage teams and content within the app
- **Dark Theme** — Material 3 dark theme out of the box

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.4.0 |
| UI | Compose Multiplatform 1.11.1 + Material 3 |
| DI | Koin 4.2.1 |
| Backend | GitLive Firebase KMP SDK (Auth + Realtime Database) |
| Navigation | Jetpack Navigation Compose (type-safe routes) |
| Serialization | kotlinx-serialization 1.11.0 |
| DateTime | kotlinx-datetime 0.8.0 |
| Concurrency | kotlinx-coroutines 1.11.0 + Flow |

---

## Architecture

```
feature/ (UI + ViewModel)  ──>  data/ (repositories, DTOs)  ──>  domain/ (pure models, interfaces)
       │                                │
       └──────────> core/ (DI, nav, theme) <──────────┘
```

- **`domain/`** — pure Kotlin, zero framework imports. Repository interfaces and domain models.
- **`data/`** — GitLive Firebase implementations, `@Serializable` DTOs, mappers.
- **`feature/`** — MVVM: `*Screen.kt`, `*ViewModel.kt`, `*UiState.kt` per feature.
- **`core/`** — Koin DI, navigation routes, Material 3 theme, AdaptiveScaffold.

---

## Prerequisites

- **JDK 17** or higher
- **Android Studio** (Jellyfish+) with Kotlin Multiplatform plugin
- **Xcode** 15+ (iOS target only)
- **Android SDK** with API 37 (installed via Android Studio)

---

## Build & Run

```bash
# Android
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# Desktop (JVM)
./gradlew :desktopApp:run
./gradlew :desktopApp:hotRun --auto        # hot-reload

# Desktop release artifacts
./gradlew :desktopApp:createDistributable   # DMG / MSI / DEB
```

**iOS:** open `iosApp/iosApp.xcodeproj` in Xcode, select the `iosApp` scheme, and run on a simulator.

---

## Testing

```bash
./gradlew :shared:allTests                  # all platforms
./gradlew :shared:jvmTest                   # JVM only
./gradlew :shared:testAndroidHostTest       # Android host tests
./gradlew :shared:iosSimulatorArm64Test     # iOS simulator tests
```

Test stack: `kotlin.test` + MockK + Turbine + `kotlinx-coroutines-test`.

---

## CI/CD

| Workflow | Trigger | What |
|---|---|---|
| `ci-test.yml` | push / PR to `main` | JVM, Android, and iOS tests in parallel |
| `build-check.yml` | PR to `main` | Android `assembleDebug` + Desktop distributables on all 3 OSes |
| `release.yml` | tag `v*` or manual | Android APK/AAB + Desktop DMG/MSI/DEB → GitHub Release |

CI requires an environment secret: `ANDROID_GOOGLE_SERVICES_JSON` (set under the `development` environment).

---

## Project Structure

```
ETXCenter/
├── androidApp/                # Android application shell
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/.../        # MainActivity, MainApplication
├── desktopApp/                # Desktop (JVM) application shell
│   └── src/main/kotlin/.../   # main.kt (entry point)
├── iosApp/                    # iOS Xcode project (not a Gradle module)
│   └── iosApp/                # iOSApp.swift, ContentView.swift
├── shared/                    # Shared KMP module (all business logic + UI)
│   └── src/
│       ├── commonMain/kotlin/.../
│       │   ├── App.kt         # Root composable
│       │   ├── core/          # DI, navigation, theme, layout
│       │   ├── data/          # DTOs, mappers, repository impls
│       │   ├── domain/        # Pure models + repository interfaces
│       │   └── feature/       # auth, home, live, stats, teams, players
│       ├── commonTest/        # Shared unit tests
│       ├── androidMain/       # Android expect/actual
│       ├── jvmMain/           # Desktop expect/actual
│       └── iosMain/           # iOS expect/actual
├── gradle/
│   ├── libs.versions.toml     # Version catalog (all deps and plugins)
│   └── wrapper/
├── .github/workflows/         # CI / Release / Build Check
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for architecture rules, branch naming, commit conventions, and PR guidelines.

- Branch naming: `type/context-short-description` (`feat/`, `fix/`, `core/`, `chore/`)
- Commits: [Conventional Commits](https://www.conventionalcommits.org/)
- Run `./gradlew :shared:allTests` before pushing

---

## License

This project is provided for academic purposes (TFG — Trabajo de Fin de Grado). No open-source license is currently assigned.
