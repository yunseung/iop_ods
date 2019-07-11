package com.ibkc.ods.activity;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by macpro on 2018. 6. 20..
 */

/**
 * 웹뷰에서 이미지보기 팝업을 보여주는 native 화면.
 */
public class ImagePreviewWebViewActivity extends BaseActivity {
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


    /**
     * activity elements initialize.
     */
    private void initializeElements() {
        String param = getIntent().getStringExtra(Const.INTENT_KEY_DATA);
        mWebView.addJavascriptInterface(new JavaScriptBridge(ImagePreviewWebViewActivity.this, mWebView, JavascriptAPI.class), JavaScriptBridge.CALL_NAME);
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
        if (!mWebView.canGoBack()) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
