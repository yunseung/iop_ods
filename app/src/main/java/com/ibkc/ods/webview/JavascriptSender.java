package com.ibkc.ods.webview;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.ibkc.CLog;

/**
 * Created by macpro on 2018. 7. 2..
 */

public class JavascriptSender {
    private final String TAG = JavascriptSender.class.getSimpleName();

    private JavascriptSender() {
    }

    public static JavascriptSender getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final JavascriptSender INSTANCE = new JavascriptSender();
    }

    /**
     * 자바스크립를 실행한다.
     *
     * @param javascript 자바스크립트 코드
     */
    public void callJavascript(final WebView webView, final String javascript) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            ValueCallback<String> resultCallback = null;
            webView.evaluateJavascript(javascript, resultCallback);
            CLog.d("++ loadUrl : " + javascript);
        } else {
            webView.loadUrl("javascript:" + javascript);
        }
    }

    /**
     * 지정된 웹뷰에 javascript function callback 요청
     *
     * @param webView
     * @param funcName
     * @param obj
     */
    public void callJavascriptFunc(WebView webView, String funcName, Object obj) {
        String param = obj == null ? "" : obj.toString();
        CLog.d(param);
        String func = String.format("%s(%s)", funcName, param);
        callJavascript(webView, func);
    }
}

