plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

android {
    namespace = "com.capivv.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "SDK_VERSION", "\"${project.findProperty("VERSION_NAME") ?: "1.0.0"}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Ktor HTTP Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Google Play Billing
    implementation(libs.billing)
    implementation(libs.billing.ktx)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // Security & Storage
    implementation(libs.security.crypto)
    implementation(libs.datastore.preferences)

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.findProperty("GROUP") as String? ?: "com.capivv"
            artifactId = "capivv-sdk"
            version = project.findProperty("VERSION_NAME") as String? ?: "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set(project.findProperty("POM_NAME") as String? ?: "Capivv Android SDK")
                description.set(project.findProperty("POM_DESCRIPTION") as String?)
                url.set(project.findProperty("POM_URL") as String?)

                licenses {
                    license {
                        name.set(project.findProperty("POM_LICENSE_NAME") as String?)
                        url.set(project.findProperty("POM_LICENSE_URL") as String?)
                    }
                }

                developers {
                    developer {
                        id.set(project.findProperty("POM_DEVELOPER_ID") as String?)
                        name.set(project.findProperty("POM_DEVELOPER_NAME") as String?)
                    }
                }

                scm {
                    url.set(project.findProperty("POM_SCM_URL") as String?)
                    connection.set(project.findProperty("POM_SCM_CONNECTION") as String?)
                    developerConnection.set(project.findProperty("POM_SCM_DEV_CONNECTION") as String?)
                }
            }
        }
    }
}
