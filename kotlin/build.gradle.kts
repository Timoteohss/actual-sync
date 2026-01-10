import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.android.library") version "8.7.3"
    id("app.cash.sqldelight") version "2.0.2"
}

group = "com.actualbudget"
version = "0.1.0-SNAPSHOT"

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
        publishLibraryVariants("release", "debug")
    }

    // iOS targets with XCFramework support
    val xcf = XCFramework("ActualSync")
    // Only build arm64 for faster iteration (real device only)
    listOf(
        // iosX64(),  // Simulator Intel - disabled for faster builds
        iosArm64()
        // iosSimulatorArm64()  // Simulator Apple Silicon - disabled for faster builds
    ).forEach {
        it.binaries.framework {
            baseName = "ActualSync"
            isStatic = true
            xcf.add(this)
        }
    }

    // JVM target (for desktop)
    jvm("desktop") {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

            // Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

            // Date/Time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

            // HTTP Client
            implementation("io.ktor:ktor-client-core:3.0.3")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")

            // UUID
            implementation("com.benasher44:uuid:0.8.4")

            // SQLDelight
            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:3.0.3")
            implementation("app.cash.sqldelight:android-driver:2.0.2")
        }

        // val iosX64Main by getting  // Disabled for faster builds
        val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting  // Disabled for faster builds
        val iosMain by creating {
            dependsOn(commonMain.get())
            // iosX64Main.dependsOn(this)  // Disabled
            iosArm64Main.dependsOn(this)
            // iosSimulatorArm64Main.dependsOn(this)  // Disabled
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.3")
                implementation("app.cash.sqldelight:native-driver:2.0.2")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:3.0.3")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
            }
        }
    }
}

android {
    namespace = "com.actualbudget.sync"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// SQLDelight configuration
sqldelight {
    databases {
        create("ActualDatabase") {
            packageName.set("com.actualbudget.sync.db")
        }
    }
}

// Task to run the demo application
tasks.register<JavaExec>("runTest") {
    group = "application"
    description = "Run sync demo against an Actual Budget server"
    mainClass.set("com.actualbudget.sync.MainKt")

    val desktopTarget = kotlin.targets.getByName("desktop") as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
    val mainCompilation = desktopTarget.compilations.getByName("main")
    classpath = mainCompilation.runtimeDependencyFiles!! + mainCompilation.output.allOutputs

    // Pass server URL and password as system properties
    // Usage: ./gradlew runTest -PserverUrl="http://your-server:5006" -Ppassword="your-password"
    systemProperty("serverUrl", project.findProperty("serverUrl")?.toString() ?: "")
    systemProperty("password", project.findProperty("password")?.toString() ?: "")

    dependsOn("desktopMainClasses")
}
