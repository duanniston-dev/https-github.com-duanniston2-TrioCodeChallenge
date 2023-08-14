plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    kotlin("plugin.serialization") version "1.9.0"

}

android {
    namespace = "br.com.duannistontriocodechallenge"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.duannistontriocodechallenge"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    //Android X
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //Google
    implementation("com.google.android.material:material:1.9.0")
    //Koin
    implementation("io.insert-koin:koin-android:3.4.3")
    //  Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    //Test
    //Android X
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //JUnit
    testImplementation("junit:junit:4.13.2")

}
