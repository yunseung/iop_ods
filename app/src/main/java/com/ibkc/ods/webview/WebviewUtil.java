package com.ibkc.ods.webview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.util.CUUID;

public class WebviewUtil {
    /**
     * 앱에서 사용되는 User-Agent 값을 생성한다. <br>
     * baseUserAgent 값이 있는 경우 baseUserAgent에 앱전용 User-Agent값이 append된다.
     *
     * @return User-Agent 문자열
     */
    public static String makeUserAgentString(Context context, String string) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(string)) {
            sb.append(string);
        }
        sb.append(";").append("COMPANY=").append("ibkcapital");
        sb.append(";").append("REQUEST_SVC_CD=").append("10");
        sb.append(";").append("DEVICE_APP_VER=").append(BuildConfig.VERSION_NAME);
        sb.append(";").append("DEVICE_SRL_NO=").append(CUUID.getUUID(context.getApplicationContext()));
        sb.append(";").append("DEVICE_OS=").append("android");
        sb.append(";").append("OS_VERSION=").append(Build.VERSION.RELEASE);
        sb.append(";").append("DEVICE_MODEL=").append(Build.MODEL);

        return sb.toString();
    }
}
