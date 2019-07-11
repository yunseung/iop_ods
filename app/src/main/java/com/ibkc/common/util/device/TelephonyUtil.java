package com.ibkc.common.util.device;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;


public class TelephonyUtil {

    /**
     * 단말기 전화 번호를 가져온다.
     *
     * @param context
     * @return String
     */
    public static String getPhoneNumber(Context context) {

        String phoneNumber = "";
        // MSISDN 추출

        TelephonyManager tpManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            phoneNumber = tpManager.getLine1Number();
        }

        // 국가코드
        if (phoneNumber != null && phoneNumber.length() > 0) {
            phoneNumber = phoneNumber.replace("+82", "0");
        }

        if (phoneNumber == null || phoneNumber.equals("null")) {
            phoneNumber = "";
        }

        return phoneNumber;
    }


}
