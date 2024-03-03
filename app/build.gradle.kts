plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.ai_language"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ai_language"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true

        // 필요한 경우 추가적인 exclude를 여기에 추가합니다.
        packaging {
            resources {
                excludes.add("META-INF/INDEX.LIST")
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
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation(files("libs/activation.jar"))
    implementation(files("libs\\activation.jar"))
    implementation(files("libs\\additionnal.jar"))
    implementation(files("libs\\mail.jar"))
    testImplementation("junit:junit:4.13.2")
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
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
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
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    //KTX
    implementation("androidx.fragment:fragment-ktx:1.3.3")
    //WebRtc - 영상통화
    implementation(files("libs/autobanh.jar"))
    //Agora SDK
    implementation("io.agora.rtc:voice-sdk:4.2.6")
    //  MultiDex Libarary
    api("androidx.multidex:multidex:2.0.1")
    //Google Storage 버킷
    implementation("com.google.cloud:google-cloud-storage:1.113.16")

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

    //EncryptedSharedPreferences
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
}