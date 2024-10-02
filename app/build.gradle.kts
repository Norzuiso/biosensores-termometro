// build.gradle.kts (nivel módulo :app)
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.termometro_app_java"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.termometro_app_java"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // MPAndroidChart library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Aquí la dependencia
}
