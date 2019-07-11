package com.ibkc.ods.webview;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.ibkc.CLog;
import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.Const;
import com.ibkc.ods.activity.BaseActivity;
import com.ibkc.ods.util.CPermission;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class CWebView extends WebView {

    private Context mContext = null;
    private int mWebViewType = Const.PARENTS_WEB;

    public CWebView(Context context) {
        super(context);
        CLog.d(">> CWebView");
        if (context == null) return;
        mContext = context;
        init();
    }

    public CWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CLog.d(">> CWebView");
        if (context == null) return;
        mContext = context;
        init();
    }

    public CWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CLog.d(">> CWebView");
        if (context == null) return;
        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        CLog.d(">> CWebView");
        if (context == null) return;
        mContext = context;
        init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    public void setWebViewType(int type) {
        mWebViewType = type;
    }

    private void init() {

        CLog.d("view type : " + mWebViewType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSettings().setSafeBrowsingEnabled(false);
        }
        setWebViewClient(new OdsWebClient((BaseActivity)mContext));
        setWebChromeClient(new OdsWebChromeClient());

        setScrollContainer(true); // 뷰를 이동 가능하도록 하고, 스크롤이 있는 컨테이너인지를 지정.
        requestFocus(); // 웹뷰에 포커스가 우선적으로 가기 위함.
        requestFocusFromTouch(); // 웹뷰에 포커스가 우선적으로 가기 위함.
        setScrollbarFadingEnabled(true); //스크롤바 자동으로 사라짐
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //스크롤바 스타일

        WebSettings settings = getSettings();
        settings.setUserAgentString(WebviewUtil.makeUserAgentString(mContext.getApplicationContext(), settings.getUserAgentString()));
        settings.setDomStorageEnabled(true); // 돔저장소 사용
        settings.setDatabaseEnabled(true);  //html local storeage사용 여부
        settings.setAppCacheEnabled(true); //html local storeage사용 여부
        settings.setJavaScriptEnabled(true); // javascript 통신을 하기 위함.
        settings.setAllowContentAccess(false); // 폰에 설치돼있는 콘텐츠의 URL 에 접근 가능. 해당 URL 을 가지고 content provider 를 통해 앱을 실행시키거나 하는 등의 동작.
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 캐시 사용 안함.
        settings.setAllowFileAccess(true); // 웹 뷰 내에서 파일 액세스 활성화 여부
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //필요에 의해 팝업창을 띄울 경우가 있는데, 해당 속성을 추가해야 window.open() 이 제대로 작동합니다.
        settings.setUseWideViewPort(true); //와이드 화면 대응
        settings.setLoadWithOverviewMode(true); //웹페이지 기기화면 크기 대응
        settings.setLoadsImagesAutomatically(true); //웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정하는 속성
        settings.setGeolocationEnabled(true); //Geolocation API 사용
        settings.setSupportMultipleWindows(true); //open window 허용
        settings.setNeedInitialFocus(false);// requestFocus()호출시 focus를 가질 노드를 알려줄지 여부(?)
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true); // webView zoom
        settings.setSupportZoom(true); // webView zoom
        // origin policy for file access
        settings.setAllowUniversalAccessFromFileURLs(false); // file://URL이면 어느 오리진에 대해서도 Ajax로 요청을 보낼 수 있다
        settings.setAllowFileAccessFromFileURLs(false); // file://URL이면 어느 오리진에 대해서도 Ajax로 요청을 보낼 수 있다
        //쿠키 동기화
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= 11) {
            settings.setDisplayZoomControls(false);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            settings.setTextZoom(100);
        }


        // chrome inspector 등 에서 웹뷰 디버깅을 위해 넣어둠.
        if (BuildConfig.IS_DEV || BuildConfig.IS_TEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        // HTML5 configuration parametersettings.
        settings.setAppCachePath(mContext.getApplicationContext().getDir("appcache", 0).getPath());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            settings.setGeolocationDatabasePath(mContext.getApplicationContext().getDir("geolocation", 0).getPath());
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            settings.setDatabasePath("/data/data/" + mContext.getApplicationContext().getPackageName() + "/databases/");
        }

        //웹뷰 성능 향상
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        setHorizontalScrollBarEnabled(true); //스크롤바 삭제
        setVerticalScrollBarEnabled(true);   //스크롤바 삭제


        // webView 에서 https 보안으로 컨텐츠(이미지 텍스트)들이 에러나고 막히고 안보일때 대응하는 소스.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
                CLog.d("++ url=" + url + "/userAgent=" + userAgent + "/contentLength=" + contentLength + "/contentDisposition=" + contentDisposition + "/mimeType=" + mimeType);

                CPermission.getInstance().checkStoragePermission(mContext, new CPermission.PermissionGrantedListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {


                            // 개발 서버는 다운로드 되지 않는다.
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setMimeType(mimeType);
                            //------------------------COOKIE!!------------------------
                            String cookies = CookieManager.getInstance().getCookie(url);
                            request.addRequestHeader("cookie", cookies);

                            //------------------------COOKIE!!------------------------
                            request.addRequestHeader("User-Agent", userAgent);
                            request.setDescription("Downloading file...");

                            String fileName = "";
                            //파일 이름 설정


                            if (url.indexOf("?filename=") > -1) {
                                fileName = url.substring(url.indexOf("?filename="), url.length());
                                fileName = fileName.replace("?filename=", "");
                                fileName = URLDecoder.decode(fileName, "UTF-8");
                            } else {
                                fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                            }
                            CLog.d("++ fileName : " + fileName);
                            request.setTitle(fileName);
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                            DownloadManager dm = (DownloadManager) mContext.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            Toast.makeText(mContext.getApplicationContext(), "파일을 다운로드 합니다.", Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            CLog.printException(e);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });

            }
        });
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);

        if (outAttrs != null) {
            int imeActions = outAttrs.imeOptions & EditorInfo.IME_MASK_ACTION;

            if ((imeActions & EditorInfo.IME_ACTION_DONE) != 0) {
                // clear the existing action
                outAttrs.imeOptions ^= imeActions;
                // set the DONE action
                outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
            }
            if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
                outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
            }

        }
        return inputConnection;
    }


}
