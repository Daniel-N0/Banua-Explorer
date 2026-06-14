package com.example.banuaexplorer

import android.app.Application
import com.example.banuaexplorer.core.di.AppContainer

class BanuaExplorerApplication : Application() {

    // Variabel penampung container kita
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Nyalakan container saat aplikasi pertama kali dibuka
        container = AppContainer(this)
    }
}