package com.ibkc.common.util.device;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ActivityUtil {
    /**
     * Activity Activity Stack에 존재 하는지 여부를 확인
     * @param context ApplicetionConext
     * @param activityName Activity Package + Activity Name
     * @return
     */
    public static boolean isStackActitvity(Context context, String activityName) {
        if (activityName == null) return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> actvity = am.getRunningTasks(5);
        for (int i = 0; i < actvity.size(); i++) {
            if (actvity.get(i).topActivity.toString().indexOf(activityName) > -1) {

                return true;
            }
        }
        return false;
    }
}
