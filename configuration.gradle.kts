//import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
//import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
//
//plugins {
//    kotlin("multiplatform")
//}
//
////select iOS target platform depending on the Xcode environment variables
//val KotlinMultiplatformExtension.iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget
//    get() {
//        return if (System.getenv("SDK_NAME")
//                ?.startsWith("iphoneos") == true
//        ) ::iosArm64 else ::iosX64
//    }
//
//fun KotlinMultiplatformExtension.iosTarget(baseName: String) =
//    iosTarget("ios") {
//        binaries {
//            framework {
//                this.baseName = baseName
//            }
//        }
//    }
