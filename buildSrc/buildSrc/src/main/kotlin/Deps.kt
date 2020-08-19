object Deps {

    object Jetbrains {
        object Kotlin : Group(name = "org.jetbrains.kotlin") {
            private const val version = "1.4.0"

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
                private const val version = "1.0.0-RC"

                object Core : Dependency(
                    group = Kotlinx,
                    name = "kotlinx-serialization-core",
                    version = version
                )
            }

            object Coroutine {
                private const val version = "1.3.9"

                object Common {
                    object Core : Dependency(
                        group = Kotlinx,
                        name = "kotlinx-coroutines-core",
                        version = version
                    )
                }

                object Android : Dependency(
                    group = Kotlinx,
                    name = "kotlinx-coroutines-android",
                    version = version
                )
            }
        }
    }

    object Ktor : Group(name = "io.ktor") {
        private const val version = "1.4.0"

        object Core : Dependency(Ktor, name = "ktor-client-core", version = version)
        object Json : Dependency(Ktor, name = "ktor-client-json", version = version)
        object Serialization :
            Dependency(Ktor, name = "ktor-client-serialization", version = version)

        object Logging : Dependency(Ktor, name = "ktor-client-logging", version = version)

        object Jvm {
            object Okhttp : Dependency(Ktor, name = "ktor-client-okhttp", version = version)
            object Json : Dependency(Ktor, name = "ktor-client-json-jvm", version = version)
            object Serialization :
                Dependency(Ktor, name = "ktor-client-serialization-jvm", version = version)

            object Logging : Dependency(Ktor, name = "ktor-client-logging-jvm", version = version)
        }

        object Native {
            object Ios : Dependency(Ktor, name = "ktor-client-ios", version = version)
            object Core : Dependency(Ktor, name = "ktor-client-core-native", version = version)
            object Json : Dependency(Ktor, name = "ktor-client-json-native", version = version)
            object Serialization :
                Dependency(Ktor, name = "ktor-client-serialization-native", version = version)

            object Logging :
                Dependency(Ktor, name = "ktor-client-logging-native", version = version)
        }
    }

    object Koin : Group(name = "org.koin") {
        private const val version = "3.0.0-alpha-2"

        object Core : Dependency(Koin, name = "koin-core", version = version)

        object Android : Dependency(Koin, name = "koin-android", version = version)
        object AndroidScope : Dependency(Koin, name = "koin-android-scope", version = version)
        object AndroidViewModel :
            Dependency(Koin, name = "koin-android-viewmodel", version = version)
    }

    object Android {
        object Tools {
            object Build : Group(name = "com.android.tools.build") {
                object Gradle : Dependency(group = Build, name = "gradle", version = "4.2.0-alpha07")
            }
        }
    }

    object AndroidX {

        object Core : Group(name = "androidx.core") {
            object CoreKtx : Dependency(group = Core, name = "core-ktx", version = "1.5.0-alpha01")
        }

        object AppCompat : Group(name = "androidx.appcompat") {
            object AppCompat :
                Dependency(
                    group = AndroidX.AppCompat,
                    name = "appcompat",
                    version = "1.3.0-alpha01"
                )
        }

        object RecyclerView : Group(name = "androidx.recyclerview") {
            object RecyclerView :
                Dependency(
                    group = AndroidX.RecyclerView,
                    name = "recyclerview",
                    version = "1.2.0-alpha05"
                )
        }

        object ConstraintLayout : Group(name = "androidx.constraintlayout") {
            object ConstraintLayout : Dependency(
                group = AndroidX.ConstraintLayout,
                name = "constraintlayout",
                version = "2.0.0-rc1"
            )
        }

        object Palette : Group(name = "androidx.palette") {
            object Palette :
                Dependency(group = AndroidX.Palette, name = "palette", version = "1.0.0")
        }

        object Fragment : Group(name = "androidx.fragment") {
            object Ktx : Dependency(
                group = AndroidX.Fragment,
                name = "fragment-ktx",
                version = "1.3.0-alpha07"
            )
        }

        object LifeCycle : Group(name = "androidx.lifecycle") {
            private const val version = "2.3.0-alpha06"

            object RuntimeKtx :
                Dependency(group = LifeCycle, name = "lifecycle-runtime-ktx", version = version)

            object Viewmodel :
                Dependency(group = LifeCycle, name = "lifecycle-viewmodel", version = version)

            object CommonJava8 :
                Dependency(group = LifeCycle, name = "lifecycle-common-java8", version = version)

            object ViewmodelSavedstate : Dependency(
                group = LifeCycle,
                name = "lifecycle-viewmodel-savedstate",
                version = version
            )
        }

        object SwypeRefreshLayout : Group(name = "androidx.swiperefreshlayout") {
            object SwypeRefreshLayout : Dependency(
                group = AndroidX.SwypeRefreshLayout,
                name = "swiperefreshlayout",
                version = "1.2.0-alpha01"
            )
        }

        object Navigation : Group(name = "androidx.navigation") {
            private const val version = "2.3.0"

            object FragmentKtx : Dependency(
                group = AndroidX.Navigation,
                name = "navigation-fragment-ktx",
                version = version
            )

            object Plugin {
                object SafeArgs : Dependency(
                    group = AndroidX.Navigation,
                    name = "navigation-safe-args-gradle-plugin",
                    version = version
                )
            }
        }

        object ViewPager2 : Group(name = "androidx.viewpager2") {
            object ViewPager2 : Dependency(
                group = AndroidX.ViewPager2,
                name = "viewpager2",
                version = "1.1.0-alpha01"
            )
        }

        object Compose : Group(name = "androidx.compose") {
            const val version = "0.1.0-dev16"

            object ComposeCompiler : Dependency(group = Compose, name = "compose-compiler", version = version)

            object Runtime : Group(name = Compose.name + ".runtime") {
                object Runtime : Dependency(group = Compose.Runtime, name = "runtime", version = version)
                object SavedInstanceState : Dependency(group = Compose.Runtime, name = "runtime-saved-instance-state", version = version)
            }

            object Ui : Group(name = Compose.name + ".ui") {
                object Ui : Dependency(group = Compose.Ui, name = "ui", version = version)
            }

            object Foundation : Group(name = Compose.name + ".foundation") {
                object Foundation : Dependency(group = Compose.Foundation, name = "foundation", version = version)
                object FoundationLayout : Dependency(group = Compose.Foundation, name = "foundation-layout", version = version)

            }

            object Material : Group(name = Compose.name + ".material") {
                object Material : Dependency(group = Compose.Material, name = "material", version = version)
            }

        }

        object Ui : Group(name = "androidx.ui") {
            object Tooling : Dependency(group = Ui, name = "ui-tooling", version = Compose.version)
        }
    }

    object Google {
        object Android {
            object Material : Group(name = "com.google.android.material") {
                object Material :
                    Dependency(group = Android.Material, name = "material", version = "1.2.0")
            }

            object ExoPlayer : Group("com.google.android.exoplayer") {
                private const val version = "2.11.7"

                object Core :
                    Dependency(group = ExoPlayer, name = "exoplayer-core", version = version)

                object Hls :
                    Dependency(group = ExoPlayer, name = "exoplayer-hls", version = version)

                object Ui : Dependency(group = ExoPlayer, name = "exoplayer-ui", version = version)
            }
        }

        object Gms : Group(name = "com.google.gms") {
            object Services : Dependency(group = Gms, name = "google-services", version = "4.3.3")
        }
    }

    object Glide : Group(name = "com.github.bumptech.glide") {
        object Glide : Dependency(group = Deps.Glide, name = "glide", version = "4.11.0")
    }

    object Timber : Group(name = "com.jakewharton.timber") {
        object Timber : Dependency(group = Deps.Timber, name = "timber", version = "4.7.1")
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