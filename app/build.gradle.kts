plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.dinocode.tiendavirtualapp_kotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dinocode.tiendavirtualapp_kotlin"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie) /*Animaciones*/
    implementation(libs.firebaseAuth) /*Autenticación con Firebase*/
    implementation(libs.firebaseDatabase) /*Base de datos con Firebase*/
    implementation(libs.imagePicker) /*Recortar una imagen*/
    implementation(libs.glide) /*Leer imágenes*/
    implementation(libs.storage)/*Subir archivos multimedia*/
    implementation(libs.authGoogle) /*Iniciar sesión con google*/
    implementation(libs.ccp) /*Seleccionar nuestro código telefónico por país*/
    implementation(libs.circleImage)
    implementation(libs.photoView)
    implementation(libs.maps)
    implementation(libs.places)
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.okhttp3)
    implementation(libs.browser)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}