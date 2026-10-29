# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

WT4 is a JavaFX desktop app for tracking time as JIRA worklogs. It works offline against a local SQLite database and syncs with JIRA. It's written mostly in Kotlin, with some Java. UI is TornadoFX + JFoenix + CalendarFX, DI is Dagger 2 (kapt), async work uses RxJava **1** (`rx.*`, not `io.reactivex.rxjava3`), and DB access uses jOOQ.

## Build / run / test

The JDK is pinned in `.sdkmanrc` (`java=11.0.31.fx-zulu`, a JDK that bundles JavaFX). Run `sdk env` to switch to it. Gradle uses the Kotlin DSL, and shared dependency versions live in `buildSrc/src/main/java/lt/markmerkk/Versions.kt`.

```sh
./gradlew build                      # build all modules + run tests
./gradlew :app:run                   # launch the app (debug profile, data in ~/.wt4_debug)
./gradlew test                       # all tests
./gradlew :components:test           # tests of one module
./gradlew :remote:test --tests 'lt.markmerkk.worklogs.WorklogUploadValidatorTest'
./gradlew :models:test --tests '*LogTest.someMethodName'
```

Tests use JUnit 4, mockito-kotlin, and AssertJ. Each module has `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`, which enables mocking final Kotlin classes. Test fixtures and mock builders live in the `:mock-factory` module.

### Packaging (custom `jbundle` plugin in `buildSrc`)

- `./gradlew :app:jbundleCreate` builds the app, then runs jpackage/jlink scripts from `app/package/scripts/` to make a native bundle. `jbundleGenIcons` generates the Mac/Win icons.
- The release profile is chosen by **editing** the `val jBundleProps = JBundleExtraPropsFactory...` line at the top of `app/build.gradle.kts`. The options are `Debug.asBasic` or `Release.asBasicWin/asBasicMac/asBasicMacX64/asBasicLinux`, and each release variant checks the host OS. The chosen profile also feeds the generated `BuildConfig` (flavor, version, debug, sentry DSN, GA key) and the `-DWT_APP_PATH` JVM prop. Keep it on `Debug.asBasic` unless you're doing a release.
- The version is set in `version.properties` (`version_name`, `version_code`). `version_code` also drives config-dir migrations (`ConfigPathMigrationFactory`).

## Module layout

These are the modules included in `settings.gradle.kts`, in dependency order:

- `models`: entities (`Log`, `Ticket`, `RemoteData`, …), shared interfaces (`TimeProvider`, `UserSettings`, …), `Const` (system-property keys `WT_ROOT`, `WT_APP_PATH`, `WT_TMP`).
- `database2`: SQLite via jOOQ. `WorklogStorage` and `TicketStorage` are the main entry points. Schema migrations are in `migrations/MigrationXToY.kt` and are run by `MigrationsRunner`.
- `remote`: JIRA integration. It wraps the prebuilt `libs/jira-client2-2.1.0-SNAPSHOT.jar`. `SyncInteractorImpl` holds the sync pipeline, and the `worklogs/` and `tickets/` packages hold the API, emitter and validator classes.
- `components`: UI-agnostic business logic (time counting, ticket search/filter, validators, export, versioning, `ConfigPathProvider`, `LogEditService2`). It depends on `models`, `remote` and `database2`.
- `app`: the JavaFX/TornadoFX UI and the Dagger graph. Its main class is `lt.markmerkk.MainAsJava` → `Main` (TornadoFX `App`).
- `credits`, `mock-factory`: small support modules.

Vendored/prebuilt dependencies: `libs/` holds JFoenix 21 and jira-client2 as jars. The `JFoenix/` and `jira-client2/` git submodules are **not** part of the build (they're commented out in `settings.gradle.kts`). To rebuild jira-client2 you have to apply `remote/jira-client-build.patch` (see the comment in `remote/build.gradle.kts`).

## Architecture notes

- **DI**: `app/.../dagger/components/AppComponent.kt` with modules `AppModule`, `NetworkModule` and `SyncModule`. `AppModule` builds the `DBConnProvider` and runs the full DB migration chain at graph creation. `Main.restart()` rebuilds the whole Dagger graph and re-attaches the UI, for example after a settings or profile change. Widgets get their dependencies with `Main.component().inject(this)`.
- **UI pattern**: MVP. Each feature under `app/.../widgets/<feature>/` usually has a `*Contract.kt` (View/Presenter interfaces), a `*Presenter*.kt`, and a TornadoFX `*Widget.kt` (`View`/`Fragment`). Presenters use `onAttach/onDetach` and RxJava subscriptions on `SchedulerProviderFx` (FX UI thread vs IO). Cross-widget communication goes through `WTEventBus` (Guava EventBus) with event classes in `models/.../events` and `app/.../events`.
- **Data model / sync**: `Log` keeps local data plus optional `RemoteData`. `isRemote` means it was synced, and `isMarkedForDeletion` means it was deleted locally and is waiting for a remote delete. The sync order in `SyncInteractorImpl.syncLogs` is: fetch the JIRA user → delete marked logs remotely → upload local logs → fetch remote logs → delete local logs unknown to remote, all for the active display date range.
- **DB schema changes**: add a new `MigrationNToN+1` in `database2/.../migrations/`, register it in the list in `AppModule`, and update the jOOQ generated classes. The generated classes are in `database2/generate/src`, which is a source dir of `database2`. Regenerate them with `database2/generate/generate.sh`, which rebuilds the DB from `initial.sql`, so update `initial.sql` too.
- **Runtime data dir**: `~/.<WT_APP_PATH>`, which is `~/.wt4_debug` for debug and `~/.wt4` for release. `-DWT_ROOT` overrides the home root. The dir holds the SQLite DB (`wt4_2.db`), settings and logs (logback config in `app/src/main/resources/logback.xml`).
- **Strings and assets**: `app/src/main/resources/translations_en.properties`, `graphics.properties` (SVG glyph paths), and `Styles.kt` (TornadoFX stylesheet).
