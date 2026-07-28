plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
}

group = "com.floatingcompanion"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jogamp.org/deployment/maven/")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.components.resources)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")
    
    // Global hotkeys
    implementation("com.github.kwhat:jnativehook:2.2.2")
    
    // WebView
    implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40-alpha04")
    
    // Settings persistence
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // JNA
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
}

compose.desktop {
    application {
        mainClass = "com.floatingcompanion.MainKt"
        
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi)
            packageName = "FloatingCompanion"
            packageVersion = "1.0.0"
            description = "A floating AI companion"
            vendor = "FloatingCompanion"
            
            windows {
                menuGroup = "FloatingCompanion"
                shortcut = true
                console = false
            }
        }
    }
}
