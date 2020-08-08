// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

}

apply {
    from("$rootDir/configuration.gradle.kts")
}

buildscript {
    val kotlin_version by extra { "1.3.72" }
    val koin_version by extra { "3.0.0-alpha-2" }

    val coroutine_version by extra { "1.3.8" }
    val serializer_version by extra { "0.20.0" }
    val ktor_version by extra { "1.3.1" }

    val android_nav_version by extra { "2.3.0" }
    val android_lifecycle_version by extra { "2.2.0" }
    val android_palette_version by extra { "1.0.0" }
    val android_fragment_version by extra { "1.3.0-alpha02" }
    val android_constraint_version by extra { "2.0.0-beta8" }
    val android_recycler_version by extra { "1.2.0-alpha04" }
    val android_material_version by extra { "1.3.0-alpha02" }
    val android_exo_player by extra { "2.11.1" }
    val android_work_version by extra { "2.3.4" }

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
//        classpath("org.koin:koin-gradle-plugin:$koin_version")
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$android_nav_version")
        classpath("com.google.gms:google-services:4.3.3")
    }
}


allprojects {
    repositories {
        google()
        jcenter()
        maven { setUrl("https://dl.bintray.com/ekito/koin") }
    }
}