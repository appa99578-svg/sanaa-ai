# SANAA AI - Phase 1 (NOT compiled or tested yet)

Stack: Kotlin 1.9.24, Compose BOM 2024.06, AGP 8.5.2, Gradle 8.7, JDK 17, compileSdk/targetSdk 34, minSdk 26.
APK output: app/build/outputs/apk/debug/app-debug.apk

## Option A - remote build (recommended)
1. Push this folder to a GitHub repo (branch main).
2. Actions tab -> "Build SANAA AI APK" -> Run workflow.
3. Download artifact sanaa-ai-debug-apk and install it.

## Option B - Termux (experimental, unverified)
Known blocker: Google's Android SDK build-tools (aapt2) are x86 binaries; they do not run on ARM64 Termux.
You need an ARM64 aapt2 and an aarch64 SDK, then:
    pkg install openjdk-17 gradle
    export ANDROID_HOME=<sdk path>
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    # uncomment android.aapt2FromMavenOverride in gradle.properties
    gradle assembleDebug --stacktrace
The gradle-wrapper.jar is not included. Run `gradle wrapper --gradle-version 8.7` once to get ./gradlew.
