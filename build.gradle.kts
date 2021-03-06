import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Deps.Jetbrains.Kotlin.Plugin.Gradle)
        classpath(Deps.Jetbrains.Kotlin.Plugin.Serialization)
        classpath(Deps.Android.Tools.Build.Gradle)
        classpath(Deps.AndroidX.Navigation.Plugin.SafeArgs)
        classpath(Deps.Google.Gms.Services)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/ekito/koin")
        maven("https://dl.bintray.com/kodein-framework/kodein-dev")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(
                    "-Xallow-jvm-ir-dependencies",
                    "-Xskip-prerelease-check",
                    "-XXLanguage:+InlineClasses",
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xuse-experimental=kotlin.time.ExperimentalTime",
                    "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi"
                )
            }
        }
    }
}