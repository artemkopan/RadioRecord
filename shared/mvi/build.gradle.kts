plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-android-extensions")
}

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "mvi"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:core"))
            }
        }
        val androidMain by getting{
            dependencies {
                implementation(Deps.AndroidX.LifeCycle.Viewmodel)
                implementation(Deps.AndroidX.LifeCycle.ViewmodelSavedstate)
            }
        }
        val iosMain by getting
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    compileSdkVersion(29)
    defaultConfig {
        targetSdkVersion(29)
        minSdkVersion(21)
    }
}
