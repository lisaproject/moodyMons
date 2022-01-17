package com.example.moodymons

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Ref: https://github.com/iDeMonnnnnn/DeMon_Sound.git
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }
}