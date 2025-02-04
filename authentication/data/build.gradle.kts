plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.upsaclay.authentication.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
}

dependencies {

    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.koin)
    implementation(libs.koin.core)
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)

    implementation(project(":common:data"))
    implementation(project(":common:domain"))
    implementation(project(":authentication:domain"))
}
