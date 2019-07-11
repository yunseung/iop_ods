package com.ibkc;

import android.util.Log;


import com.ibkc.common.util.StackTraceInfo;
import com.ibkc.ods.BuildConfig;


/**
 * 로그 Util
 */
public class CLog {

    private static String LOG_TAG = "IBKC";

    public static void v(String tag, String msg) {

        print(Log.VERBOSE, tag, getClassName(), msg,false);
    }

    public static void d(String tag, String msg) {
        print(Log.DEBUG, tag, getClassName(), msg, false);
    }

    public static void i(String tag, String msg) {
        print(Log.INFO, tag, getClassName(), msg,false);
    }

    public static void w(String tag, String msg) {
        print(Log.WARN, tag, getClassName(), msg,false);
    }

    public static void e(String tag, String msg) {
        print(Log.ERROR, tag, getClassName(), msg, false);
    }

    public static void v(String msg) {
        print(Log.VERBOSE, LOG_TAG, getClassName(), msg, false);
    }

    public static void d(String msg) {
        print(Log.DEBUG, LOG_TAG, getClassName(), msg, false);
    }

    public static void i(String msg) {
        print(Log.INFO, LOG_TAG, getClassName(), msg, false);
    }

    public static void w(String msg) {
        print(Log.WARN, LOG_TAG, getClassName(), msg, false);
    }

    public static void e(String msg) {
        print(Log.ERROR, LOG_TAG, getClassName(), msg, false);
    }

    /**
     * 익셉션 발생로그는 출력
     * @param e
     */
    public static void printException(Exception e) {
        StackTraceElement[] temp = e.getStackTrace();
        for (StackTraceElement ste : temp) {
            print(Log.WARN, LOG_TAG, "", ste.toString(), true);
        }
    }

    /**
     * 긴 로그는 모두 출력이 되지 않으므로, 잘라서 출력 하는 함수
     * develop level이거나 e로그만 출력된다.
     *
     * @param logLevel : Log.VERBOSE, Log.DEBUG...
     * @param tag      : TAG
     * @param log      : log
     */
    private static void print(int logLevel, String tag, String className, String log, boolean show) {
        if (BuildConfig.IS_DEV || BuildConfig.IS_TEST) {
            int outputLength = 3000;
            int length = log.length();

            if (length > outputLength) {
                for (int i = 0; i < length / outputLength + 1; i++) {
                    int start = i * outputLength;
                    int end = (i + 1) * outputLength;
                    if (end > length) {
                        end = length;
                    }

                    switch (logLevel) {
                        case Log.VERBOSE:
                        case Log.DEBUG:
                            if(i == 0){
                                Log.d(tag, className + log.substring(start, end));
                            }else{
                                Log.d(tag, log.substring(start, end));
                            }
                            break;
                        case Log.INFO:
                            if(i == 0){
                                Log.i(tag, className + log.substring(start, end));
                            }else{
                                Log.i(tag, log.substring(start, end));
                            }
                            break;
                        case Log.WARN:
                            if(i == 0){
                                Log.w(tag, className + log.substring(start, end));
                            }else{
                                Log.w(tag, log.substring(start, end));
                            }
                            break;
                        case Log.ERROR:
                            if(i == 0){
                                Log.e(tag, className + log.substring(start, end));
                            }else{
                                Log.e(tag, log.substring(start, end));
                            }
                            break;
                    }
                }
            } else {
                switch (logLevel) {
                    case Log.VERBOSE:
                    case Log.DEBUG:
                    case Log.INFO:
                    case Log.WARN:
                        Log.w(tag, className + log);
                        break;
                    case Log.ERROR:
                        Log.e(tag, className + log);
                        break;
                }
            }
        }else{
            return;
        }

    }

    public static String getClassName() {
        String sTAG = "";
        if (BuildConfig.IS_DEV || BuildConfig.IS_TEST ) {
            Thread t = Thread.currentThread();
            if (t.getStackTrace() != null && t.getStackTrace().length > 5) {
                sTAG = sTAG + "(" + StackTraceInfo.getCurrentFileName(3) + ")";
//                sTAG = sTAG.replace(".javajava", "(" + StackTraceInfo.getCurrentFileName(3) + ")");
            }
            return sTAG;
        }else{
            return sTAG;
        }
    }
}