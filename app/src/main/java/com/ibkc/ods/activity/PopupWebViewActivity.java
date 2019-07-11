package com.ibkc.ods.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.ibkc.CLog;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.webview.CWebView;

/**
 * Created by macpro on 2018. 6. 28..
 */

/**
 * webView 에서 popup 을 띄우기 곤란한 상황 (session, history 관리 등) 이 왔을 때 띄워주는 native popup activity.
 */
public class PopupWebViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");

        setContentView(R.layout.activity_popup_web);
        getApplication().getResources();

        // view & elements setting.
        initializeElements();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * activity elements 초기화.
     */
    private void initializeElements() {
        mWebView = findViewById(R.id.web_view);
        mWebView.loadUrl(getIntent().getStringExtra(Const.URL));
    }

    public void onClick(View v) {
        finish();
        overridePendingTransition(0, 0);
    }
}

