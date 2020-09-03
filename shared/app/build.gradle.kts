plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-android-extensions")
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
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
                baseName = "RadioRecord"
                export(project(":shared:mvi"))
                export(project(":shared:core"))
                transitiveExport = true
            }
        }
        compilations.getByName("main").apply {
            val observer by cinterops.creating {
                val file = project.file("src/nativeInterop/cinterop/observer.def")
                defFile(file)
                packageName("c.observer")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared:core"))
                api(project(":shared:mvi"))

                // SERIALIZATION
                implementation(Deps.Jetbrains.Kotlinx.Serialization.Core)

                // KTOR
                implementation(Deps.Ktor.Core)
                implementation(Deps.Ktor.Json)
                implementation(Deps.Ktor.Serialization)
                implementation(Deps.Ktor.Logging)

                api(Deps.Kodein.Di)
            }
        }
        val androidMain by getting {
            dependencies {
                // KTOR
                implementation(Deps.Ktor.Jvm.Okhttp)

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

        val iosMain by getting {
            dependencies {
                // KTOR
                implementation(Deps.Ktor.Native.Ios)
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

////https://youtrack.jetbrains.com/issue/KT-27170
configurations.create("compileClasspath")
//

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "io.radio.record"
                artifactId = "shared"
                version = "1.0"
                artifact("$buildDir/outputs/aar/app-debug.aar")
            }
        }
    }
}

//val packForXcode by tasks.creating(Sync::class) {
//    group = "build"
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
//    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
//    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
//    val framework = kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>(targetName).binaries.getFramework(mode)
//    inputs.property("mode", mode)
//    dependsOn(framework.linkTask)
//    val targetDir = File(buildDir, "xcode-frameworks")
//    from({ framework.outputDirectory })
//    into(targetDir)
//}
//tasks.getByName("build").dependsOn(packForXcode)