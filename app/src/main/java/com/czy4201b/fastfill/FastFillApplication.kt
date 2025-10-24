package com.czy4201b.fastfill

import android.app.Application

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
