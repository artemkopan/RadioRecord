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
                baseName = "core"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Jetbrains.Kotlinx.Coroutine.Core.toString())
            }
        }
        val androidMain by getting{
            dependencies {
                api(Deps.Jetbrains.Kotlinx.Coroutine.Android)
                implementation(Deps.Timber.Timber)
            }
        }
        val iosMain by getting{
            dependencies {
                api(Deps.Jetbrains.Kotlinx.Coroutine.Core.toString()) {
                    version {
                        strictly(Deps.Jetbrains.Kotlinx.Coroutine.version)
                    }
                }
            }
        }
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
