plugins {
    // Memanggil versi 8.4.0 dari libs.versions.toml
    alias(libs.plugins.android.application) apply false

    // Memanggil versi 2.0.0 dari libs.versions.toml
    alias(libs.plugins.kotlin.compose) apply false

    // Ini tetap manual karena belum masuk TOML
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}