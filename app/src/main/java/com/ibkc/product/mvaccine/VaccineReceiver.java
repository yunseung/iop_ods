package com.ibkc.product.mvaccine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class VaccineReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub


		
		/*-------------------------------- 안내 ------------------------------------------
		본 파일은 mVaccine 제품에 대한 연동의 이해를 돕기위해 작성 된 샘플코드 입니다. 
		백신 액티비티는 결과 값을 브로드캐스트로도 전달합니다.
		아래와 같이 브로드캐스트로 결과 값을 전달 받아 사용 하실 수 있습니다.		
		com.secureland.smartmedic.core.Constants.EMPTY_VIRUS - 악성코드, 루팅여부 모두 정상
		com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE1 - 악성코드 탐지 후 사용자가 해당 악성코드 앱을 삭제
		com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE2 - 악성코드 탐지 후 사용자가 해당 악성코드 앱을 미삭제
		----------------------------------------------------------------------------------*/

        //
        // "com.TouchEn.mVaccine.b2b2c.FIRE"
        // 수신 된 Intent 처리

        int i = intent.getIntExtra("result", 0);

        Log.e("CodeReceiver", "result = " + i);

        switch (i) {
            case com.secureland.smartmedic.core.Constants.EMPTY_VIRUS:                //1000
                Log.e("CodeReceiver", "com.secureland.smartmedic.core.Constants.EMPTY_VIRUS");
                break;

            case com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE1:        //1010
                Log.e("CodeReceiver", "com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE1");
                break;

            case com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE2:        //1100
                Log.e("CodeReceiver", "com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE2");
                break;
        }
    }
}
