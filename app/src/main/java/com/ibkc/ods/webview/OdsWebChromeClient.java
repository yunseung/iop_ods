package com.ibkc.ods.webview;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.ibkc.CLog;
import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.util.eventbus.WebConsoleErrorEventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by macpro on 2018. 6. 15..
 */

public final class OdsWebChromeClient extends WebChromeClient {
    private final String TAG = OdsWebChromeClient.class.getSimpleName();

    public OdsWebChromeClient() {
        super();
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();

        if ("ERROR".equals(messageLevel.toString()) && consoleMessage.message() != null) {
            String message = consoleMessage.message();
            if (consoleMessage.message().indexOf("onAndroidHardwareBackPressed") > -1) {
                //백버튼 에러는 웹뷰 백을 한다.
                EventBus.getDefault().post(new WebConsoleErrorEventBus(message, WebConsoleErrorEventBus.BACK));
                return true;
            } else if (consoleMessage.message().indexOf("Unable to preventDefault inside passive event listener due to target being treated as passive.") > -1) {
                //패턴 웹 에러는 패스 한다.
                return super.onConsoleMessage(consoleMessage);
            }
        }

        if (BuildConfig.DEBUG == false) return true; //개발일 경우 Error 로그가 나오면 얼럿을 다띄운다
        String[] sourceIds = consoleMessage.sourceId().split("/");
        String sourceId = sourceIds[sourceIds.length - 1];
        String message = sourceId + " : " + consoleMessage.lineNumber() + "  " + consoleMessage.message();

        if (consoleMessage.message().indexOf("TypeError") > -1 ||
                consoleMessage.message().indexOf("ReferenceError") > -1) {
            CLog.e(message);
            EventBus.getDefault().post(new WebConsoleErrorEventBus(message, WebConsoleErrorEventBus.NONE));
            return true;
        }

        return super.onConsoleMessage(consoleMessage);
    }
}
