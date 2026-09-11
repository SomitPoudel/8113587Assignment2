# 8113587Assignment2 — Art Gallery

NIT3213 Android assignment · Student ID: 8113587 · Sydney campus

An Android app that logs in through the course API, displays artwork summaries in a RecyclerView, and opens a details screen for the selected artwork.

## Features

- Student ID and first-name login with validation and error messages.
- Dashboard using the keypass returned by the server (`art` for this student).
- Artwork details, loading indicators, retry and sign-out.
- Hilt dependency injection and ViewModels with StateFlow.

## Setup

1. Clone this repository and open its root folder in Android Studio.
2. Use an Android Studio version compatible with the configured Android Gradle Plugin. The current project specifies AGP 9.4.0, Gradle 9.6.0, Kotlin 2.2.10 and Java 17 compilation targets. Configure a Gradle JDK compatible with those tools.
3. Install Android SDK Platform 35 and the build tools requested by Gradle, then sync the project. Initial dependency downloads require internet access.
4. Run the `app` configuration on an emulator or device with Android 7.0 (API 24) or later.
5. Log in using your student ID without `s` and your case-sensitive first name. Internet access is required for the API.

## Dependencies and structure

Dependencies are declared in `app/build.gradle.kts`: Hilt, Retrofit, Gson, OkHttp, Material Components, RecyclerView, Navigation, Lifecycle and Kotlin coroutines.

- `data`: API models and network repository.
- `domain`: artwork models and repository interface.
- `di`: Hilt dependency providers.
- `ui`: fragments, ViewModels and RecyclerView adapter.

API base URL: `https://nit3213apinew.onrender.com/`

- `POST /sydney/auth`: authenticates and returns the keypass.
- `GET /dashboard/{keypass}`: returns the artwork collection.

## Tests

15 tests cover login validation, ViewModel states, retries, API requests and response mapping. They use JUnit, coroutine test utilities, a fake repository and MockWebServer.

From the project root on Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

On macOS/Linux, use `./gradlew` instead. The test report is at `app/build/reports/tests/testDebugUnitTest/index.html`.

Test files are included; passing results are not asserted by this README. Before submission, check login, navigation, sign-out, error handling and screen rotation. The GitHub Actions workflow also defines test, lint and build checks.

