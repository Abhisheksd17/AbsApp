plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.ui"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}


    dependencies {

        implementation(project(":core:common"))
        implementation(project(":core:model"))


        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.activity.compose)

        debugImplementation(libs.androidx.compose.ui.tooling)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // Hilt
        implementation(libs.hilt.android)
        ksp(libs.hilt.compiler)

        // Hilt Testing
        androidTestImplementation(libs.hilt.android.testing)
        kspAndroidTest(libs.hilt.compiler)
        testImplementation(libs.hilt.android.testing)
        kspTest(libs.hilt.compiler)

        implementation(libs.coil.compose)
    }
