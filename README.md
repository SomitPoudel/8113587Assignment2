# 8113587Assignment2 — Art Gallery

NIT3213 final assignment · Student ID: 8113587 · Sydney campus · Assigned topic: `art`.

An Android app with Login, Dashboard and Details screens. The app authenticates against the course API, loads the topic returned by authentication and displays artwork summaries in a RecyclerView. Selecting an artwork shows every returned field, including its description.

## Build and run

1. Extract the ZIP, including its `.git` directory, into a normal writable folder.
2. In Android Studio, choose **Open** and select the `8113587Assignment2` folder containing `settings.gradle.kts`. The Android Studio project name is set to `8113587Assignment2`.
3. Use Android Studio Ladybug 2024.2.1 or a newer version compatible with Android Gradle Plugin 8.7.3. Set the Gradle JDK to **17**.
4. In SDK Manager, install **Android SDK Platform 35**, **SDK Build-Tools 34.0.0**, and Platform-Tools. Let Android Studio create your local SDK configuration; `local.properties` is intentionally not committed.
5. Allow Gradle to sync. Internet access to Google's Maven repository, Maven Central and Gradle distribution servers is required on the first build.
6. Start an emulator or connect an Android device running Android 7.0 / API 24 or later. Select the `app` configuration and click **Run**.
7. Enter your numeric student ID without an `s` and your first name with its correct capitalization. The password is entered at runtime and is not embedded in the project.
8. Successful login opens the collection. Select a card to open details. Use Back to return, or Sign out to return to login.

The seven-digit ID supplied for this project is accepted. The eight-digit ID in the specification is treated as an example, not an exact-length restriction.

### Command line

With Java 17 and the Android SDK configured:

```bash
# macOS / Linux; restore executable permission if your ZIP tool removed it
chmod +x gradlew
./gradlew testDebugUnitTest assembleDebug lintDebug
```

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The test report is generated at `app/build/reports/tests/testDebugUnitTest/index.html`.

## API configuration

Base URL: `https://nit3213apinew.onrender.com/`

- Login: `POST sydney/auth`, with JSON properties `username` and `password`.
- Dashboard: `GET dashboard/{keypass}`.
- Expected topic for this student: `art`. The application always uses the **actual returned keypass**; it does not bypass authentication or force a fixed result.
- Observed artwork fields: `artworkTitle`, `artist`, `medium`, `year`, `description`.

`ArtApi.kt` defines the endpoints. `AppModule.kt` provides the Retrofit client and base URL. The network response is mapped into `Artwork`, preserving every returned field. This avoids silently dropping information if the API adds another field. Descriptions are excluded only from the dashboard summary.

## Architecture and dependency injection

The app uses Kotlin, XML layouts, a single activity, fragments, Jetpack Navigation, View Binding, ViewModels, StateFlow and coroutines.

- `data`: Retrofit API contracts and `NetworkArtRepository`, which maps network responses into domain models.
- `domain`: `ArtRepository` interface, `Artwork` and `Gallery`, independent of Android UI components.
- `di`: Hilt module providing singleton OkHttp, Retrofit API and repository dependencies.
- `ui`: ViewModels, state definitions, screen fragments and RecyclerView adapter.

`ArtApplication` starts Hilt with `@HiltAndroidApp`. The activity and fragments hosting injected ViewModels use `@AndroidEntryPoint`. Hilt creates the `@HiltViewModel` classes and resolves their `ArtRepository` dependencies through `AppModule`. The network repository receives `ArtApi` through constructor injection. Tests can supply a fake repository without a network connection.

ViewModels own request state and survive rotation. Fragments collect it only while their views are started. The dashboard's navigation argument is supplied through `SavedStateHandle`, allowing a restored screen to reload its data after process recreation. The selected artwork is passed as JSON in a navigation argument so the Details screen can be restored without relying on a global mutable selection.

## Error handling and usability

