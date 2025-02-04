import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

val gedoiseVm1BaseUrl = project.findProperty("GEDOISE_VM_1_BASE_URL") as String
val gedoiseVm2BaseUrl = project.findProperty("GEDOISE_VM_2_BASE_URL") as String

android {
    namespace = "com.upsaclay.common.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "SERVICE_1_BASE_URL",
                "\"${localProperties.getProperty("LOCAL_SERVER_BASE_URL")}\"",
            )

            buildConfigField(
                "String",
                "SERVICE_2_BASE_URL",
                "\"${localProperties.getProperty("LOCAL_SERVER_BASE_URL")}\"",
            )
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            buildConfigField(
                "String",
                "SERVICE_1_BASE_URL",
                "\"$gedoiseVm1BaseUrl\"",
            )

            buildConfigField(
                "String",
                "SERVICE_2_BASE_URL",
                "\"$gedoiseVm2BaseUrl\"",
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.okhttp)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.koin)
    implementation(libs.koin.core)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.jakewharton.timber)
    implementation(libs.gson)

    testImplementation(libs.junit)

    implementation(project(":common:domain"))
}
