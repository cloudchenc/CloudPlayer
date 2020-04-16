package com.chen.cloudplayer

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class CloudApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Initialize Logger
        Logger.addLogAdapter(AndroidLogAdapter())
    }

}