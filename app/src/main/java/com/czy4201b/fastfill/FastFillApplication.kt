package com.czy4201b.fastfill

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FastFillApplication : Application() {

    companion object {
        lateinit var instance: FastFillApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
