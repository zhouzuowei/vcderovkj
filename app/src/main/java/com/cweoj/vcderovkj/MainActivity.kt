package com.cweoj.vcderovkj

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.jrmzhz.apage.APageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_TAG"
    private val web by lazy {
        findViewById<WebView>(R.id.web)
    }
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //接口请求
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate: onCreate")
        setWebView(web)
//        findViewById<Button>(R.id.bbbbb).setOnClickListener {
        lifecycleScope.launch(Dispatchers.IO) {
            App.gaid = AdvertisingIdClient.getAdvertisingIdInfo(this@MainActivity).id ?: ""
            if (BuildConfig.DEBUG) Log.d(TAG, "gaid: ${App.gaid}")
        }
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (BuildConfig.DEBUG) Log.d(TAG, "url: ${App.url}")
                val url = URL(App.url)
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.connectTimeout = 10000
                val code = con.responseCode
                if (BuildConfig.DEBUG) Log.d(TAG, "onCreate: $code")
                //{"data":{"url":"https://testhfph.com","key":"zsQhU6ZHvmSecoXX3vDzge"}}
                if (code == 200) {
                    val data = BufferedReader(InputStreamReader(con.inputStream))
                    val stringBuffer = StringBuffer()
                    var line: String? = data.readLine()
                    while (!line.isNullOrEmpty()) {
                        stringBuffer.append(line)
                        line = data.readLine()
                    }
                    if (BuildConfig.DEBUG) Log.d(TAG, "stringBuffer: $stringBuffer")
                    val bean = jsonObj(stringBuffer.toString())
                    if (bean?.url.isNullOrEmpty()) {
                        startPage()
                    } else {
                        withContext(Dispatchers.Main) {
                            initApps(bean?.key ?: "")
                            web.loadUrl(bean?.url ?: "")
//                            web.loadUrl("https://direct.lc.chat/13045821/")
//                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(bean?.url?:"")))
                        }
                    }
                } else {
                    startPage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                startPage()
            }
        }
//        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (BuildConfig.DEBUG) Log.d(TAG, "onConfigurationChanged: ${newConfig}")
    }
    private fun initApps(appid: String) {
        if (BuildConfig.DEBUG) Log.d("MainActivity_TAG", "initApps: $appid")
        AppsFlyerLib.getInstance().init(appid, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
            }

            override fun onConversionDataFail(p0: String?) {
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
            }

            override fun onAttributionFailure(p0: String?) {
            }
        }, this)
        AppsFlyerLib.getInstance().setDebugLog(BuildConfig.DEBUG)
        AppsFlyerLib.getInstance().start(this, appid)
    }

    private fun setWebView(web: WebView) {
        web.visibility = View.VISIBLE
        web.setSettingsExt(this) {
            uploadMessageAboveL = it
        }
        web.isLongClickable = true
        web.setOnLongClickListener {
            true
        }
        web.addJavascriptInterface(JsClass(), "ANDROID_JS_BRIDGE")
    }

    private suspend fun startPage() {
        withContext(Dispatchers.Main) {
            APageUtils.startPage(context = this@MainActivity)
            finish()
        }
    }

    private fun jsonObj(json: String): JsonBean? {
        try {
            val obj = JSONObject(json).optJSONObject("data") ?: return null
            return JsonBean(obj.optString("url", "") ?: "", obj.optString("key", "") ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    data class JsonBean(val url: String, val key: String)

    override fun onBackPressed() {
        if (web.canGoBack()) {
            web.goBack()
            return
        }
        super.onBackPressed()
    }

    //屏幕长亮
    override fun onResume() {
        super.onResume()
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (BuildConfig.DEBUG) Log.d("MainActivity_TAG", "onActivityResult")
        if (requestCode == 201 && resultCode == Activity.RESULT_OK) {
            val uris = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            uploadMessageAboveL?.onReceiveValue(uris)
            uploadMessageAboveL = null
        } else {
            uploadMessageAboveL?.onReceiveValue(arrayOf())
            uploadMessageAboveL = null
        }
    }
}