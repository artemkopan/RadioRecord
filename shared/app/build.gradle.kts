plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-android-extensions")
}

setupMultiplatform()
setupAppBinaries("RadioRecord", project(":shared:mvi"), project(":shared:core"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":shared:core"))
                api(project(":shared:mvi"))

                // SERIALIZATION
                implementation(Deps.Jetbrains.Kotlinx.Serialization.RuntimeCommon)

                // KTOR
                implementation(Deps.Ktor.Core)
                implementation(Deps.Ktor.Json)
                implementation(Deps.Ktor.Serialization)
                implementation(Deps.Ktor.Logging)

                //KOIN
                api(Deps.Koin.Core)
            }
        }
        androidMain {
            dependencies {

                // SERIALIZATION
                implementation(Deps.Jetbrains.Kotlinx.Serialization.Runtime)

                // KTOR
                implementation(Deps.Ktor.Jvm.Okhttp)
                implementation(Deps.Ktor.Jvm.Json)
                implementation(Deps.Ktor.Jvm.Serialization)
                implementation(Deps.Ktor.Jvm.Logging)

                // DI
                api(Deps.Koin.Android)
                api(Deps.Koin.AndroidViewModel)

                // EXO PLAYER
                api(Deps.Google.Android.ExoPlayer.Core)
                api(Deps.Google.Android.ExoPlayer.Hls)
                api(Deps.Google.Android.ExoPlayer.Ui)

                //ANDROIDX
                implementation(Deps.AndroidX.Core.CoreKtx)
                implementation(Deps.AndroidX.Palette.Palette)

                //GLIDE
                implementation(Deps.Glide.Glide)
            }
        }

        iosMain {
            dependencies {
                // COROUTINE
                implementation(Deps.Jetbrains.Kotlinx.Coroutine.Native.Core)
                // SERIALIZATION
                implementation(Deps.Jetbrains.Kotlinx.Serialization.RuntimeNative)
                // KTOR
                implementation(Deps.Ktor.Native.Ios)
                implementation(Deps.Ktor.Native.Core)
                implementation(Deps.Ktor.Native.Json)
                implementation(Deps.Ktor.Native.Serialization)
                implementation(Deps.Ktor.Native.Logging)
            }
        }
    }
}


////https://youtrack.jetbrains.com/issue/KT-27170
//configurations.create("compileClasspath")
//

//val packForXcode by tasks.creating(Sync::class) {
//    group = "build"
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
//    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)
//    inputs.property("mode", mode)
//    dependsOn(framework.linkTask)
//    val targetDir = File(buildDir, "xcode-frameworks")
//    from({ framework.outputDirectory })
//    into(targetDir)
//}
//tasks.getByName("build").dependsOn(packForXcode)
