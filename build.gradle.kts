// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies{
        //classpath(libs.dagger.hilt.android.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt.android.plugin) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.appdistribution) apply  false
}