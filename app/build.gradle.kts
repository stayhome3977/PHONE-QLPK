import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// The QLPK API deployed on Azure. Override per machine in local.properties, e.g.
//   qlpk.apiRoot=http://10.0.2.2:5131/api/
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val apiRoot: String = localProperties.getProperty("qlpk.apiRoot", "https://app-qlpk-hoangqlpk97.azurewebsites.net/api/")
    .let { if (it.endsWith("/")) it else "$it/" }

android {
    namespace = "com.example.quanlyphongkham"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.quanlyphongkham"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_ROOT", "\"$apiRoot\"")
        // Same Cloudflare Turnstile widget as the web portal (QLPK_FE/.env.production). The widget only
        // accepts hostnames registered for it, so the captcha page is served under the portal's host.
        buildConfigField("String", "TURNSTILE_SITE_KEY", "\"0x4AAAAAAEgL308hsDpDZ-XH\"")
        buildConfigField("String", "TURNSTILE_HOST", "\"https://wonderful-plant-05c95c300.6.azurestaticapps.net/\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
