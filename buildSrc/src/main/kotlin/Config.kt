@file:Suppress("UNUSED_VARIABLE")

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import java.io.FileNotFoundException

fun Project.setupMultiplatform() {

    plugins.apply("com.android.library")
    plugins.apply("kotlin-multiplatform")
    plugins.apply("kotlin-android-extensions")

    setupAndroidSdkVersions()
    setupAndroidFilesPath()

    kotlin {

        android {
            publishLibraryVariants("debug", "release")
        }

        iosX64("ios")
        iosArm64()

        sourceSets {
            commonMain {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlinx.Coroutine.Core.toString())
                }
            }

            commonTest {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Common)
                    implementation(Deps.Jetbrains.Kotlin.TestAnnotations.Common)
                }
            }

            androidMain {
                dependsOn(commonMain())

                dependencies {
                    implementation(Deps.Jetbrains.Kotlinx.Coroutine.Android)
                }
            }

            androidTest {
                dependsOn(commonTest())

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Junit)
                }
            }

            iosMain {
                dependsOn(commonMain())
                dependencies {
                    implementation(Deps.Jetbrains.Kotlinx.Coroutine.Core.toString()) {
                        version {
                            strictly(Deps.Jetbrains.Kotlinx.Coroutine.version)
                        }
                    }
                }
            }

            iosTest().dependsOn(commonTest())

            iosX64Main().dependsOn(iosMain())
            iosX64Test().dependsOn(iosTest())

            iosArm64Main().dependsOn(iosMain())
            iosArm64Test().dependsOn(iosTest())

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
}

fun Project.setupAndroidSdkVersions() {
    android {
        compileSdkVersion(29)

        defaultConfig {
            targetSdkVersion(29)
            minSdkVersion(21)
        }
    }
}

private fun Project.setupAndroidFilesPath() {
    android {
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        sourceSets["main"].res.srcDirs("src/androidMain/res")
//        sourceSets["main"].java.srcDirs("src/androidMain/kotlin")
    }
}

// Workaround since iosX64() and iosArm64() function are not resolved if used in a module with Kotlin 1.3.70
fun Project.setupAppBinaries(baseName: String, vararg dependencies: Any) {
    fun KotlinNativeTarget.setupIosBinaries() {
        binaries {
            framework {
                this.baseName = baseName
                freeCompilerArgs = freeCompilerArgs.plus("-Xobjc-generics").toMutableList()
                dependencies.forEach { export(it) }
                transitiveExport = true
            }
        }

        compilations["main"].apply {
            val observer by cinterops.creating {
                val file = project.file("src/nativeInterop/cinterop/observer.def")
                if (!file.exists()) {
                    throw FileNotFoundException("file not found: ${file.absolutePath}")
                }
                defFile(file)
                packageName("c.observer")
            }
        }
    }

    kotlin {
        configure(listOf(iosX64(), iosArm64())) {
            setupIosBinaries()
        }
        tasks.create("debugFatFramework", FatFrameworkTask::class.java) {
            this.baseName = baseName
            destinationDir = buildDir.resolve("framework/debug")
            from(
                iosX64().binaries.getFramework("DEBUG"),
                iosArm64().binaries.getFramework("DEBUG")
            )
        }
    }
}


fun Project.android(block: BaseExtension.() -> Unit) {
    extensions.getByType<BaseExtension>().block()
}

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType<KotlinMultiplatformExtension>().block()
}

typealias SourceSets = NamedDomainObjectContainer<KotlinSourceSet>

fun KotlinMultiplatformExtension.sourceSets(block: SourceSets.() -> Unit) {
    sourceSets.block()
}

private fun SourceSets.getOrCreate(name: String): KotlinSourceSet = findByName(name) ?: create(name)

// common

fun SourceSets.commonMain(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("commonMain").apply(block)

fun SourceSets.commonTest(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("commonTest").apply(block)

// android

fun SourceSets.androidMain(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("androidMain").apply(block)

fun SourceSets.androidTest(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("androidTest").apply(block)

// iosCommon

fun SourceSets.iosMain(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosMain").apply(block)

fun SourceSets.iosTest(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosTest").apply(block)

// iosX64

fun SourceSets.iosX64Main(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosX64Main").apply(block)

fun SourceSets.iosX64Test(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosX64Test").apply(block)

// iosArm64

fun SourceSets.iosArm64Main(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosArm64Main").apply(block)

fun SourceSets.iosArm64Test(block: KotlinSourceSet.() -> Unit = {}): KotlinSourceSet =
    getOrCreate("iosArm64Test").apply(block)