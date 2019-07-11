package com.ibkc.ods.util.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by macpro on 2018. 7. 11..
 */

/**
 * ODS preferences class
 */
public class CPreferences {
    private static final String UUID = "UUID"; //앱 처음시작
    private static final String APP_FIRST_LAUNCH = "APP_FIRST_LAUNCH";
    private static final String DOMAIN = "DOMAIN";

    public static final String PREF_NAME = "ibkc_ods.pref";

    private static void setString(Context context, String key, String value) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(Context context, String key, String defValue) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        return pref.getString(key, defValue);
    }

    private static void setBoolean(Context context, String key, boolean value) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getBoolean(Context context, String key, boolean defValue) {
        assert context != null;
        assert key != null;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        return pref.getBoolean(key, defValue);
    }

    public static void setDomain(Context context, String domain) {
        assert context != null;
        setString(context, DOMAIN, domain);
    }

    public static String getDomain(Context context) {
        assert context != null;
        return getString(context, DOMAIN, "");
    }
    public static String getUUID(Context context) {
        assert context != null;
        return getString(context, UUID, null);
    }

    public static void setUUID(Context context, String data) {
        assert context != null;
        setString(context, UUID, data);
    }
}
