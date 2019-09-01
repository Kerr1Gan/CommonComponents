package com.common.componentes.util;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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
                if (listener != null) {
                    listener.onError();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        webView.setWebChromeClient(new SimpleWebChromeClient());

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
