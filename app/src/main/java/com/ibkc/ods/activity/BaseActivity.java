package com.ibkc.ods.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.ibkc.CLog;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.config.ProjectUrl;
import com.ibkc.ods.util.eventbus.WebConsoleErrorEventBus;
import com.ibkc.ods.util.ui.PopupUtil;
import com.ibkc.ods.webview.CWebView;
import com.ibkc.ods.webview.JavascriptSender;
import com.ibkc.product.transkey.TranskeyManager;
import com.raonsecure.touchen_mguard_4_0.MDMAPI;
import com.softsecurity.transkey.TransKeyActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by macpro on 2018. 7. 3..
 */

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    public CWebView mWebView = null;
    public String mOCRCallback = null;
    public String mTransKeyCallbackFunc = null;
    public boolean mIsPopup = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CLog.d(">> onResume");
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (RuntimeException e) {
            CLog.printException(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CLog.d(">> onStop");
        try {
            EventBus.getDefault().unregister(this);
        } catch (RuntimeException e) {
            CLog.printException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearCache(true);
        mWebView.destroyDrawingCache();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    /**
     * webView 에서 openWindow 같은 popup 호출이 어려워서 새로운 activity (new tab) 을 띄운다.
     *
     * @param url new tab url
     */
    public void addNewWebView(String url, String postData) {
        Intent intent = new Intent(this, SecondWebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.URL, url);
        intent.putExtra(Const.INTENT_KEY_DATA, postData);
        startActivityForResult(intent, Const.REQ_NEW_WEB);
    }

    /**
     * webView 에서 popup 이 떠있을 때 뒤로가기 버튼을 막기 위한 flag
     * @param isPopup popup show ? true : false
     */
    public void setIsPopup(boolean isPopup) {
        mIsPopup = isPopup;
    }

    /**
     * webView 에서 10분 로그아웃 popup 떠있을 때 popup 의 확인 버튼을 눌렀을 경우 실질적으로 mainActivity 를 재기동 하고 로그인 페이지로 보내주는 함수.
     */
    public void doLogout() {
        RS_MDM_LogoutOffice();
        Intent i = new Intent(BaseActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void RS_MDM_LogoutOffice(){
        //MDM 정책 해제
        MDMAPI.getInstance().RS_MDM_LogoutOffice(new MDMAPI.MGuardCallbackListener() {
            @Override
            public void onCompleted(int i, String s) {
                Log.e("MDMAPI", "LogoutOffice="+i+"/"+s);
            }
        });
    }
    /**
     * 사진촬영 솔루션 호출
     *
     * @param param 카메라 타입, callback 들어있음. (신분증 인식, 차량번호 인식, 일반 카메라)
     */
    public void startOCR(JSONObject param) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.REQ_TRANSKEY:
                onActivityResult_REQ_TRANSKEY(data);
                break;
            default:
                break;
        }
    }

    private void onActivityResult_REQ_TRANSKEY(Intent data) {
        if (data == null) return;
        //사용자가 입력한 키보드 데이터 값을 암호화하여 반환. 입력 값 유추방지.이 데이터는 서버의 Decrypt API를이용하여 plain-data로 디코딩 가능함
        String cipherData = data.getStringExtra(TransKeyActivity.mTK_PARAM_CIPHER_DATA_EX); // 암호화 값
        //암호화에 사용된 Key 값 //Hex스트링으로 변환 해서 서버에 전달
        byte[] secureKey_pbk = data.getByteArrayExtra(TransKeyActivity.mTK_PARAM_SECURE_KEY);
        String secureKey = TranskeyManager.getInstance().toHexString(secureKey_pbk);

        String dummyData = data.getStringExtra(TransKeyActivity.mTK_PARAM_DUMMY_DATA);
        int iRealDataLength = data.getIntExtra(TransKeyActivity.mTK_PARAM_DATA_LENGTH, 0);

        JSONObject json = new JSONObject();
        try {
            json.put("cipherData", cipherData);
            json.put("secureKey", secureKey);
            json.put("dummyData", dummyData);
            json.put("iRealDataLength", iRealDataLength);
        } catch (JSONException e) {
            CLog.printException(e);
            json = null;
        }
        if (mWebView == null) return;

        if (json != null) {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mTransKeyCallbackFunc, json);
        } else {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mTransKeyCallbackFunc, "transkey result is null");
        }

    }

    @Override
    public void onBackPressed() {
        if (mIsPopup) {
            return;
        }

        if (mWebView.canGoBack()) {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, "onAndroidHardwareBackPressed", null);
        } else {
            if (mWebView.getUrl().contains("MNUODSLOGN000")) {
                PopupUtil.showDefaultPopup(BaseActivity.this, getString(R.string.app_exit_message), new PopupUtil.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        finish();
                    }
                });
            } else if (mWebView.getUrl().contains("MNUODSMAIN000")) {
                PopupUtil.showDefaultPopup(BaseActivity.this, getString(R.string.logout_message), new PopupUtil.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //MDM 정책 해제
                        RS_MDM_LogoutOffice();
                        Intent i = new Intent(BaseActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            } else {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    PopupUtil.showDefaultPopup(BaseActivity.this, getString(R.string.app_exit_message), new PopupUtil.OnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveClick() {
                            finish();
                        }
                    });
                }
            }
        }
    }

    /**
     * 웹뷰 에러 이벤트 버스
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCall(WebConsoleErrorEventBus event) {
        if(event.type ==WebConsoleErrorEventBus.BACK ){
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }else{
                this.finish();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("스크립트 에러");
            builder.setMessage((String) event.msg);
            builder.show();
        }
    }
}
