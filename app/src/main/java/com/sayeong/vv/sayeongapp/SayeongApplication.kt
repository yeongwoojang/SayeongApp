package com.sayeong.vv.sayeongapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SayeongApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}