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