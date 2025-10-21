plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.czy4201b.fastfill"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.czy4201b.fastfill"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        release {
            // ① 先看环境变量（CI 用）
            def storeFileEnv = System.getenv("RELEASE_STORE_FILE")
            if (storeFileEnv) {
                storeFile = file(storeFileEnv)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias      = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword   = System.getenv("RELEASE_KEY_PASSWORD")
                return        // 环境变量齐全，直接返回
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.release
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)        // 协程、Flow 支持
    ksp(libs.androidx.room.compiler)              // 注解处理器（KSP 最快）
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}