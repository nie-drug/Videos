plugins {
    id("com.android.application")
}

android {
    buildToolsVersion = "34.0.4"
    namespace = "com.projectmaterial.videos"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.projectmaterial.videos"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
}

dependencies {
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.19")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.material:material:1.13.0-alpha01")
}