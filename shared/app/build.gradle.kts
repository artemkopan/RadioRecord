import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
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
                implementation(Deps.Jetbrains.Kotlinx.Serialization.Core)

                // KTOR
                implementation(Deps.Ktor.Core)
                implementation(Deps.Ktor.Json)
                implementation(Deps.Ktor.Serialization)
                implementation(Deps.Ktor.Logging)

                api(Deps.Kodein.Di)
            }
        }
        androidMain {
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

        iosMain {
            dependencies {

                // KTOR
                implementation(Deps.Ktor.Native.Ios)

            }
        }
    }
}


////https://youtrack.jetbrains.com/issue/KT-27170
configurations.create("compileClasspath")
//

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.radio.record"
            artifactId = "shared"
            version = "1.0"
        }
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"

    val framework = kotlin{}.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)