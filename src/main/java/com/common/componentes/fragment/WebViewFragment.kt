package com.common.componentes.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.common.componentes.R
import com.common.componentes.util.WebViewUtil
import com.common.utils.activity.ActivityUtil
import com.common.utils.file.FileUtil
import java.io.File
import java.lang.Exception


/**
 * Created by KerriGan on 2017/8/4.
 */
class WebViewFragment : Fragment() {

    companion object {
        const val TAG = "WebViewFragment"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TYPE = "extra_type"
        const val WEB_ROOT_PATH = "web"
        const val TYPE_INNER_WEB = 0x10
        const val TYPE_DEFAULT = TYPE_INNER_WEB shl 1
        const val TYPE_MIME = TYPE_DEFAULT shl 1
        const val INTERFACE_NAME = "android"

        @JvmStatic
        fun openUrl(url: String): Bundle {
            return Bundle().apply { putString(WebViewFragment.EXTRA_URL, url) }
        }

        @JvmStatic
        fun openInnerUrl(url: String): Bundle {
            return Bundle().apply {
                putString(WebViewFragment.EXTRA_URL, url)
                putInt(WebViewFragment.EXTRA_TYPE, WebViewFragment.TYPE_INNER_WEB)
            }
        }

        @JvmStatic
        fun openWithMIME(url: String): Bundle {
            return Bundle().apply {
                putString(WebViewFragment.EXTRA_URL, url)
                putInt(WebViewFragment.EXTRA_TYPE, WebViewFragment.TYPE_MIME)
            }
        }
    }

    private var mWebView: WebView? = null
    private var mJsInterface: JavaScriptInterface? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cc_fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()

        if (arguments != null) {
            var url = arguments!!.get(EXTRA_URL) as String?
            var type = arguments!!.get(EXTRA_TYPE)
            if (type == null) {
                type = TYPE_DEFAULT
            }

            if (type == TYPE_DEFAULT) {
                if (url != null) {
                    mWebView?.loadUrl(url)
                }
            } else if (type == TYPE_INNER_WEB) {
                var file = context!!.getExternalFilesDir(WEB_ROOT_PATH)
                if (file != null) {
                    file = File(file, url)
                    if (file.exists() && !file.isDirectory) {
                        mWebView?.loadUrl("file://${file.absolutePath}")
                        Log.e(TAG, "load by dynamic")
                        return
                    }
                }
                mWebView?.loadUrl("file:///android_asset/${WEB_ROOT_PATH}/${url}")
                Log.e(TAG, "load by static")
            } else if (type == TYPE_MIME) {
                val extension = MimeTypeMap.getFileExtensionFromUrl(url)
                val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        ?: "text/html"
                toDoWithMIME(mime, url)
            }
        }
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView() {
        mWebView = view?.findViewById<View>(R.id.web_view) as WebView?
        mWebView?.webViewClient = object : SimpleWebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                view.loadUrl("about:blank")
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
                // 这个方法在6.0才出现
                val statusCode = errorResponse.statusCode
                println("onReceivedHttpError code = $statusCode")
                if (404 == statusCode || 500 == statusCode) {
                    view.loadUrl("about:blank")// 避免出现默认的错误界面
                }
            }
        }
        mWebView?.setWebChromeClient(object : SimpleWebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                // android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")
                            || title.toLowerCase().contains("Page not found".toLowerCase())) {
                        view.loadUrl("about:blank")// 避免出现默认的错误界面
                    }
                }
            }
        })

        val settings = mWebView?.getSettings()
        settings?.javaScriptEnabled = true
        settings?.domStorageEnabled = true
        settings?.databaseEnabled = true

        mJsInterface = JavaScriptInterface(context!!)
        mWebView?.addJavascriptInterface(mJsInterface, INTERFACE_NAME)
    }

    private fun toDoWithMIME(mime: String?, url: String?) {
        if (mime?.startsWith("text") == true) {
            var arr = FileUtil.readFileContent(File(url))
            if (arr != null) {
                mWebView?.loadDataWithBaseURL(null, String(arr), mime, "utf-8", null)
            }
        } else if (mime?.startsWith("image") == true || mime?.startsWith("video") == true) {
            mWebView?.loadUrl("file://${url}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.removeJavascriptInterface(INTERFACE_NAME)
        WebViewUtil.destroyWebView(mWebView, false)
    }

    class JavaScriptInterface(val context: Context) {

        @JavascriptInterface
        fun gotoAppDetailSettings() {
            context.startActivity(ActivityUtil.getAppDetailSettingIntent(context))
        }

        @JavascriptInterface
        fun gotoGPDetail() {
            try {
                ActivityUtil.jumpToMarket(context, context.packageName, "com.android.vending")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}