- Blank or nonnumeric student IDs and blank passwords are rejected before a request.
- Login preserves password capitalization and prevents duplicate requests while loading.
- Loading indicators and disabled login controls make request progress visible.
- HTTP failures, connectivity problems and invalid responses produce readable errors.
- The dashboard provides a retry button and an empty-state message.
- Scrolling layouts accommodate longer details and smaller screens.
- System-bar and keyboard insets keep content clear of system controls.
- Material components support light and dark themes and labelled input fields.
- Password text is not saved in view state; it is cleared after successful login. No request-body logging, analytics, or credential file storage is enabled. Android backup and cleartext traffic are disabled.

## Tests

15 JUnit tests are included:

| Test file | Coverage |
| --- | --- |
| `ViewModelTest.kt` | Input validation, seven-digit ID, case preservation, loading state, duplicate-request suppression, successful login, network error and retry, restored keypass forwarding, dashboard load and retry, empty results, missing session, summary/detail separation |
| `NetworkArtRepositoryTest.kt` | Sydney POST route and JSON body, dashboard route and artwork mapping, numeric year conversion, HTTP 401 propagation, blank keypass, malformed dashboard response, valid empty collection |

ViewModel tests use a fake repository with the coroutine test dispatcher. Repository tests use MockWebServer and real Retrofit serialization, so they do not depend on the course server or expose real passwords. A GitHub Actions workflow runs the unit tests, lint and debug build when the repository is pushed.

## Verification status

Completed during preparation on 7 September 2026:

- Live Sydney authentication returned HTTP 200 with `keypass: art`.
- Live `/dashboard/art` returned seven artworks with the five fields listed above.
- XML files were parsed and local resource references checked.
- Gradle wrapper scripts and wrapper JAR are included.

**Not completed in the preparation environment:** Android compilation, execution of the 15 JUnit tests, lint and emulator/device testing. The initial Gradle invocation failed while downloading Gradle because the Java process could not reach the distribution server; this workspace also lacked an installed Android SDK. No APK or passing test result is claimed. Run the commands above in Android Studio before submitting.

### Manual acceptance checks before submission

- Valid login opens the seven-artwork dashboard.
- Incorrect capitalization in the password shows a server error without opening the dashboard.
- Empty inputs are rejected immediately.
- Every card shows title, artist, medium and year, with no description.
- Selecting a card displays all five fields, including the full description.
- Device Back and the details Back button return to the collection.
- Rotation on Dashboard and Details preserves the current screen and content.
- Disable networking and attempt login; confirm a readable error. For the dashboard, disable networking before login finishes or recreate the process on that screen, then restore networking and retry.
- Sign out opens Login and Back cannot reveal the previous authenticated screen.
- Check small-screen, landscape, dark-theme and larger-font layouts.

## Git repository submission

The assignment requires a **Git repository URL**, not just a ZIP. The archive includes the actual Git history created during this preparation session. Commits are attributed to Codex and are not backdated or presented as earlier student work.

Create an empty repository in your Git hosting account. From the extracted project folder, add its URL and push:

```bash
git remote add origin YOUR_REPOSITORY_URL
git push -u origin main
```

If the ZIP extraction omitted `.git`, extract again with a tool that preserves all files. Do not reinitialize Git if you want to retain the supplied history. Check `git log --oneline` before pushing. Give the marker access if the repository is private. Submit the resulting repository link through the required course submission system. No remote repository has been created or submitted from this environment.

## Learning references and assistance

This implementation follows the supplied NIT3213 final assignment specification and lecture concepts from L2/L3 (Android and XML UI), L4 (clean code and navigation), L5 (Retrofit and coroutines), L6 (RecyclerView), L7 (Hilt), L8 (testing), and the ethical/practical considerations slides (credential handling).

- [Android Gradle Plugin 8.7 compatibility](https://developer.android.com/build/releases/agp-8-7-0-release-notes)
- [Hilt](https://dagger.dev/hilt/)
- [Retrofit](https://square.github.io/retrofit/)

The project was prepared with AI assistance. Review the implementation, run it, and follow your unit's rules for acknowledging assistance before submission.
