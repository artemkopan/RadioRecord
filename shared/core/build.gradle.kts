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
            }
        }

        iosMain {
            dependencies {
            }
        }

    }

}