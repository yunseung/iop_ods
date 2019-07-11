package com.ibkc.ods.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ibkc.CLog;
import com.ibkc.common.jsbridge.JavaScriptBridge;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.databinding.ActivityMainBinding;
import com.ibkc.ods.webview.JavascriptAPI;
import com.ibkc.ods.webview.JavascriptSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by macpro on 2018. 6. 20..
 */

/**
 * 두 번째 webView native 화면.
 * 항상 첫 번째 화면 위에 뜬다.
 * 웹뷰에서 메인과 별개로 관리해줘야 할 화면이 있어서 만든 액티비티.
 */
public class SecondWebViewActivity extends BaseActivity {
    private ActivityMainBinding mBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mWebView = mBinding.mainWebView;
        mBinding.loadingLayout.setVisibility(View.GONE);
        // view & elements setting.
        initializeElements();
    }

    @Override
    protected void onResume() {
        // 지금의 webView 가 기존 webView 위에 올라온 child 라는 것을 알려주기 위한 태그. (OdsWebClient 에서 페이지 로딩 후 알림)
        // 상단 뒤로가기 버튼 클릭 시 새로운 웹뷰 액티비티를 닫아야 하는 경우도 있고..mainActivity, SecondActivity 에 따라 화면 이동이 달라지기 때문.
        mWebView.setTag(R.string.web_view_type_tag, Const.CHILD_WEB);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * activity elements 초기화.
     */
    private void initializeElements() {
        String param = getIntent().getStringExtra(Const.INTENT_KEY_DATA);
        mWebView.addJavascriptInterface(new JavaScriptBridge(SecondWebViewActivity.this, mWebView, JavascriptAPI.class), JavaScriptBridge.CALL_NAME);
        if (param == null) {
            mWebView.loadUrl(getIntent().getStringExtra(Const.URL));
        } else {
            try {

                //Post로 전송시 encoding이 풀리기 때문에 두번 인코딩함
                String temp = URLEncoder.encode(param, "UTF-8");
                temp = "sendData=" + URLEncoder.encode(temp, "UTF-8");
                mWebView.postUrl(getIntent().getStringExtra(Const.URL), temp.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                CLog.printException(e);
                mWebView.loadUrl("");
                Toast.makeText(getApplicationContext(), "URL encoding error", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsPopup) {
            return;
        }

        if (!mWebView.canGoBack()) {
            Intent i = new Intent();
            i.putExtra(Const.IS_FINISH, true);
            setResult(RESULT_OK, i);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 두 번째 웹뷰에서 전체 메뉴 이동시 두 번째 웹뷰를 지우고 첫 번째 웹뷰에서 메뉴 이동시키는 스크립트 함수.
     * @param url 첫 번째 웹뷰로 전달시켜 이동시킬 페이지 url.
     */
    public void menuFromChildView(String url) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Const.URL, url);
        resultIntent.putExtra(Const.TOP_MENU, true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 두 번째 웹뷰에서 뒤로가기 눌렀을 때 history 유/무에 따라 두 번째 웹뷰 activity 를 지울지, history back 할지 분기하는 스크립트 함수.
     */
    public void backFromChildView() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            if (SecondWebViewActivity.this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                Intent i = new Intent();
                i.putExtra(Const.IS_FINISH, true);
                setResult(RESULT_OK, i);
                finish();
            } else {
                SecondWebViewActivity.this.onBackPressed();
            }
        }
    }

    /**
     * PopupViewActivity 호출하여 두 번째 액티비티 위에 popup 을 띄우는 스크립트 함수.
     * @param url PopupViewActivity 로 전달하여 이동시킬 페이지 url.
     */
    public void showNativePopup(String url) {
        Intent popupIntent = new Intent(SecondWebViewActivity.this, PopupWebViewActivity.class);
        popupIntent.putExtra(Const.URL, url);
        startActivity(popupIntent);
        overridePendingTransition(0, 0);
    }

    /**
     * ImagePreviewWebViewActivity 호출하여 두 번째 액티비티 위에 popup 을 띄우는 스크립트 함수.
     * @param url ImagePreviewWebViewActivity 로 전달하여 이동시킬 페이지 url.
     */
    public void showImageTab(final String url, final String postData) {
        Intent intent = new Intent(SecondWebViewActivity.this, ImagePreviewWebViewActivity.class);
        intent.putExtra(Const.URL, url);
        intent.putExtra(Const.INTENT_KEY_DATA, postData);
        startActivity(intent);
    }

    @Override
    public void startOCR(JSONObject param) {
        try {
            mOCRCallback = param.getString("callback");

            if (param.getJSONObject(JavaScriptBridge.PARAM).getString("type").equals(Const.TYPE_VISIT)) { // 방문보고서 일 때
                Intent i = new Intent(SecondWebViewActivity.this, VisitPhotoGridActivity.class);
                i.putExtra("param", param.getJSONObject(JavaScriptBridge.PARAM).toString());
                startActivityForResult(i, Const.REQ_VISIT_PHOTO_ACTIVITY);
            } else if (param.getJSONObject(JavaScriptBridge.PARAM).getString("type").equals(Const.TYPE_LEAS)) { // 리스일 때
                Intent i = new Intent(SecondWebViewActivity.this, AddReportEquipGridActivity.class);
                i.putExtra("param", param.getJSONObject(JavaScriptBridge.PARAM).toString());
                startActivityForResult(i, Const.REQ_LEAS_REPORT_ACTIVITY);
            }
        } catch (JSONException e) {
            CLog.printException(e);
            Toast.makeText(getApplicationContext(), "Json exception from startOCR() script", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addNewWebView(final String url, final String postData) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                // 새 창에서 또 다른 새 (+) 메뉴로 이동시 history 를 지우기 위해 추가한 tag.
                // clear history 는 page finished 이후에 먹히는 성질이 있어서 아래 태그가 주어진 상황에서만 지우도록 함.
                mWebView.setTag(R.string.web_view_history_tag, Const.CLEAR_HISTORY);
                try {
                    //Post로 전송시 encoding이 풀리기 때문에 두번 인코딩함
                    String temp = URLEncoder.encode(postData, "UTF-8");
                    temp = "sendData=" + URLEncoder.encode(temp, "UTF-8");
                    mWebView.postUrl(url, temp.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    CLog.printException(e);
                    mWebView.loadUrl("");
                    Toast.makeText(getApplicationContext(), "URL encoding error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 전자서식 서명 완료 후 서식 화면 위로 추가서류 촬영 native 화면(AddReportEquipGridActivity)을 띄우는 함수
     * @param param
     */
    public void closeFormStartOCR(JSONObject param) {
        try {
            mOCRCallback = param.getString("callback");

            // 서식 activity 종료 후 추가서류 사진 촬영 화면으로 이동.
            Intent i = new Intent(SecondWebViewActivity.this, AddReportEquipGridActivity.class);
            i.putExtra("param", param.toString());
            startActivityForResult(i, Const.REQ_INST_MEDI_REPORT_ACTIVITY);
        } catch (JSONException e) {
            CLog.printException(e);
            Toast.makeText(getApplicationContext(), "Json exception from closeFormStartOCR() script", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_INST_MEDI_REPORT_ACTIVITY: // 추가서류, 장비 사진 촬영 전송 결과.
                if (resultCode == RESULT_OK && data != null) {
                    Intent i = new Intent();
                    JSONObject resJson = new JSONObject();
                    JSONObject param = new JSONObject();
                    try {
                        param.put(Const.CALLBACK, mOCRCallback);
                        param.put(Const.RESULT, new JSONObject(data.getStringExtra(Const.RESULT)));
                        resJson.put(JavaScriptBridge.PARAM, param);
                    } catch (JSONException e) {
                        CLog.printException(e);
                        resJson = null;
                    }

                    i.putExtra(Const.INTENT_KEY_DATA, resJson.toString());
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;

            case Const.REQ_LEAS_REPORT_ACTIVITY: // 메디컬 할부 리스 사진 촬영 결과.
            case Const.REQ_VISIT_PHOTO_ACTIVITY: // 방문 사진 촬영 전송 결과.
                if (resultCode == RESULT_OK && data != null) {
                    JSONObject param;
                    JSONObject result = new JSONObject();
                    try {
                        param = new JSONObject(data.getStringExtra(Const.RESULT));
                        result.put(Const.RESULT, param);
                        CLog.e(param.toString());
                    } catch (JSONException e) {
                        CLog.printException(e);
                        result = null;
                    }

                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, mOCRCallback, result);
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
