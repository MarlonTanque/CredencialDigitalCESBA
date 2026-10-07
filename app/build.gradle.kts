plugins {
    alias(libs.plugins.android.application)
}

val webUiDir = layout.projectDirectory.dir("src/main/web").asFile
val buildWebUi by tasks.registering(Exec::class) {
    workingDir(webUiDir)
    if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
        commandLine("cmd", "/c", "npm.cmd", "run", "build")
    } else {
        commandLine("npm", "run", "build")
    }
    inputs.files(fileTree(webUiDir) { exclude("node_modules/**") })
    outputs.dir(layout.projectDirectory.dir("src/main/assets"))
}
tasks.named("preBuild").configure { dependsOn(buildWebUi) }

android {
    namespace = "com.example.credencialdigitalcesba"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.credencialdigitalcesba"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation("androidx.webkit:webkit:1.13.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
