import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android.plugin)
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.appdistribution)
}

ext {
    set("majorVersion", 1)
    set("minorVersion", 0)
    set("patchVersion", 0)
    set("developmentVersion", 1)
    set("preReleaseVersion", null)
}

android {
    namespace = "io.github.reskimulud.myloginapps"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.reskimulud.myloginapps"
        minSdk = 24
        targetSdk = 34
        versionCode = generateVersionCode()
        versionName = generateVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        all {
            storeFile = file("$rootDir/market/myloginapps.jks")
            try {
                val fileLocalProperties = file("$rootDir/local.properties")

                if (fileLocalProperties.exists()) {
                    val localProperties = readProperties(fileLocalProperties)
                    storePassword = localProperties["storePassword"] as String
                    keyAlias = localProperties["keyAlias"] as String
                    keyPassword = localProperties["keyPassword"] as String
                } else {
                    throw NoSuchFileException(fileLocalProperties)
                }
            } catch (e: NoSuchFileException) {
                storePassword = System.getenv("STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
        all {
            firebaseAppDistribution {
                testers = "reski.mulud@gmail.com"
                artifactType = "APK"
                serviceCredentialsFile = try {
                    val fileLocalProperties = file("$rootDir/local.properties")

                    if (fileLocalProperties.exists()) {
                        val localProperties = readProperties(fileLocalProperties)
                        localProperties["serviceCredentialFilePath"] as String
                    } else {
                        throw NoSuchFileException(fileLocalProperties)
                    }
                } catch (e: NoSuchFileException) {
                    System.getenv("SERVICE_CREDENTIAL")
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    setFlavorDimensions(listOf("env"))
    productFlavors {
        create("development") {
            dimension = "env"
            buildConfigField("int", "STAGE_TYPE", "0")
            applicationIdSuffix = ".dev"
            firebaseAppDistribution {
                releaseNotes = "My Login Apps (Debug) v${defaultConfig.versionName}"
            }
        }
        create("staging") {
            dimension = "env"
            buildConfigField("int", "STAGE_TYPE", "1")
            applicationIdSuffix = ".stag"
            firebaseAppDistribution {
                releaseNotes = "My Login Apps (Debug) v${defaultConfig.versionName}"
            }
        }
        create("production") {
            dimension = "env"
            buildConfigField("int", "STAGE_TYPE", "2")
            firebaseAppDistribution {
                releaseNotes = "My Login Apps v${defaultConfig.versionName}"
            }
        }
    }

    applicationVariants.configureEach {
        buildOutputs.all {
            val variantOutputImpl = this as BaseVariantOutputImpl
            variantOutputImpl.outputFileName = "MyLoginApp_${variantOutputImpl.name}_${versionName}_${getCurrentDate("dd.MM.YYYY")}.apk"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}

tasks {
    register("assembleDevelopmentAndUpload") {
        group = "Custom"
        description = "Assemble development debug (build APK) and upload it to App Distribution"

        dependsOn("assembleDevelopmentDebug", "appDistributionUploadDevelopmentDebug")
    }

    register("assembleStagingAndUpload") {
        group = "Custom"
        description = "Assemble staging debug (build APK) and upload it to App Distribution"

        dependsOn("assembleStagingDebug", "appDistributionUploadStagingDebug")
    }

    register("assembleProductionAndUpload") {
        group = "Custom"
        description = "Assemble production release (build APK) and upload it to App Distribution"

        dependsOn("assembleProductionRelease", "appDistributionUploadProductionRelease")
    }
}

fun generateVersionCode() : Int {
    return ext.let {
        it.get("majorVersion") as Int * 10000 + it.get("minorVersion") as Int * 100 + it.get("patchVersion") as Int
    }
}

fun generateVersionName() : String {
    return ext.let {
        val versionName = StringBuilder("")
            .append(it.get("majorVersion"))
            .append(".")
            .append(it.get("minorVersion"))
            .append(".")
            .append(it.get("patchVersion"))
        val devVersion: Int = it.get("developmentVersion") as Int
        if (devVersion > 0) versionName.append(".$devVersion")

        val preReleaseVersion = it.get("preReleaseVersion") as String?
        if (!preReleaseVersion.isNullOrEmpty()) versionName.append(".$preReleaseVersion")

        versionName.toString()
    }
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}

fun getCurrentDate(pattern: String): String {
    val date = Date()
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}