# AGENTS.md — ETXCenter / SAVETX

## Build & run

```bash
./gradlew :androidApp:assembleDebug     # Build Android APK
./gradlew :desktopApp:run               # Run Desktop (JVM) app
./gradlew :desktopApp:hotRun --auto     # Desktop hot-reload
```

iOS: open `iosApp/` in Xcode, use the `iosApp` scheme with a simulator.

## Test commands

```bash
./gradlew :shared:allTests              # All platforms (common + jvm + android + ios)
./gradlew :shared:jvmTest               # Desktop/JVM tests only
./gradlew :shared:testAndroidHostTest   # Android host tests only
./gradlew :shared:iosSimulatorArm64Test # iOS simulator tests only
```

Always run `./gradlew :shared:allTests` before pushing a PR.

## Architecture (non-obvious rules)

- **All feature code lives in `shared/src/commonMain/`**, not in Android/JVM/iOS sources.
- **`domain/` is pure Kotlin** — no framework imports (Firebase, Compose, Koin). It defines
  repository interfaces and domain models.
- **`data/` implements `domain/` interfaces** using GitLive Firebase KMP SDK. DTOs in `data/dto/`
  carry `@Serializable` and mirror Firebase JSON; domain models in `domain/model/` use clean types (
  `Instant`, `LocalDateTime` from `kotlinx-datetime`).
- **`feature/` is MVVM**: each feature subpackage has exactly three files: `*Screen.kt` (UI),
  `*ViewModel.kt` (logic), `*UiState.kt` (immutable state sealed class).
- **No direct Firebase access from composables or ViewModels.** All data goes through
  `domain/repository/` interfaces.
- **DI**: Koin (`core/di/AppModule.kt:24`). `initKoin()` is called from `MainApplication.kt` (
  Android), `main.kt` (Desktop), and iOS entry point.
- **Navigation**: Jetpack Navigation Compose type-safe routes defined in
  `core/navigation/Destinations.kt`.
- Single Scaffold Rule: Never nest Scaffold components. Features must either use a single
  screen-level Scaffold to manage their TopAppBar, or rely entirely on the root AdaptiveScaffold.
  Never re-apply window insets manually if the parent Scaffold already provides innerPadding.
- No Java standard libraries in commonMain: Never use java.time.*, java.util.Date, or
  String.format(). Use kotlinx-datetime for all time operations, and use Kotlin string templates
  with .padStart() for formatting.
- Limit RTDB Collections: Never call .valueEvents on a root collection node (like /live_events_feed)
  without a query boundary. Always append .limitToLast(N) before .valueEvents when fetching
  historical or feed lists to prevent massive bandwidth spikes.

## Source-set layout

| Source set         | Purpose                                             |
|--------------------|-----------------------------------------------------|
| `commonMain/`      | Shared business logic + UI (all features live here) |
| `commonTest/`      | Shared unit tests                                   |
| `androidMain/`     | Android-specific `expect`/`actual` implementations  |
| `androidHostTest/` | Android host tests                                  |
| `jvmMain/`         | Desktop-specific `expect`/`actual` implementations  |
| `jvmTest/`         | Desktop/JVM tests                                   |
| `iosMain/`         | iOS-specific `expect`/`actual` implementations      |
| `iosTest/`         | iOS tests                                           |

## Module boundaries

- `:shared` — KMP shared module (all core code)
- `:androidApp` — Android app shell (`MainActivity`, `MainApplication`)
- `:desktopApp` — Desktop app shell (`main.kt`)
- `iosApp/` — iOS Xcode project (not a Gradle module)

## Testing patterns

- Framework: `kotlin.test` + `kotlinx-coroutines-test` + MockK + Turbine
- Dispatchers: Must use `Dispatchers.setMain(testDispatcher)` / `Dispatchers.resetMain()` (see
  `AuthViewModelTest.kt:22-38` for the canonical pattern)
- Run with `runTest { ... }` using `StandardTestDispatcher`
- Use Turbine's `.test { }` extension on `StateFlow` for asserting emissions

## Key toolchain versions

- Kotlin 2.4.0 (with Compose Compiler plugin)
- Compose Multiplatform 1.11.1
- Gradle 9.5.1 (wrapper)
- AGP 9.2.1 (`compileSdk=37`, `minSdk=26`, `targetSdk=37`)
- JVM target: 11 (Android), 17 (shared/Desktop)

## Dependency catalog

All versions in `gradle/libs.versions.toml`. Dependencies reference the catalog — do not hardcode
version strings in build files.

## Branch & commit conventions

- Branch naming: `type/context-short-description` (`feat/`, `fix/`, `core/`, `chore/`)
- Commits: Conventional Commits (`type(scope): description`)
- PRs go against `main`, require `:shared:allTests` passing locally.

## Things to never do

- Never commit `google-services.json` or any Firebase credentials.
- Never import Firebase, Compose, or Koin into `domain/`.
- Never call `Firebase.database.reference(...)` or `Firebase.auth` directly from a ViewModel or
  composable.
- Never add dependencies by version string — use the version catalog.
