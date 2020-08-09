plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-android-extensions")
}

setupMultiplatform()
setupAppBinaries("Core")

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Nappier.Nappier)
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.LifeCycle.ViewmodelSavedstate)
                implementation(Deps.Nappier.NappierAndroid)
            }
        }

        iosMain {
            dependencies {
                implementation(Deps.Nappier.NappierIos)
            }
        }
    }

}