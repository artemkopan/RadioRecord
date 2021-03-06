plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.Plugin.Gradle)
    implementation(Deps.Jetbrains.Kotlin.Plugin.Serialization)
    implementation(Deps.Jetbrains.Kotlin.StdLib)
    implementation(Deps.Jetbrains.Kotlin.Reflect)
    implementation(Deps.Android.Tools.Build.Gradle)
}

kotlin {
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}