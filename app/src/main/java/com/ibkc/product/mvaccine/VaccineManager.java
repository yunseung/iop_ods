package com.ibkc.product.mvaccine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.TouchEn.mVaccine.b2b2c.activity.BackgroundScanActivity;
import com.TouchEn.mVaccine.b2b2c.util.Global;

import static com.ibkc.product.mvaccine.VaccineConst.REQUEST_CODE;

public class VaccineManager {

    private static class LazyHolder {
        private static final VaccineManager instance = new VaccineManager();
    }

    public static VaccineManager getInstance() {
        return LazyHolder.instance;
    }

    public void initVaccine() {
        /*------------------------ mVaccine 사이트ID, 라이선스key 설정 ------------------------------
  	사이트 인증을 위해 지급받은 사이트 ID, KEY 값을 정확하게 입력해야 합니다.(라온시큐어 담당SE제공)
    ID, KEY 값이 맞지 않을 경우 mVaccine 구동이 정상적으로 되지 않습니다.
    -------------------------------------------------------------------------------------*/

        com.secureland.smartmedic.core.Constants.site_id = "ibk_capital_app";
        com.secureland.smartmedic.core.Constants.license_key = "0b9114d2dbf5864ac95fa7a540bf9136f85e9d06";
        com.secureland.smartmedic.core.Constants.debug = false; // 디버깅 필요 시 true 설정
        Global.debug = false; // 디버깅 필요 시 true 설정
    }

    /*------------------ 권장 옵션 (mini 모드) --------------------
	검사진행 중 UI가 없는 간소화 모드입니다.
	옵션에 따라 백신의 액티비티에서 루팅검사, 악성코드 검사를 실행합니다.
	--------------------------------------------------------*/
    public void mini(Context context) {
        Intent i = new Intent(context, BackgroundScanActivity.class); // BackgroundScanActivity와 통신할 Intent생성

        //BackgroundScanActivity로 넘길 옵션값 설정
        i.putExtra("useBlackAppCheck", true);  // 루팅 검사를 실시하면 루팅 우회 앱 설치 여부까지 검사
        i.putExtra("scan_rooting", true);     // 루팅 검사
        i.putExtra("scan_package", true);
        i.putExtra("useDualEngine", false);
        i.putExtra("backgroundScan", false);  // mini 전용
        i.putExtra("rootingexitapp", true);
        i.putExtra("rootingyesorno", true);
        i.putExtra("rootingyes", true);
        i.putExtra("show_update", true);
        i.putExtra("show_license", true);
        i.putExtra("show_notify", true);    // mini 전용
        i.putExtra("notifyClearable", false);    // mini 전용
        i.putExtra("notifyAutoClear", false);    // mini 전용
        i.putExtra("show_toast", false);
        i.putExtra("show_warning", false);
        i.putExtra("show_scan_ui", false);    // mini 전용
        i.putExtra("showBlackAppName", true);

        ((AppCompatActivity) context).startActivityForResult(i, REQUEST_CODE); //Intent를 보내고 결과값을 얻어옴

    }
}
