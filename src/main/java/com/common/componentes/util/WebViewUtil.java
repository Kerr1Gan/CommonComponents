package com.common.componentes.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.common.componentes.fragment.SimpleWebChromeClient;
import com.common.componentes.fragment.SimpleWebViewClient;


public class WebViewUtil {

    public static WebView initWebView(WebView webView) {
        return initWebView(webView, null);
    }

    public static WebView initWebView(WebView webView, ICallback listener) {
        webView.setWebViewClient(new SimpleWebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("about:blank");
                // 断网或者网络连接超时
//                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
//                    view.loadUrl("about:blank"); // 避免出现默认的错误界面
//                }
                if (listener != null) {
                    listener.onError();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                System.out.println("onReceivedHttpError code = " + statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
                    if (listener != null) {
                        listener.onError();
                    }
                }
            }
        });
        webView.setWebChromeClient(new SimpleWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // android 6.0 以下通过title获取
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
                        view.loadUrl("about:blank");// 避免出现默认的错误界面
                        if (listener != null) {
                            listener.onError();
                        }
                    }
                }
            }
        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        return webView;
    }

    public static void destroyWebView(WebView webView, boolean clearCache) {
        if (webView == null) {
            return;
        }
        // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
        webView.loadUrl("about:blank");
        webView.setWebViewClient(null);
        webView.setWebChromeClient(null);
        webView.clearHistory();
        webView.clearCache(clearCache);
        webView.freeMemory();
        webView.pauseTimers();
        webView.destroy();
    }

    public interface ICallback {
        void onError();
    }
}
