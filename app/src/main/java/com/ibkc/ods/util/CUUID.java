package com.ibkc.ods.util;

import android.content.Context;

import com.ibkc.CLog;
import com.ibkc.ods.util.preferences.CPreferences;

/**
 * UUID 생성에 필요한 class.
 */
public class CUUID {
    private static String uniqueID = null;
    /**
     * 랜덤 UUID를 생성한다.
     * @param context
     * @return
     */
    public static synchronized String getUUID(Context context) {
        if(uniqueID != null){
            CLog.d("++ uniqueID : "+uniqueID);
            return uniqueID;
        }
        uniqueID = CPreferences.getUUID(context);
        if(uniqueID == null){
            uniqueID = java.util.UUID.randomUUID().toString();
            CPreferences.setUUID(context, uniqueID);
        }
        CLog.d("++ uniqueID : "+uniqueID);
        return uniqueID;
    }
}
