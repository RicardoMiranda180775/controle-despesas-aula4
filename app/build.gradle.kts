plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Gera automaticamente o código necessário para o Room.
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.controlefinanceiro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.controlefinanceiro"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Biblioteca do Room para trabalhar com o banco SQLite.
    implementation(libs.androidx.room.runtime)

    // Integração com Coroutines e Flow.
    implementation(libs.androidx.room.ktx)

    // Processador de código do Room.
    ksp(libs.androidx.room.compiler)
}
