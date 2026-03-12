/**
 * CONCEPTO CLAVE: build.gradle.kts (módulo :app)
 *
 * Este archivo configura el módulo de la aplicación Android.
 * Usa Kotlin DSL (archivos .kts) en lugar del antiguo Groovy DSL.
 *
 * Secciones principales:
 *  - plugins: herramientas de compilación (Android + Kotlin + Compose)
 *  - android: configuración específica del proyecto Android
 *  - dependencies: librerías externas que usa la app
 */
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.calculadora"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.calculadora"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    /**
     * CONCEPTO: buildFeatures { compose = true }
     * Activa el compilador de Jetpack Compose. Sin esto, los archivos
     * que usen @Composable no compilarán.
     */
    buildFeatures {
        compose = true
    }

    /**
     * CONCEPTO: composeOptions
     * Versión del compilador de Compose que debe ser compatible
     * con la versión de Kotlin usada.
     */
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core de Android con extensiones Kotlin (KTX)
    implementation("androidx.core:core-ktx:1.12.0")

    // Lifecycle y ViewModel — sobreviven rotaciones de pantalla
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Activity con soporte para Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    /**
     * CONCEPTO: Compose BOM (Bill of Materials)
     * El BOM garantiza que todas las librerías de Compose usen
     * versiones compatibles entre sí sin tener que especificarlas
     * una por una.
     */
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material Design 3 — sistema de diseño moderno de Google
    implementation("androidx.compose.material3:material3")

    // Tests unitarios (corren en la JVM, sin emulador)
    testImplementation("junit:junit:4.13.2")

    // Tests instrumentados (corren en un dispositivo/emulador)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Herramientas de depuración de Compose (solo en builds de debug)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
