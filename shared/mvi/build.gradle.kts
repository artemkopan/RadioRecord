plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-android-extensions")
}

setupMultiplatform()

kotlin {

    sourceSets {

        commonMain {
            dependencies {
                implementation(project(":shared:core"))
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.LifeCycle.Viewmodel)
                implementation(Deps.AndroidX.LifeCycle.ViewmodelSavedstate)
            }
        }
    }

}