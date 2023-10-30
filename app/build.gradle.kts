plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

android {
    namespace = "com.example.projetomovimenta"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projetomovimenta"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.3")
    implementation("androidx.navigation:navigation-ui:2.7.3")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-auth:22.1.2")
    implementation ("com.google.firebase:firebase-firestore:24.8.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation ("com.firebaseui:firebase-ui-auth:8.0.1")
    implementation ("com.google.firebase:firebase-database:20.2.1")
    implementation ("com.google.firebase:firebase-appcheck:17.0.0") // Substitua pela versão correta
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("androidx.webkit:webkit:1.4.0") // Exemplo de dependência do WebView
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.0.0")
    implementation ("com.google.android.libraries.places:places:2.7.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")



}



// Dependências que você tinha comentado, mas estão descomentadas agora:
// testImplementation("junit:junit:4.13.2")
// androidTestImplementation("androidx.test.ext:junit:1.1.5")
// androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")