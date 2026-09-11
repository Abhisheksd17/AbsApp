# Implementation Plan - APK Size Optimization

The project's APK size (reported as 300MB) is primarily driven by large native SDKs (Agora, SQLCipher), lack of code/resource shrinking, and the inclusion of multiple CPU architectures (ABIs) in a single APK.

## Analysis of Culprits

> [!NOTE]
> 1. **Agora Full SDK**: `io.agora.rtc:full-sdk` includes massive native libraries (.so files) for video and audio across all architectures.
> 2. **SQLCipher**: Adds significant native overhead for database encryption.
> 3. **No Minification**: `isMinifyEnabled = false` is currently set for release builds, preventing R8 from stripping unused code and resources.
> 4. **Compose Material Icons Extended**: Including this library adds several megabytes of icon data, most of which is likely unused.
> 5. **Universal APK**: The build currently packages all native libraries (arm64-v8a, armeabi-v7a, x86, x86_64) into one file.

## Proposed Changes

### [app]

#### [MODIFY] [app/build.gradle.kts](file:///E:/Personal Projects/AbsApp/AbsApp/app/build.gradle.kts)
- Enable R8 code shrinking and resource shrinking for `release` builds.
- Configure `splits` or `bundle` to generate architecture-specific APKs.
- Add `resConfigs "en"` (or other primary languages) to strip unused translations from libraries.

### [Build Optimization]

#### [MODIFY] All library `build.gradle.kts` files
- Enable `isMinifyEnabled = true` for release builds to ensure consumer modules can benefit from shrinking.

## Optimization Strategies

### 1. Enable R8 Shrinking
We will set `isMinifyEnabled = true` and `isShrinkResources = true`. This is the most effective way to reduce size as it removes unused code from heavy libraries like Agora and SQLCipher.

### 2. ABI Splitting
We will configure the build to produce separate APKs for different hardware architectures. A user with an ARM64 phone will only download the ARM64 libraries, reducing their download size by 60-70%.

### 3. Icon Optimization
We will investigate if we can replace `material-icons-extended` with specific icon imports to avoid pulling in thousands of unused icons.

## Verification Plan

### Manual Verification
- Run `./gradlew :app:assembleRelease` and compare the size of the generated APK(s) to the original.
- Use the **APK Analyzer** in Android Studio to verify that native libraries and resources have been effectively pruned.
- Test the application (especially Video/Audio calls and Database) to ensure R8 hasn't removed necessary code (we may need to add `@Keep` annotations or ProGuard rules).
