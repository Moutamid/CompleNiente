plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.moutamid.calenderapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.calenderapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "CalenderApp-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }


    packagingOptions {
        exclude("META-INF/DEPENDENCIES")

        // You can also exclude other conflicting files if needed
        // exclude 'META-INF/LICENSE'
        // exclude 'META-INF/LICENSE.txt'
        // exclude 'META-INF/NOTICE'
        // exclude 'META-INF/NOTICE.txt'
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
}

dependencies {
    // Calender Library
    implementation("com.github.nomanr:WeekCalendar:1.0.3")
    // Shared Preference Library
    implementation("com.github.akshay2211:Stash:1c45b0e5d5")
    // Image Libraries
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    // Firebase
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.google.firebase:firebase-crashlytics:19.0.3")
    implementation("com.google.firebase:firebase-analytics:22.1.0")
    implementation("com.google.firebase:firebase-messaging:24.0.1")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")

    implementation("com.android.volley:volley:1.2.1")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}