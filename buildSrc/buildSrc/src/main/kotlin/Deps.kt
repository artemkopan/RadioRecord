object Deps {

    object Jetbrains {
        object Kotlin : Group(name = "org.jetbrains.kotlin") {
            private const val version = "1.3.72"

            object Plugin {
                object Gradle :
                    Dependency(group = Kotlin, name = "kotlin-gradle-plugin", version = version)

                object Serialization :
                    Dependency(group = Kotlin, name = "kotlin-serialization", version = version)
            }

            object StdLib {
                object Common :
                    Dependency(group = Kotlin, name = "kotlin-stdlib-common", version = version)

                object Jdk7 :
                    Dependency(group = Kotlin, name = "kotlin-stdlib-jdk7", version = version)
            }

            object Test {
                object Common :
                    Dependency(group = Kotlin, name = "kotlin-test-common", version = version)

                object Junit :
                    Dependency(group = Kotlin, name = "kotlin-test-junit", version = version)
            }

            object TestAnnotations {
                object Common : Dependency(
                    group = Kotlin,
                    name = "kotlin-test-annotations-common",
                    version = version
                )
            }
        }

        object Kotlinx : Group(name = "org.jetbrains.kotlinx") {
            object Serialization {
                private const val version = "0.20.0"

                object RuntimeCommon : Dependency(group = Kotlinx, name = "kotlinx-serialization-runtime-common", version = version)
                object Runtime : Dependency(group = Kotlinx, name = "kotlinx-serialization-runtime", version = version)
                object RuntimeNative : Dependency(group = Kotlinx, name = "kotlinx-serialization-runtime-native", version = version)
            }
        }
    }

    object Android {
        object Tools {
            object Build : Group(name = "com.android.tools.build") {
                object Gradle : Dependency(group = Build, name = "gradle", version = "3.5.2")
            }
        }
    }

    object AndroidX {
        object Core : Group(name = "androidx.core") {
            object Ktx : Dependency(group = Core, name = "core-ktx", version = "1.1.0")
        }

        object AppCompat : Group(name = "androidx.appcompat") {
            object AppCompat :
                Dependency(group = AndroidX.AppCompat, name = "appcompat", version = "1.1.0")
        }

        object RecyclerView : Group(name = "androidx.recyclerview") {
            object RecyclerView :
                Dependency(group = AndroidX.RecyclerView, name = "recyclerview", version = "1.1.0")
        }

        object ConstraintLayout : Group(name = "androidx.constraintlayout") {
            object ConstraintLayout : Dependency(
                group = AndroidX.ConstraintLayout,
                name = "constraintlayout",
                version = "1.1.3"
            )
        }

        object Fragment : Group(name = "androidx.fragment") {
            object FragmentKtx : Dependency(group = AndroidX.Fragment, name = "fragment-ktx", version = "2.3.0")
        }

        object LifeCycle : Group(name = "androidx.lifecycle") {
            private const val version = "2.2.0"

            object Viewmodel : Dependency(group = LifeCycle, name = "lifecycle-viewmodel", version = version)
            object CommonJava8 : Dependency(group = LifeCycle, name = "lifecycle-common-java8", version = version)
            object ViewmodelSavedstate : Dependency(group = LifeCycle, name = "lifecycle-viewmodel-savedstate", version = version)
        }

        object SwypeRefreshLayout : Group(name = "androidx.swiperefreshlayout") {
            object SwypeRefreshLayout : Dependency(group = AndroidX.SwypeRefreshLayout, name = "swiperefreshlayout", version = "1.0.0")
        }

        object Navigation : Group(name = "androidx.navigation") {
            private const val version = "2.3.0"

            object FragmentKtx : Dependency(group = AndroidX.Navigation, name = "navigation-fragment-ktx", version = version)

            object Plugin {
                object SafeArgs : Dependency(group = AndroidX.Navigation, name = "navigation-safe-args-gradle-plugin", version = version)
            }
        }
    }

    object Google {
        object Android {
            object Material : Group(name = "com.google.android.material") {
                object Material :
                    Dependency(group = Android.Material, name = "material", version = "1.1.0")
            }

            object Gms : Group(name = "com.google.gms") {
                object Services : Dependency(group = Gms, name = "google-services", version = "4.3.3")
            }
        }
    }

    object Glide {
        object Glide : Group(name = "com.github.bumptech.glide") {
            object Picasso : Dependency(group = Glide, name = "glide", version = "4.11.0")
        }
    }


    open class Group(val name: String)

    open class Dependency private constructor(
        private val notation: String
    ) : CharSequence by notation {
        constructor(
            group: Group,
            name: String,
            version: String
        ) : this("${group.name}:$name:$version")

        override fun toString(): String = notation
    }
}