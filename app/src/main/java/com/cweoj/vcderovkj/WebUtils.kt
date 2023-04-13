package com.cweoj.vcderovkj

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


/**
 * @ClassName: WebUtils
 * @Description: web拓展
 * @Author: zhouzuowei
 * @Date: 2023/4/13 21:16
 */
fun WebView.setSettingsExt(
    context: AppCompatActivity,
    uploadMessageAboveL: (ValueCallback<Array<Uri>>?) -> Unit
) {
    settings.cacheMode = WebSettings.LOAD_NO_CACHE
    settings.javaScriptEnabled = true
    settings.allowContentAccess = true
    settings.allowFileAccess = true
    settings.allowFileAccessFromFileURLs = true
    settings.domStorageEnabled = true
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.mixedContentMode = MIXED_CONTENT_COMPATIBILITY_MODE
    webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

//        override fun onReceivedSslError(
//            view: WebView?,
//            handler: SslErrorHandler?,
//            error: SslError?
//        ) {
//            Log.d("setSettingsExt", "onReceivedSslError")
//            handler?.proceed()
//        }

        override fun onReceivedError(
            view: WebView?, errorCode: Int, description: String?, failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return
            }
        }

        override fun onReceivedError(
            view: WebView?, request: WebResourceRequest?, error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.d("setSettingsExt", "shouldOverrideUrlLoading: ${request?.url.toString()}")
            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.d("setSettingsExt", "shouldOverrideUrlLoading: $url")
            return false
        }

//        override fun onRenderProcessGone(
//            view: WebView?,
//            detail: RenderProcessGoneDetail?
//        ): Boolean {
//            return super.onRenderProcessGone(view, detail)
//        }
    }

    webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            Log.d("setSettingsExt", "onShowFileChooser: ${filePathCallback}")
            uploadMessageAboveL(filePathCallback)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            context.startActivityForResult(
                Intent.createChooser(i, "Image Chooser"),
                201)
            return true
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
        }

//        override fun onJsPrompt(
//            view: WebView?,
//            url: String?,
//            message: String?,
//            defaultValue: String?,
//            result: JsPromptResult?
//        ): Boolean {
//            result?.cancel()
//            return true
//        }

//        @SuppressLint("UsingALog")
//        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//            val msg = consoleMessage ?: return false
//            return super.onConsoleMessage(consoleMessage)
//        }
    }
}