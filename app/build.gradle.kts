import com.android.build.api.dsl.ApplicationBuildType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soli.newframeapp"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.soli.newframeapp"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        //apk开头名称
        setProperty("archivesBaseName", "NewFrame")
    }

    signingConfigs {
        create("release") {
            storeFile = file("./platform.keystore")
            storePassword = "android"
            keyAlias = "platform"
            keyPassword = "android"
        }
    }

    flavorDimensions.add("dev")
    productFlavors {
        create("Rom")
        create("App")
    }

    buildTypes {
        val action = Action<ApplicationBuildType> {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release", action)
        getByName("debug", action)
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "dir" to "../aars", "include" to listOf("*.aar", "*.jar"))))
    implementation(project(":lib_common"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.floatwindow)
    implementation(libs.androidx.palette)
    implementation(libs.lottie)
}