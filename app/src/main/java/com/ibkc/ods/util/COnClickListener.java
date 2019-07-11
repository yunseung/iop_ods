package com.ibkc.ods.util;

import android.os.Handler;
import android.view.View;

import com.ibkc.CLog;

/**
 * Custom Click Listener
 *
 * Double Click 등을 막기 위한 Custom click listener
 * click 상태를 static 변수에서 확인 하기 때문에 이 listener 를 사용하는 모든 view 들은 상태가 공유된다.
 * 그러므로, 다른 view 라 하더라도 이 listener 를 사용하면 view 들 간에도 double click 을 막는다.
 *
 */
public abstract class COnClickListener implements View.OnClickListener {
    private static final int DELAY_MILLIS = 600;
    private static boolean sIsClicked = false;
    private int mDelayMillis = DELAY_MILLIS;

    public abstract void COnClick(View v);

    public COnClickListener() {
        this(DELAY_MILLIS);
    }

    public COnClickListener(int delayMillis) {
        mDelayMillis = delayMillis;
    }

    @Override
    public final void onClick(View v) {

        if (sIsClicked) {
            CLog.w("======Blocked OnClick event!!=====");
            return;
        }

        sIsClicked = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sIsClicked = false;
            }
        }, mDelayMillis);

        COnClick(v);
    }
}
