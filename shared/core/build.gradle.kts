setupMultiplatform()

kotlin {

    sourceSets {
        commonMain {
            dependencies {
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.LifeCycle.ViewmodelSavedstate)
                implementation(Deps.Timber.Timber)
            }
        }

        iosMain {
            dependencies {
            }
        }

    }

}