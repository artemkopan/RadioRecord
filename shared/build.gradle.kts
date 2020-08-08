import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-android-extensions")
}

kotlin {

    //select iOS target platform depending on the Xcode environment variables
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64
        else ::iosX64

    fun iosTarget(baseName: String) = iosTarget("ios") {
        binaries {
            framework {
                this.baseName = baseName
            }
        }
    }

    iosTarget("RadioRecord")
    android()

    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib-common")

                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${rootProject.extra["kotlin_version"]}")

                // COROUTINE
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${rootProject.extra["coroutine_version"]}")

                // SERIALIZATION
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${rootProject.extra["serializer_version"]}")

                // KTOR
                implementation("io.ktor:ktor-client-core:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-json:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-serialization:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-logging:${rootProject.extra["ktor_version"]}")

                //KOIN
                implementation("org.koin:koin-core:${rootProject.extra["koin_version"]}")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${rootProject.extra["kotlin_version"]}")

                // COROUTINE
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutine_version"]}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutine_version"]}")

                // SERIALIZATION
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${rootProject.extra["serializer_version"]}")

                // KTOR
                implementation("io.ktor:ktor-client-okhttp:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-json-jvm:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-serialization-jvm:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-logging-jvm:${rootProject.extra["ktor_version"]}")

                // AndroidX
                implementation("androidx.fragment:fragment-ktx:${rootProject.extra["android_fragment_version"]}")
                implementation("androidx.lifecycle:lifecycle-viewmodel:${rootProject.extra["android_lifecycle_version"]}")
                implementation("androidx.lifecycle:lifecycle-common-java8:${rootProject.extra["android_lifecycle_version"]}")
                implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${rootProject.extra["android_lifecycle_version"]}")
                implementation("androidx.recyclerview:recyclerview:${rootProject.extra["android_recycler_version"]}")
                implementation("androidx.constraintlayout:constraintlayout:${rootProject.extra["android_constraint_version"]}")
                implementation("androidx.palette:palette:${rootProject.extra["android_palette_version"]}")
                implementation("com.google.android.material:material:${rootProject.extra["android_material_version"]}")
                implementation("com.github.bumptech.glide:glide:4.11.0") {
                    exclude(group = "com.android.support")
                }

                // DI
                api("org.koin:koin-core:${rootProject.extra["koin_version"]}")
                api("org.koin:koin-android:${rootProject.extra["koin_version"]}")
                api("org.koin:koin-android-viewmodel:${rootProject.extra["koin_version"]}")
                // EXO PLAYER
                api("com.google.android.exoplayer:exoplayer-core:${rootProject.extra["android_exo_player"]}")
                api("com.google.android.exoplayer:exoplayer-hls:${rootProject.extra["android_exo_player"]}")
                api("com.google.android.exoplayer:exoplayer-ui:${rootProject.extra["android_exo_player"]}")
            }
        }

        val iosMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${rootProject.extra["kotlin_version"]}")
                // COROUTINE
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${rootProject.extra["coroutine_version"]}")
                // SERIALIZATION
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${rootProject.extra["serializer_version"]}")
                // KTOR
                implementation("io.ktor:ktor-client-ios:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-core-native:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-json-native:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-serialization-native:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-logging-native:${rootProject.extra["ktor_version"]}")
            }
        }

        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("kotlinx.coroutines.ObsoleteCoroutinesApi")
                useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
                useExperimentalAnnotation("kotlinx.coroutines.InternalCoroutinesApi")
            }
        }
    }
}



//https://youtrack.jetbrains.com/issue/KT-27170
configurations.create("compileClasspath")

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)
