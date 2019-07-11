package com.ibkc.common;

import android.util.Log;

public class Common {
    public static Boolean debug = false;

    public static void printException(Exception e) {
        StackTraceElement[] temp = e.getStackTrace();
        for (StackTraceElement ste : temp) {
            Log.w("com.ibkc.common",  ste.toString());
        }
    }

}
