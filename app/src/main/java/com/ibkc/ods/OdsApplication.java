package com.ibkc.ods;

import android.app.Application;

import com.ibkc.common.util.StringUtils;
import com.ibkc.common.util.io.FileManager;

import java.io.File;

/**
 * Created by macpro on 2018. 6. 25..
 */

public class OdsApplication extends Application {

    private static OdsApplication APP = null;

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        APP = this;

    }


}
