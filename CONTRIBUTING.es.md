# Contribuir a ETXCenter / SAVETX

¡Gracias por tu interés en contribuir a SAVETX! Esta guía está diseñada para ayudarte a configurar
tu entorno de desarrollo, comprender nuestra arquitectura híbrida en Kotlin Multiplatform (KMP) y
garantizar que tus cambios se integren sin fricciones.

---

## Índice

- [Objetivos del Proyecto](#objetivos-del-proyecto)
- [Stack Tecnológico](#stack-tecnológico)
- [Arquitectura y Estructura de Paquetes](#arquitectura-y-estructura-de-paquetes)
- [Reglas de Dependencias y Convenciones](#reglas-de-dependencias-y-convenciones)
- [Configuración y Ejecución del Proyecto](#configuración-y-ejecución-del-proyecto)
- [Estrategia de Ramas (Git Flow)](#estrategia-de-ramas-git-flow)
- [Convención de Commits y PRs](#convención-de-commits-y-prs)
- [Directrices para Pull Requests](#directrices-para-pull-requests)

---

## Objetivos del Proyecto

SAVETX es una aplicación móvil y de escritorio desarrollada con Kotlin Multiplatform diseñada para
visualizar datos competitivos en tiempo real recopilados por el ecosistema SAVETX.

Los objetivos principales de la aplicación son:

- Visualización de partidos en tiempo real (Live Match State).
- Historial detallado de enfrentamientos finalizados.
- Estadísticas globales e individuales de Equipos y Jugadores.
- Soporte multiplataforma nativo compartiendo UI (Android como objetivo principal del TFG, preparado
  para Desktop e iOS).

---

## Stack Tecnológico

| Capa / Componente             | Tecnología                                             |
|-------------------------------|--------------------------------------------------------|
| **Core Multiplatform**        | Kotlin 2.x                                             |
| **Interfaz de Usuario (UI)**  | Compose Multiplatform (Material 3)                     |
| **Inyección de Dependencias** | Koin Core + Koin Compose                               |
| **Base de Datos / Auth**      | GitLive Firebase Kotlin SDK (Auth & Realtime Database) |
| **Navegación**                | Jetpack Navigation Compose (KMP Type-Safe)             |
| **Serialización**             | `kotlinx.serialization` (JSON)                         |
| **Concurrencia**              | Kotlin Coroutines + Asynchronous Flows                 |

---

## Arquitectura y Estructura de Paquetes

Para garantizar la mantenibilidad y escalabilidad del TFG, el proyecto utiliza un enfoque **Híbrido
**: una separación por capas limpias para la infraestructura de datos, combinada con un diseño
orientado a características (*Feature-Driven*) para la interfaz de usuario.

Todo el desarrollo core se realiza en el módulo compartido (
`shared/src/commonMain/kotlin/com/kingsaul22/etxcenter/`).

```text
com.kingsaul22.etxcenter
├── core/                  # Infraestructura transversal y compartida
│   ├── di/                # Módulos de Koin
│   ├── navigation/        # Definición del NavHost y rutas estricta/Type-Safe
│   └── theme/             # Sistema de diseño Material 3 (Colores, Tipografía)
│
├── data/                  # Capa de datos (Acceso al mundo exterior)
│   ├── datasource/        # Conexiones directas de bajo nivel
│   ├── dto/               # Objetos de transferencia de datos de Firebase (@Serializable)
│   ├── mapper/            # Extensiones para mapear de DTO a Modelo de Dominio
│   └── repository/        # Implementaciones de las interfaces de dominio
│
├── domain/                # Reglas de negocio puras (Kotlin puro)
│   ├── model/             # Entidades de datos limpias (Player, Team, Match)
│   └── repository/        # Contratos/Interfaces de los repositorios
│
└── feature/               # UI y Flujos de usuario agrupados por funcionalidad
    ├── auth/              # Pantallas, ViewModels y estado de Autenticación
    ├── home/              # Dashboard principal de la aplicación
    ├── live/              # Visualización interactiva del estado en vivo
    └── stats/             # Históricos y listados estadísticos

```

---

## Reglas de Dependencias y Convenciones

Para evitar el acoplamiento y garantizar que la aplicación pueda compilarse en múltiples plataformas
sin efectos secundarios, se deben cumplir estrictamente las siguientes reglas:

### 1. Dirección del flujo de dependencias

Las capas de la aplicación solo pueden mirar en una dirección:

```text
feature (UI/ViewModel)  ──>  data (Repositories/DTOs)  ──>  domain (Modelos Puros)
   │                           │
   └───────────> [core] <──────┘

```

* **El Dominio es sagrado:** `domain/` no puede importar nada de `data/`, ni de `feature/`, ni
  librerías como Firebase o Compose. Es Kotlin puro.
* **La UI no habla con Firebase:** Está estrictamente prohibido usar referencias directas de
  Firebase (ej. `Firebase.database.reference(...)`) dentro de los composables o ViewModels de
  `feature/`. Todo el acceso a datos debe estar encapsulado detrás de una interfaz del repositorio
  de dominio.

### 2. Separación estricta entre DTOs y Modelos de Dominio

* Los objetos dentro de `data/dto/` reflejan fielmente la estructura JSON de la Realtime Database de
  Firebase y deben estar anotados con `@Serializable`.
* Los objetos dentro de `domain/model/` son los que consume la UI. No llevan anotaciones de
  persistencia y utilizan tipos de datos limpios (como `Instant` o `LocalDateTime` de
  `kotlinx-datetime` en lugar de *timestamps* planos de tipo `Long`).

### 3. Estructura de Características (Features)

Cada paquete dentro de `feature/` debe seguir una estructura MVVM limpia y predecible expuesta en 3
archivos:

* `*Screen.kt`: Contiene exclusivamente los componentes `@Composable` de la UI.
* `*ViewModel.kt`: Gestiona los eventos de la pantalla y procesa los flujos de datos.
* `*UiState.kt`: Clase de datos inmutable que representa el estado exacto de la UI en cualquier
  momento dado.

---

## Configuración y Ejecución del Proyecto

### Requisitos Previos

* Android Studio Jellyfish (o superior) con el plugin *Kotlin Multiplatform* instalado.
* JDK 17 o superior configurado en el entorno de ejecución de Gradle.

### Comandos Comunes de Desarrollo

Utiliza los siguientes comandos desde la raíz del proyecto para compilar y ejecutar los entornos
específicos:

```bash
# Ejecutar la aplicación en el Emulador/Dispositivo Android conectado
./gradlew :androidApp:installDebug

# Compilar el binario de depuración de Android
./gradlew :androidApp:assembleDebug

# Ejecutar la aplicación en Entorno de Escritorio (Desktop/JVM)
./gradlew :desktopApp:run

# Ejecutar el set completo de pruebas automatizadas en el módulo compartido
./gradlew :shared:allTests

```

*Para ejecutar el módulo de iOS (`iosApp/`), abre el directorio `/iosApp` en Xcode y lánzalo
utilizando un simulador compatible.*

---

## Estrategia de Ramas (Git Flow)

Este proyecto sigue estrictamente el modelo **GitHub Flow**:

* `main` es la rama protegida y de producción. Está prohibido realizar pushes directos a `main`.
* Toda nueva característica, corrección o cambio debe realizarse en una rama dedicada nacida del
  último estado de `main`.
* La integración de código se realiza única y exclusivamente mediante **Pull Requests (PR)**
  revisados y aprobados.

### Convención de Nombres de Rama

Las ramas deben nombrarse utilizando el formato en minúsculas `tipo/contexto-descripcion-corta`:

| Prefijo  | Propósito                                               | Ejemplo                            |
|----------|---------------------------------------------------------|------------------------------------|
| `feat/`  | Desarrollo de nuevas características de la app          | `feat/live-state-stream`           |
| `fix/`   | Corrección de fallos y bugs                             | `fix/navigation-backstack-crash`   |
| `core/`  | Cambios globales de arquitectura, DI o navegación       | `core/koin-viewmodel-refactor`     |
| `chore/` | Tareas de mantenimiento o actualización de dependencias | `chore/bump-compose-multiplatform` |

---

## Convención de Commits y PRs

Los títulos de los Pull Requests y los mensajes de commit principales deben seguir estrictamente la
especificación de **Conventional Commits**:

```text
tipo(alcance-opcional): descripción corta en imperativo

```

La descripción debe comenzar con un verbo en infinitivo o imperativo ("añadir", "corregir", "
actualizar", evitar tiempos pasados).

**Ejemplos válidos para este proyecto:**

* `feat(auth): implementar inicio de sesión anónimo con GitLive`
* `fix(live): corregir desfase en el marcador del partido en tiempo real`
* `core(di): registrar FirebasePlayerRepository en el módulo de Koin`
* `chore(deps): actualizar biblioteca kotlinx-serialization a v1.9`

---

## Directrices para Pull Requests

Al abrir un Pull Request, asegúrate de cumplir con los siguientes controles de calidad:

1. **Una única responsabilidad:** El PR debe resolver un único problema o implementar una única
   funcionalidad. No mezcles refactorizaciones globales con la corrección de un bug visual.
2. **Pruebas en verde:** Asegúrate de que `./gradlew :shared:allTests` compile y pase localmente
   antes de subir tus cambios al repositorio remoto.
3. **No subir secretos:** Nunca incluyas credenciales, llaves privadas ni archivos `.json` de
   configuración de Firebase privados en los commits. Utiliza los mecanismos nativos de inyección de
   propiedades del entorno.