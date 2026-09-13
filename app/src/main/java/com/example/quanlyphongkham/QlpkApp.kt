package com.example.quanlyphongkham

import android.app.Application
import com.example.quanlyphongkham.di.AppContainer

class QlpkApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
