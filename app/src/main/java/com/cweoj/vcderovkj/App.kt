package com.cweoj.vcderovkj

import android.app.Application
import android.util.Base64
import android.util.Log
import java.util.TimeZone

/**
 * @ClassName: App
 * @Description: 类作用描述
 * @Author: zhouzuowei
 * @Date: 2023/4/13 17:47
 */
class App : Application() {
    companion object {
        var url = ""
        var gaid = ""
        lateinit var application: Application
    }
    private val baseUrl = "https://xyz.vcderovkj.top/vcd/post"
    private val TAG = "VCDEROVKJ_APPLICATION"
    override fun onCreate() {
        super.onCreate()
        application = this
        val language = this.resources.configuration.locale.language
        val displayName = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate: $displayName")
        val base = "pid=${BuildConfig.APPLICATION_ID}&leg=${language}&ino=$displayName".toByteArray(Charsets.UTF_8)
        val params = Base64.encodeToString(base, Base64.DEFAULT)
        if (BuildConfig.DEBUG)  Log.d(TAG, "onCreate: $params")
        url = "$baseUrl?req=$params"
    }
}