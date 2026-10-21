plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.feature_auth"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        buildConfigField("String", "SOCKET_URL", "\"wss://absapp-backend.onrender.com\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:worker"))

    // Compose BOM (controls all versions)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)


    implementation(libs.work.runtime)
    implementation(libs.hilt.work)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}
