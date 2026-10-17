Skip to content
srrakib985-collab
Calculator
Repository navigation
Code
Issues
Pull requests
Actions
Projects
Wiki
Security and quality
Insights
Settings
Commit a49c41e
srrakib985-collab
srrakib985-collab
committed
3 weeks ago
build: initialize Android project structure
Set up base project configuration, including Gradle build files, ProGuard rules, manifest, and theme color constants for the Dark Mode Calculator.
main
1 parent 
f8d1830
 commit 
a49c41e
46 files changed

+1,546
-11
Lines changed: 1546 additions & 11 deletions
File tree
Filter files…
.env.example
.gitignore
README.md
app
.gitignore
build.gradle.kts
proguard-rules.pro
src
androidTest/java/com/example
ExampleInstrumentedTest.kt
main
AndroidManifest.xml
java/com/example
CalculatorEngine.kt
CalculatorScreen.kt
CalculatorViewModel.kt
MainActivity.kt
ui/theme
Color.kt
Theme.kt
Type.kt
res
drawable
ic_calc_logo.jpg
ic_launcher_background.xml
ic_launcher_foreground.xml
mipmap-anydpi-v26
ic_launcher.xml
ic_launcher_round.xml
mipmap-hdpi
ic_launcher.webp
ic_launcher_round.webp
mipmap-mdpi
ic_launcher.webp
ic_launcher_round.webp
mipmap-xhdpi
ic_launcher.webp
ic_launcher_round.webp
mipmap-xxhdpi
ic_launcher.webp
ic_launcher_round.webp
mipmap-xxxhdpi
ic_launcher.webp
ic_launcher_round.webp
values
colors.xml
strings.xml
themes.xml
xml
backup_rules.xml
data_extraction_rules.xml
test
java/com/example
CalculatorTest.kt
ExampleRobolectricTest.kt
ExampleUnitTest.kt
GreetingScreenshotTest.kt
screenshots
greeting.png
build.gradle.kts
gradle.properties
gradle
libs.versions.toml
wrapper
gradle-wrapper.properties
metadata.json
settings.gradle.kts
Some content is hidden
Large Commits have some content hidden by default. Use the searchbox below for content that may be hidden.
Search within code
 
Customizable line height
The default line height has been increased for improved accessibility. You can choose to enable a more compact line height from the view settings menu.

‎.env.example‎
+7
Lines changed: 7 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,7 @@
# GEMINI_API_KEY: Required for Gemini AI API calls.
# This is a placeholder key.
# AI Studio automatically injects this at runtime from user secrets.
# Users configure this via the Secrets panel in the AI Studio UI.
# IMPORTANT: Uncomment the line below if your app uses the Gemini API.
# If left commented out, the key will NOT be packaged in the APK.
# GEMINI_API_KEY=MY_GEMINI_API_KEY
‎.gitignore‎
+18
Lines changed: 18 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,18 @@
*.iml
.gradle
.kotlin
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
/.idea/navEditor.xml
/.idea/assetWizardSettings.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
.env
debug.keystore
‎README.md‎
-11
Lines changed: 0 additions & 11 deletions
This file was deleted.
‎app/.gitignore‎
+1
Lines changed: 1 addition & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1 @@
/build
‎app/build.gradle.kts‎
+140
Lines changed: 140 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,140 @@
import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}
android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }
  defaultConfig {
    applicationId = "com.aistudio.calculator.dvrp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }
  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}
// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}
googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }
// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)
  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
‎app/proguard-rules.pro‎
+21
Lines changed: 21 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,21 @@
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
‎app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt‎
+22
Lines changed: 22 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,22 @@
package com.example
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals(BuildConfig.APPLICATION_ID, appContext.packageName)
  }
}
‎app/src/main/AndroidManifest.xml‎
+27
Lines changed: 27 additions & 0 deletions
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,27 @@
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
0 commit comments
Comments
0
 (0)
Comment
You're not receiving notifications from this thread.

9 files remain
