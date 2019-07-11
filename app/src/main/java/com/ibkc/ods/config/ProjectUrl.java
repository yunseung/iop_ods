package com.ibkc.ods.config;

import android.content.Context;
import android.os.Build;

import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.util.preferences.CPreferences;

/**
 * ODS url setting
 */
public class ProjectUrl {
    public static final String MENU_URL = "/ib20/mnu/";
    public static final String FIRST_MENU = "MNUODSLOGN000";
    public static String connWebUrl = BuildConfig.WEB_URL;

    /**
     * 개발자 편의를 위해 만든 기능
     * connWebUrl을 개발자 서버로 바꿔준다
     * @param context
     * @return 개발자가 저장한 호출화면 url
     */
    public static String initWebUrl(Context context) {
        if (BuildConfig.IS_DEV == false) { //개발이 아닐 경우에만 테스트 url을 생성한다
            return BuildConfig.WEB_URL + ProjectUrl.MENU_URL + ProjectUrl.FIRST_MENU;
        }
        String testUrl = CPreferences.getDomain(context);
        if ("".equals(testUrl)) { //테스트 url이 없으면
            return BuildConfig.WEB_URL + ProjectUrl.MENU_URL + ProjectUrl.FIRST_MENU;
        } else {
            ProjectUrl.connWebUrl = testUrl.substring(0, testUrl.indexOf(ProjectUrl.MENU_URL));
            return testUrl;
        }

    }
}
