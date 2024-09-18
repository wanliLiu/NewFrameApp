plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soli.libcommon"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        //vectorDrawable 使用android.support库支持，支持低版本，默认是使用系统的
        vectorDrawables.useSupportLibrary = true
    }

    kotlinOptions {
        jvmTarget = "17"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

// Kotlin support
//    implementation(libs.kotlinx.coroutines.core)
//    implementation(libs.kotlinx.coroutines.android)

// Android support
    implementation(libs.androidx.multidex)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.flexbox)

// Vector drawable
    implementation(libs.androidx.vectordrawable)
//    implementation(libs.androidx.vectordrawable.animated)

// RxJava
    implementation(libs.rxandroid)
    implementation(libs.rxbinding)
    implementation(libs.rxjava3)
//    implementation(libs.rxlifecycle)

// Network
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    implementation(libs.svga)

// Image loading
    implementation(libs.fresco)
    implementation(libs.fresco.okhttp3)
    implementation(libs.fresco.animatedgif)

// Debug tools
    implementation(libs.stetho)

// Third-party libraries
    implementation(libs.fastjson)
    implementation(libs.gson)
    implementation(libs.jsoup)

    implementation(libs.eventbus)
    implementation(libs.rxpermissions)
    implementation(libs.materialish.progress)
//    implementation(libs.pullupdown)

// Old android libraries
    implementation(libs.nineoldandroids)

// Skin support (commented out)
// implementation(libs.skin.support)
// implementation(libs.skin.support.appcompat)
// implementation(libs.skin.support.cardview)
// implementation(libs.skin.support.constraintlayout)
// implementation(libs.skin.support.design)

// Image subsampling
    implementation(libs.scaleimage)

// Swipe layout
    implementation(libs.swipelayout)

// Logger
    implementation(libs.logger)

// Flutter (commented out for debug/release builds)
// debugImplementation 'com.soli.flutter_module:flutter_debug:1.1.2'
// releaseImplementation 'com.soli.flutter_module:flutter_release:1.1.2'

}
