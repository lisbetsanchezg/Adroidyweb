package com.beautyshop.movil

import android.app.Application

/** Clase Application: guarda el [AppContainer] para que los ViewModels reciban sus dependencias. */
class BeautyShopApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
