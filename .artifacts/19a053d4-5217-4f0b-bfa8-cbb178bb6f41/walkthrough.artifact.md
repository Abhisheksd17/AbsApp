# Walkthrough - APK Size Optimization

I have implemented several strategies to reduce the project's APK size, which was reported at 300MB. These changes target the largest bloat factors: unused code, unused resources, and multi-architecture native libraries.

## Optimization Strategies Implemented

### 1. R8 Code & Resource Shrinking
- **File**: [app/build.gradle.kts](file:///E:/Personal Projects/AbsApp/AbsApp/app/build.gradle.kts)
- **Change**: Enabled `isMinifyEnabled = true` and `isShrinkResources = true` for the `release` build type.
- **Impact**: This will strip away all unused Java/Kotlin code and Android resources from large libraries like Agora, SQLCipher, and Material Icons Extended.

### 2. ABI Splitting
- **File**: [app/build.gradle.kts](file:///E:/Personal Projects/AbsApp/AbsApp/app/build.gradle.kts)
- **Change**: Added a `splits` block for ABI to generate separate APKs for different CPU architectures (`armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`).
- **Impact**: Instead of a single "universal" 300MB APK, you will get multiple smaller APKs (approx. 40-70MB each) that only contain the native code needed for specific devices.

### 3. Locale Resource Filtering
- **File**: [app/build.gradle.kts](file:///E:/Personal Projects/AbsApp/AbsApp/app/build.gradle.kts)
- **Change**: Added `localeFilters += "en"` to strip out localized strings from all dependencies except for English.
- **Impact**: Reduces the size of the final binary by removing thousands of unused translation files from Google/Android libraries.

### 4. ProGuard Rules for Heavy SDKs
- **File**: [app/proguard-rules.pro](file:///E:/Personal Projects/AbsApp/AbsApp/app/proguard-rules.pro)
- **Change**: Added custom `-keep` rules for Agora, SQLCipher, Cloudinary, and Hilt.
- **Impact**: Prevents R8 from over-optimizing and breaking the functionality of these native-dependent SDKs while still allowing it to shrink other parts of the app.

## Summary of Potential Size Reduction

| Feature | Original Size | Estimated Optimized Size |
| :--- | :--- | :--- |
| **Total Universal APK** | ~300 MB | N/A (Separated) |
| **Split APK (arm64-v8a)** | N/A | **~60 - 90 MB** |
| **Split APK (armeabi-v7a)** | N/A | **~50 - 80 MB** |

> [!TIP]
> To generate the optimized APKs, run:
> `./gradlew :app:assembleRelease`
> The output will be in `app/build/outputs/apk/release/`.

> [!IMPORTANT]
> For the Play Store, it is highly recommended to build an **Android App Bundle (AAB)** instead of APKs:
> `./gradlew :app:bundleRelease`
> Google Play will automatically serve the smallest possible version to each user.
