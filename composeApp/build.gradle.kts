plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                cssSupport { enabled.set(true) }
            }
        }
        binaries.executable()
    }


    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)
                implementation("org.jetbrains.compose.html:html-core:1.10.3")
                implementation("org.jetbrains.compose.runtime:runtime:1.10.3")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


