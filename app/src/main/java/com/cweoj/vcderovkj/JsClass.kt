package com.cweoj.vcderovkj

import android.util.Log
import android.webkit.JavascriptInterface
import com.appsflyer.AppsFlyerLib

/**
 * @ClassName: JsClass
 * @Description: 类作用描述
 * @Author: zhouzuowei
 * @Date: 2023/4/13 22:35
 */
class JsClass {
    @JavascriptInterface
    fun getAppsFlyerId(): String {
        val id = AppsFlyerLib.getInstance().getAppsFlyerUID(App.application) ?: ""
        if (BuildConfig.DEBUG) Log.d("MainActivity_TAG", "getAppsFlyerId: $id")
        return id
    }

    @JavascriptInterface
    fun getgaid(): String {
        if (BuildConfig.DEBUG) Log.d("MainActivity_TAG", "getgaid: ${App.gaid}")
        return App.gaid
    }
}