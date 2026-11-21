plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "br.com.moraisdrive"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.moraisdrive"
        minSdk = 23
        targetSdk = 35
        versionCode = 7
        versionName = "1.3.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.car.app:app:1.7.0")
}
