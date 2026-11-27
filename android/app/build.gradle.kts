plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
    id("app.cash.paparazzi")
}

android {
    namespace = "io.github.sherazi5110.cloudstream"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.sherazi5110.cloudstream"
        minSdk = 23
        targetSdk = 36

        val tagVersion = (findProperty("appVersion") as String?)
            ?.removePrefix("v")
            ?.removePrefix("V")

        versionName = tagVersion ?: "10.0.1"

        versionCode = tagVersion?.let { v ->
            val (major, minor, patch) =
                (v.substringBefore('-').split('.').map {
                    it.toIntOrNull() ?: 0
                } + listOf(0, 0, 0)).take(3)

            major * 10000 + minor * 100 + patch
        } ?: 100000

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        fun quoted(name: String) =
            "\"" + (System.getenv(name) ?: "").replace("\"", "") + "\""

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            quoted("GOOGLE_CLIENT_ID")
        )

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_SECRET",
            quoted("GOOGLE_CLIENT_SECRET")
        )

        buildConfigField(
            "String",
            "WETRAKR_CLIENT_ID",
            quoted("WETRAKR_CLIENT_ID")
        )
    }

    val keystore = System.getenv("CS_KEYSTORE")
        ?.let { file(it) }
        ?.takeIf { it.isFile }

    signingConfigs {
        if (keystore != null) create("release") {
            storeFile = keystore
            storePassword = System.getenv("CS_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("CS_KEY_ALIAS")
            keyPassword = System.getenv("CS_KEY_PASSWORD")
                ?: System.getenv("CS_KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            signingConfig =
                signingConfigs.findByName("release")
                    ?: signingConfigs.getByName("debug")
        }
    }

    // Java 17 compatibility
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/*.kotlin_module",
            "META-INF/versions/**",
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE*",
            "META-INF/NOTICE*",
            "META-INF/INDEX.LIST"
        )

        jniLibs.useLegacyPackaging = true

        jniLibs.pickFirsts += setOf(
            "**/libc++_shared.so"
        )
    }

    splits {
        abi {
            isEnable = true
            reset()

            include(
                "arm64-v8a",
                "armeabi-v7a",
                "x86_64",
                "x86"
            )

            isUniversalApk = true
        }
    }
}

// Kotlin JVM 17 compatibility
kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        )

        optIn.addAll(
            "com.lagradost.cloudstream3.InternalAPI",
            "com.lagradost.cloudstream3.Prerelease"
        )
    }
}

dependencies {

    implementation("org.apache.commons:commons-compress:1.27.1")

    implementation(project(":cs-library"))

    coreLibraryDesugaring(
        "com.android.tools:desugar_jdk_libs_nio:2.1.5"
    )

    implementation(
        platform("androidx.compose:compose-bom:2026.03.00")
    )

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.core:core-ktx:1.18.0")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.9.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.8")

    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.11.2")

    implementation("com.github.albfernandez:juniversalchardet:2.5.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("me.xdrop:fuzzywuzzy:1.4.0")

    implementation("org.conscrypt:conscrypt-android") {
        version {
            strictly("2.5.2")
        }
    }

    implementation("app.cash.zipline:zipline-android:1.27.0")

    // Posters and backdrops
    implementation("dev.chrisbanes.haze:haze:1.6.10")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // VLC Player
    implementation("org.videolan.android:libvlc-all:3.6.5")

    // Media3
    val media3 = "1.11.1"

    implementation("androidx.media3:media3-exoplayer:$media3")
    implementation("androidx.media3:media3-exoplayer-hls:$media3")
    implementation("androidx.media3:media3-exoplayer-dash:$media3")
    implementation("androidx.media3:media3-datasource-okhttp:$media3")
    implementation("androidx.media3:media3-ui:$media3")

    // Torrent streaming
    val libtorrent = "2.1.0-39"

    implementation("org.libtorrent4j:libtorrent4j:$libtorrent")
    implementation("org.libtorrent4j:libtorrent4j-android-arm64:$libtorrent")
    implementation("org.libtorrent4j:libtorrent4j-android-arm:$libtorrent")
    implementation("org.libtorrent4j:libtorrent4j-android-x86:$libtorrent")
    implementation("org.libtorrent4j:libtorrent4j-android-x86_64:$libtorrent")

    testImplementation("org.libtorrent4j:libtorrent4j-linux:$libtorrent")

    // NewPipe Extractor
    implementation(
        "com.github.teamnewpipe:NewPipeExtractor:v0.27.1"
    )

    // QR Codes
    implementation("com.google.zxing:core:3.5.3")

    // Tests
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")

    testImplementation("junit:junit:4.13.2")
}

// Paparazzi reports
tasks.withType<Test>().configureEach {
    reports.html.required.set(false)
}

// Torrent tests
afterEvaluate {

    val unit = tasks.named<Test>("testDebugUnitTest")

    unit.configure {
        filter {
            excludeTestsMatching("*TorrentStreamingTest")
            isFailOnNoMatchingTests = false
        }
    }

    val torrent = tasks.register<Test>("torrentUnitTest") {

        val base = unit.get()

        testClassesDirs = base.testClassesDirs
        classpath = base.classpath

        jvmArgs(
            base.allJvmArgs.filter {
                it.startsWith("--add-opens") ||
                    it.startsWith("-D")
            }
        )

        filter {
            includeTestsMatching("*TorrentStreamingTest")
            isFailOnNoMatchingTests = false
        }

        dependsOn(base.dependsOn)
    }

    unit.configure {
        finalizedBy(torrent)
    }
}
