plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

setupAndroidSdkVersions()

android {
    defaultConfig {
        applicationId = "io.radio"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(project(":shared:app"))
    implementation(project(":shared:core"))
    implementation(project(":shared:mvi"))

//    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
    implementation(Deps.AndroidX.Core.CoreKtx)
    implementation(Deps.AndroidX.AppCompat.AppCompat)
    implementation(Deps.AndroidX.Fragment.Ktx)
    implementation(Deps.AndroidX.LifeCycle.Viewmodel)
    implementation(Deps.AndroidX.LifeCycle.ViewmodelSavedstate)
    implementation(Deps.AndroidX.LifeCycle.CommonJava8)
    implementation(Deps.AndroidX.LifeCycle.RuntimeKtx)
    implementation(Deps.AndroidX.RecyclerView.RecyclerView)
    implementation(Deps.AndroidX.ConstraintLayout.ConstraintLayout)
    implementation(Deps.AndroidX.Palette.Palette)
    implementation(Deps.AndroidX.ViewPager2.ViewPager2)
    implementation(Deps.AndroidX.Navigation.FragmentKtx)

//    implementation(Deps.Koin.AndroidScope)

    implementation(Deps.Google.Android.Material.Material)
    implementation(Deps.Timber.Timber)
}
