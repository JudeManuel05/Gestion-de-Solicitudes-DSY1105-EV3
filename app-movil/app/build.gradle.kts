plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cl.duoc.gestionsolicitudes"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.duoc.gestionsolicitudes"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        // URLs base. 10.0.2.2 = "localhost" del computador visto desde el EMULADOR.
        // En un teléfono físico se reemplaza por la IP del PC (ej: http://192.168.1.15:8081/)
        buildConfigField("String", "URL_MS_USUARIOS", "\"http://10.0.2.2:8081/\"")
        buildConfigField("String", "URL_MS_SOLICITUDES", "\"http://10.0.2.2:8082/\"")
        buildConfigField("String", "URL_API_EXTERNA", "\"https://mindicador.cl/\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.runtime.livedata)
    debugImplementation(libs.androidx.ui.tooling)

    // Navegación
    implementation(libs.androidx.navigation.compose)

    // Retrofit: microservicios propios + API externa
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Cámara y QR
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.zxing.core)

    // Imágenes
    implementation(libs.coil.compose)

    // Pruebas unitarias (carpeta src/test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
}
