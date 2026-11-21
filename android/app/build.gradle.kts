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
        versionCode = 3
        versionName = "1.2.0"
    }

    dependencies {\n        implementation("androidx.car.app:app:1.7.0")\n    }\n\n    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
