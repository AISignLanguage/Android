import java.io.FileInputStream
import java.util.Properties

plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id ("dagger.hilt.android.plugin")
        id("org.jetbrains.kotlin.plugin.parcelize")
        kotlin("kapt")

        id("kotlin-kapt") // 'kotlin-kapt' 플러그인 추가
    }

    val properties = Properties()
    properties.load(FileInputStream("local.properties"))

    hilt {
        enableAggregatingTask = false
    }
    android {
        namespace = "com.example.ai_language"
        compileSdk = 34

        kapt {
            correctErrorTypes = true
        }
        dataBinding {
            enable = true
        }
        buildFeatures {
            viewBinding = true
            mlModelBinding = true
            buildConfig = true
        }
        defaultConfig {
            applicationId = "com.example.ai_language"
            minSdk = 29
            targetSdk = 33
            versionCode = 1
            versionName = "1.0"

            buildConfigField("String", "gmail_sender_id", properties.getProperty("gmail_sender_id"))
            buildConfigField("String", "gmail_sender_pwd", properties.getProperty("gmail_sender_pwd"))
            buildConfigField("String", "Main_Server", properties.getProperty("Main_Server"))
            buildConfigField("String", "Main_Server_8000", properties.getProperty("Main_Server_8000"))
            buildConfigField("String", "Google_Map_key", properties.getProperty("Google_Map_key"))
            buildConfigField("String", "Naver_Api_key_id", properties.getProperty("Naver_Api_key_id"))
            buildConfigField("String", "Naver_Api_key", properties.getProperty("Naver_Api_key"))
            buildConfigField("String", "Tmap_App_key", properties.getProperty("Tmap_App_key"))
            buildConfigField("String", "Tmap_Drive_App_key", properties.getProperty("Tmap_Drive_App_key"))
            buildConfigField("String", "Youtube_Api_key", properties.getProperty("Youtube_Api_key"))
            buildConfigField("String", "Kakao_app_key", properties.getProperty("Kakao_app_key"))
            buildConfigField("String", "Speech_to_Text_key_id", properties.getProperty("Speech_to_Text_key_id"))
            buildConfigField("String", "Speech_to_Text_key_secret", properties.getProperty("Speech_to_Text_key_secret"))
            buildConfigField("String", "Dictionary_Api_key", properties.getProperty("Dictionary_Api_key"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            multiDexEnabled = true

            // 필요한 경우 추가적인 exclude를 여기에 추가합니다.
            packaging {
                resources {
                    excludes.add("META-INF/INDEX.LIST")
                    excludes.add("google/protobuf/field_mask.proto")
                }
            }

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
        aaptOptions{
            noCompress("tflite")
        }
        buildToolsVersion = "34.0.0"

        configurations.all {
            exclude(module = "protobuf-java")
        }
    }

dependencies {

    implementation ("com.google.cloud:google-cloud-storage:1.113.16") {
        exclude(group = "com.google.protobuf", module="protobuf-java")
        exclude(group = "com.google.protobuf", module="protobuf-javalite")
    }
//    implementation ("com.google.mediapipe:tasks-vision:0.10.0")  {
//        exclude(group = "com.google.protobuf", module="protobuf-java")
//        exclude(group = "com.google.protobuf", module="protobuf-javalite")
//    }

    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("com.google.dagger:hilt-android:2.50")
    implementation("androidx.activity:activity:1.8.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    kapt ("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation(files("libs/activation.jar"))
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))
    testImplementation("junit:junit:4.13.2")
    implementation ("io.socket:socket.io-client:2.0.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1") // 'X.X.X'를 최신 버전으로 교체하세요

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.kakao.sdk:v2-template:2.19.0") // 카카오톡 메시지 API
    implementation("com.kakao.sdk:v2-link:2.5.3") // 카카오톡 메시지 링크 API
    implementation("com.kakao.sdk:v2-all:2.19.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user:2.19.0") // 카카오 로그인
    implementation("com.kakao.sdk:v2-talk:2.19.0")// 친구, 메시지(카카오톡)
    implementation("com.kakao.sdk:v2-share:2.19.0") // 메시지(카카오톡 공유)
    implementation("com.kakao.sdk:v2-friend:2.19.0") // 카카오톡 소셜 피커, 리소스 번들 파일 포함
    implementation("com.kakao.sdk:v2-navi:2.19.0") // 카카오내비
    implementation("com.kakao.sdk:v2-cert:2.19.0") // 카카오 인증서비스
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    //JetPack CameraX API
    var camerax_version = ("1.1.0-beta01")
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    //KTX
    implementation("androidx.fragment:fragment-ktx:1.7.0")

    //WebRtc - 영상통화
    implementation(files("libs/autobanh.jar"))

    //Agora SDK
    implementation("io.agora.rtc:voice-sdk:4.2.6")

    //  MultiDex Libarary
    api("androidx.multidex:multidex:2.0.1")

    //Google Storage 버킷
    implementation("com.google.cloud:google-cloud-storage:1.113.16")
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    //RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.0.3")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")

    // 이메일 전송 (SMTP - Gmali)
    implementation(files("libs/activation.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(files("libs/mail.jar"))
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.3.1")
    //EncryptedSharedPreferences
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    // MediaPipe
    implementation ("com.google.mediapipe:hands:0.10.14")
    implementation ("com.google.mediapipe:solution-core:0.10.14")

    // Pytorch
    //implementation ("org.pytorch:pytorch_android:2.1.0")

    implementation ("org.pytorch:pytorch_android_lite:2.1.0") {
        exclude(group = "org.pytorch", module="pytorch_android")
        //exclude(group = "com.google.protobuf", module="protobuf-javalite")
    }
    implementation ("org.pytorch:pytorch_android_torchvision:2.1.0") {
        exclude(group = "org.pytorch", module="pytorch_android")
        //exclude(group = "com.google.protobuf", module="protobuf-javalite")
    }

    implementation("com.google.guava:guava:31.0.1-android")

    // To use CallbackToFutureAdapter
    implementation("androidx.concurrent:concurrent-futures:1.1.0")

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.0")

    // tensorflow Lite
    implementation ("org.tensorflow:tensorflow-lite:2.16.1")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation ("org.tensorflow:tensorflow-lite-task-audio:0.4.4")
    implementation ("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.16.1")

    implementation ("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    implementation ("com.google.android.gms:play-services-tflite-gpu:16.2.0")
    implementation  ("org.tensorflow:tensorflow-lite-task-vision:0.4.0")


    //naver Map
    implementation("com.naver.maps:map-sdk:3.18.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    //Youtube View
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation ("com.github.hannesa2:AndroidSlidingUpPanel:4.2.1")


    //QR code
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
}
