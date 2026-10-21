plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.data"
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

    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))
    implementation(project(":core:websocket"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.kotlinx.serialization.converter)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.cloudinary.android)
    implementation(libs.libphonenumber)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.perf)
}