package com.ibkc.ods.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.ibkc.CLog;
import com.ibkc.common.Common;
import com.ibkc.common.jsbridge.JavaScriptBridge;
import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.config.ProjectUrl;
import com.ibkc.ods.databinding.ActivityMainBinding;
import com.ibkc.ods.databinding.DialogServerConfigBinding;
import com.ibkc.ods.util.preferences.CPreferences;
import com.ibkc.ods.vo.IDCard;
import com.ibkc.ods.webview.JavascriptAPI;
import com.ibkc.ods.webview.JavascriptSender;
import com.ibkc.product.ocr.OCRManager;
import com.ibkc.product.ocr.ResultIdCardMaskingTask;
import com.ibkc.product.ocr.ResultLprImageTask;
import com.raonsecure.touchen_mguard_4_0.MDMAPI;
import com.raonsecure.touchen_mguard_4_0.MDMResultCode;
import com.rosisit.idcardcapture.CameraActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ODS webView 의 main 을 맡고 있는 MainActivity
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding = null;

    private IDCard mIDCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mWebView = mBinding.mainWebView;

        // view & elements setting.
        initializeElements();
    }

    @Override
    protected void onResume() {
        // 지금 떠있는 webView 가 첫번째인지 두번째인지 web 에 알려주기 위한 태그. (OdsWebClient 에서 페이지 로딩 후 알림)
        mWebView.setTag(R.string.web_view_type_tag, Const.PARENTS_WEB);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //MDM 정책 해제
        RS_MDM_LogoutOffice();
        super.onDestroy();
    }

    /**
     * activity elements initialize.
     */
    private void initializeElements() {
        // webView initialized 할 때 마다 캐시와 히스토리 지움.
        mWebView.addJavascriptInterface(new JavaScriptBridge(MainActivity.this, mWebView, JavascriptAPI.class), JavaScriptBridge.CALL_NAME);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(ProjectUrl.initWebUrl(this));
    }

    /**
     * 버전 정보를 웹뷰에 표시하기 위해 호출되는 스크립트 함수.
     */
    public void checkVersion(String callback) {
        JSONObject param = new JSONObject();
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            param.put("appVersion", pInfo.versionName);
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, param);
        }catch(PackageManager.NameNotFoundException | JSONException e) {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, e.toString());
            CLog.printException(e);
        }
    }

    /**
     * 신분증 인식 사진 촬영.
     */
    @Override
    public void startOCR(JSONObject obj) {
        mIDCard = new IDCard();
        try {
            mOCRCallback = obj.getString("callback");
            if (((JSONObject) obj.get("param")).getString("type").equals(Const.TYPE_IDCARD)) {
                mIDCard.setParam(obj.getJSONObject(JavaScriptBridge.PARAM));
                OCRManager.getInstance().startOCR(this);
            } else if (((JSONObject) obj.get("param")).getString("type").equals(Const.TYPE_CAR)) {
                OCRManager.getInstance().startCarNumCamera(this);
            }
        } catch (JSONException e) {
            CLog.printException(e);
            Toast.makeText(getApplicationContext(), "startOCR() script json error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * native 에서 oneGuard app 을 통해서 mdm 로그인 인증하는 함수.
     *
     * web login page 에서 로그인 진행한 (id(gcuserID), password "ibkc123!@#" 고정) 를 스크립트로 받아서 MDM 에 등록된 사용자인지 DirectlyLogin 요청.
     * 성공시 스크립트를 호출하여 실제 로그인 진행
     * @param obj login 에 필요한 param
     * @param callback directlyLogin 결과 반환하는 콜백
     * @throws JSONException
     */
    public void doMdmLogin(final JSONObject obj, final String callback) throws JSONException {
        if (BuildConfig.IS_DEV || BuildConfig.IS_TEST) {
            Toast.makeText(MainActivity.this, "MDM 로그인을 시도합니다.", Toast.LENGTH_SHORT).show();
        }

        //MDM 로그인 한다
        MDMAPI.getInstance().RS_MDM_LoginDirectly(obj.getString("gcuserID"), "ibkc123!@#", new MDMAPI.MGuardCallbackListener() {
            @Override
            public void onCompleted(int resultCode, String msg) {
                Log.e("MDMAPI", "resultCode="+resultCode +" / msg=" + msg);
                if(resultCode == 0 || resultCode == 1){

                    //MDM 정책 적용
                    MDMAPI.getInstance().RS_MDM_LoginOffice(new MDMAPI.MGuardCallbackListener() {
                        @Override
                        public void onCompleted(int i, String s) {
                            Log.e("MDMAPI", "LoginOffice login="+i+"/"+s);
                        }
                    });

                    JSONObject param = new JSONObject();
                    try {
                        param.put(Const.RESULT, Const.SUCCESS);
                    } catch (JSONException e) {
                        CLog.printException(e);
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, "native json exception");
                    }
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, param);
                }else{
                    Toast.makeText(getApplicationContext(), "["+ resultCode +"]"+msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (BuildConfig.IS_DEV) { //개발용 웹뷰 리프레쉬
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Toast.makeText(getApplicationContext(), "개발모드 웹뷰 리프레쉬 = " + ProjectUrl.initWebUrl(getApplicationContext()), Toast.LENGTH_LONG).show();
                mWebView.reload();
                return false;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { //개발용 url 리프레쉬
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                final DialogServerConfigBinding binding = DataBindingUtil.bind(LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_server_config, null));
                binding.etServerAddress.setText(mWebView.getUrl());
                builder.setView(binding.getRoot())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = binding.etServerAddress.getText().toString();
                                CPreferences.setDomain(MainActivity.this, url);
                                String testUrl = ProjectUrl.initWebUrl(getApplicationContext());
                                mWebView.clearHistory();
                                mWebView.loadUrl(testUrl);
                                Toast.makeText(getApplicationContext(), "개발모드 접속 주소 = " + url, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return false;
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_OCR_ID: // 신분증 인식
                if (resultCode == CameraActivity.RETURN_OK || resultCode == CameraActivity.RETURN_OVERTIME) { //인식 성공 및 3번 실패
                    final ResultIdCardMaskingTask task = new ResultIdCardMaskingTask(mWebView, mOCRCallback, new ResultIdCardMaskingTask.TaskCompleted() {
                        @Override
                        public void onImageMaskingCompleted(byte[] maskingImage) {
                            mIDCard.setImageByteArray(maskingImage);
                        }
                    });
                    if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
                    } else {
                        task.execute(data);
                    }
                }
                break;

            case Const.REQ_DOCUMENT_LPR: // 차량번호 인식
                if (resultCode == CameraActivity.RETURN_OK || resultCode == CameraActivity.RETURN_OVERTIME) {
                    byte[] encryptImage_Document_LPR = data.getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY);
                    final ResultLprImageTask task = new ResultLprImageTask(mWebView, mOCRCallback);
                    if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, encryptImage_Document_LPR);
                    } else {
                        task.execute(encryptImage_Document_LPR);
                    }
                }
                break;

            case Const.REQ_NEW_WEB: // 새 창으로부터 받아온 정보.
                if (resultCode == RESULT_OK && data != null) {

                    // 두 번 째 웹뷰에서 첫 번 째 웹뷰로 돌아 왔을 때 상단에 10분 타이머를 리셋하기 위해서 돌아옴을 알려주는 부분..
                    if (data.getBooleanExtra(Const.IS_FINISH, false)) {
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, "fnStartTimer", null);
                        return;
                    }

                    if (data.getBooleanExtra(Const.TOP_MENU, false)) {
                        mWebView.loadUrl(data.getStringExtra(Const.URL));
                        return;
                    }

                    // 앞에서 받아 온 결과를 web 에 sending~~
                    String result = data.getStringExtra(Const.INTENT_KEY_DATA);
                    String callback = null;
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONObject param = json.getJSONObject(JavaScriptBridge.PARAM);
                        callback = param.getString(JavaScriptBridge.CALLBACK);
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, param);
                    } catch (JSONException e) {
                        CLog.printException(e);
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, "native json exception");
                    }
                }

                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 신분증 업로드 시 필요한 신분증 객체를 반환하는 함수.
     * @return 신분증 객체
     */
    public IDCard getIDCard() {
        return mIDCard;
    }
}
