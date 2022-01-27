package com.common.componentes.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.componentes.fragment.SimpleWebViewClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;


public class WebViewUtil {

    // Verifies that a url opened by `Window.open` has a secure url.
    private static class FlutterWebChromeClient extends WebChromeClient {

        WebView webView;

        ICallback listener;

        public FlutterWebChromeClient(WebView webView, ICallback listener) {
            this.webView = webView;
            this.listener = listener;
        }

        @Override
        public boolean onCreateWindow(
                final WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            final WebViewClient webViewClient =
                    new WebViewClient() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean shouldOverrideUrlLoading(
                                @NonNull WebView view, @NonNull WebResourceRequest request) {
                            webView.loadUrl(request.getUrl().toString());
                            return true;
                        }

                        /*
                         * This method is deprecated in API 24. Still overridden to support
                         * earlier Android versions.
                         */
                        @SuppressWarnings("deprecation")
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            webView.loadUrl(url);
                            return true;
                        }
                    };

            final WebView newWebView = new WebView(webView.getContext());
            newWebView.setWebViewClient(webViewClient);

            final WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            // android 6.0 以下通过title获取
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")
                        || title.toLowerCase().contains("Page not found".toLowerCase())) {
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
                    if (listener != null) {
                        listener.onError();
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public static Map<String, String> extractHeaders(@Nullable Bundle headersBundle) {
        if (headersBundle == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> headersMap = new HashMap<>();
        for (String key : headersBundle.keySet()) {
            final String value = headersBundle.getString(key);
            headersMap.put(key, value);
        }
        return headersMap;
    }

    public static WebView initWebView(WebView webView) {
        return initWebView(webView, null);
    }

    public static WebView initWebView(WebView webView, ICallback listener) {
        webView.setWebViewClient(new SimpleWebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.setVisibility(View.INVISIBLE);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.toLowerCase().contains("about:blank")) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.postDelayed(() -> view.setVisibility(View.VISIBLE), 500);
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
                if (request.getUrl().toString().contains("favicon.ico")) {
                    return;
                }
                // 这个方法在6.0才出现
                int statusCode = errorResponse.getStatusCode();
                if (404 == statusCode || 500 == statusCode) {
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
                    if (listener != null) {
                        listener.onError();
                    }
                }
            }

            /*
             * This method is deprecated in API 24. Still overridden to support
             * earlier Android versions.
             */
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(url);
                    return false;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @RequiresApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                }
                return false;
            }
        });
        webView.setWebChromeClient(new FlutterWebChromeClient(webView, listener));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportMultipleWindows(true);
        webView.setVisibility(View.INVISIBLE);
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
