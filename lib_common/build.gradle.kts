plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soli.libcommon"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        //vectorDrawable 使用android.support库支持，支持低版本，默认是使用系统的
        vectorDrawables.useSupportLibrary = true
        consumerProguardFiles("consumer-rules.pro")
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
    api(libs.androidx.multidex)

    api(libs.androidx.appcompat)
    api(libs.androidx.recyclerview)
    api(libs.material)
    api(libs.androidx.cardview)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.core.ktx)
    api(libs.androidx.fragment.ktx)

    api(libs.flexbox)

// Vector drawable
    api(libs.androidx.vectordrawable)
//    implementation(libs.androidx.vectordrawable.animated)

// RxJava
    api(libs.rxandroid)
    api(libs.rxbinding)
    api(libs.rxjava3)
//    implementation(libs.rxlifecycle)

// Network
    api(libs.okhttp.logging.interceptor)
    api(libs.retrofit)
    api(libs.okhttp)

    api(libs.svga)

// Image loading
    api(libs.fresco)
    api(libs.fresco.okhttp3)
    api(libs.fresco.animatedgif)

// Debug tools
    api(libs.stetho)

// Third-party libraries
    api(libs.fastjson)
    api(libs.gson)
    api(libs.jsoup)

    api(libs.eventbus)
    api(libs.rxpermissions)
    api(libs.materialish.progress)
//    implementation(libs.pullupdown)

// Old android libraries
    api(libs.nineoldandroids)

// Skin support (commented out)
// implementation(libs.skin.support)
// implementation(libs.skin.support.appcompat)
// implementation(libs.skin.support.cardview)
// implementation(libs.skin.support.constraintlayout)
// implementation(libs.skin.support.design)

// Image subsampling
    api(libs.scaleimage)

// Swipe layout
    api(libs.swipelayout)

// Logger
    api(libs.logger)

// Flutter (commented out for debug/release builds)
// debugImplementation 'com.soli.flutter_module:flutter_debug:1.1.2'
// releaseImplementation 'com.soli.flutter_module:flutter_release:1.1.2'

}
