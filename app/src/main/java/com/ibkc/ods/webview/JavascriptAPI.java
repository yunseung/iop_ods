package com.ibkc.ods.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.ibkc.common.jsbridge.JSBridge;
import com.ibkc.common.jsbridge.JavaScriptBridge;
import com.ibkc.common.util.StringUtils;
import com.ibkc.ods.Const;
import com.ibkc.ods.activity.BaseActivity;
import com.ibkc.ods.activity.MainActivity;
import com.ibkc.ods.activity.SecondWebViewActivity;
import com.ibkc.ods.config.ProjectUrl;
import com.ibkc.ods.network.IDCardImageUpload;
import com.ibkc.product.transkey.TranskeyManager;

import org.json.JSONException;
import org.json.JSONObject;

public class JavascriptAPI extends JSBridge{

    /*********************************************   웹뷰   ******************************************/
    /**
     * 기존 웹뷰 위에 새로운 액티비티 웹 뷰 올리기.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "addNewTab", explain = "기존 웹뷰 위에 새로운 웹 뷰 올리기.",
            param = {"url : 이동 할 페이지", "data : 서식 등 띄울 때 웹으로 전달할 데이터"})
    public void addNewTab(WebView webview, Context context, JSONObject json) throws JSONException {
        boolean ib20Menu = true; //ib20 메뉴일 경우
        try {
            ib20Menu = json.getJSONObject(JavaScriptBridge.PARAM).getBoolean("ib20Menu");
        } catch (JSONException je) {
            ib20Menu = true;
        }

        JSONObject params = json.getJSONObject(JavaScriptBridge.PARAM);
        String url = StringUtils.decodingUrl(params.getString(Const.URL), "UTF-8");
        String data = params.toString();

        if (ib20Menu) {
            url = ProjectUrl.connWebUrl + ProjectUrl.MENU_URL + url;
        }

        ((BaseActivity) context).addNewWebView(url, data);
    }

    /**
     * webView 에서 이미지를 보이는 팝업이 있는데 그 팝업을 native 팝업으로 대체한다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "showImageTab", explain = "기존 웹뷰 위에 새로운 웹 뷰 올리기.",
            param = {"url : 이동 할 페이지", "data : 서식 등 띄울 때 웹으로 전달할 데이터"})
    public void showImageTab(WebView webview, Context context, JSONObject json) throws JSONException {
        boolean ib20Menu = true; //ib20 메뉴일 경우
        try {
            ib20Menu = json.getJSONObject(JavaScriptBridge.PARAM).getBoolean("ib20Menu");
        } catch (JSONException je) {
            ib20Menu = true;
        }

        JSONObject params = json.getJSONObject(JavaScriptBridge.PARAM);
        String url = StringUtils.decodingUrl(params.getString(Const.URL), "UTF-8");
        String data = params.toString();

        if (ib20Menu) {
            url = ProjectUrl.connWebUrl + ProjectUrl.MENU_URL + url;
        }
        if (context instanceof SecondWebViewActivity) {
            ((SecondWebViewActivity) context).showImageTab(url, data);
        }
    }

    /**
     * SecondWebView 에서 webView 상의 전체 메뉴 이동시 SecondWebView 를 삭제하고 MainWebView 에 url loading 을 전달하기 위한 함수이다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "menuFromChildView", explain = "child web view 에서 햄버거 메뉴 이동시.",
            param = {"url : 이동 할 페이지"})
    public void menuFromChildView(WebView webview, Context context, JSONObject json) throws JSONException {
        if (context instanceof SecondWebViewActivity) {
            ((SecondWebViewActivity) context).menuFromChildView(StringUtils.decodingUrl(ProjectUrl.connWebUrl + ProjectUrl.MENU_URL + json.getJSONObject(JavaScriptBridge.PARAM).getString(Const.URL), "UTF-8"));
        }
    }

    /**
     * SecondWebView 에서 webView 상의 뒤로가기 버튼 등의 동작을 했을 때 webView canGoBack() 함수와 history 를 검사하여
     * goBack() 동작을 할지, SecondWebView 를 삭제할지 판단하기 위한 함수이다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "backFromChildView", explain = "child web view 에서 상단 뒤로가기 버튼 눌렀을 때.")
    public void backFromChildView(WebView webview, Context context, JSONObject json) throws JSONException {
        if (context instanceof SecondWebViewActivity) {
            ((SecondWebViewActivity) context).backFromChildView();
        }
    }

    /**
     * 웹에서 띄우는 팝업을 투명 액티비티 생성하여 native 로 띄워준다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "showNativePopup", explain = "웹에서 띄우는 팝업을 투명 액티비티 생성하여 native 로 띄워준다.",
            param = {"url : 이동 할 페이지"})
    public void showNativePopup(WebView webview, Context context, JSONObject json) throws JSONException {
        if (context instanceof SecondWebViewActivity) {
            ((SecondWebViewActivity) context).showNativePopup(StringUtils.decodingUrl(ProjectUrl.connWebUrl + ProjectUrl.MENU_URL + json.getJSONObject(JavaScriptBridge.PARAM).getString(Const.URL), "UTF-8"));
        } else {
            // 무시
        }
    }

    /**
     * 새로운 웹 뷰 삭제.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "closeNewTab", explain = "새로운 웹 뷰 삭제.")
    public void closeNewTab(WebView webview, Context context, JSONObject json) throws JSONException {
        Intent intent = ((BaseActivity) context).getIntent();
        intent.putExtra(Const.INTENT_KEY_DATA, json.toString());
        ((BaseActivity) context).setResult(Activity.RESULT_OK, intent);
        ((BaseActivity) context).finish();
    }

    /*********************************************   OCR   ******************************************/
    /**
     * 신분증촬영 모듈 호출
     *
     * @param webview
     * @param context
     * @param json    param = {"type : 카메라 타입 (일반 사진, 신분증, 차량 번호 인식"}
     * @throws JSONException
     */
    @JSApi(invokeMethod = "startOCR", explain = "카메라 솔루션 호출",
            param = {"type : 카메라 타입 (일반 사진, 신분증, 차량 번호 인식"})
    public void startOCR(WebView webview, Context context, JSONObject json) throws JSONException {
        ((BaseActivity) context).startOCR(json);
    }

    /*********************************************   OCR   ******************************************/
    /**
     * 전자서식 닫고 신분증촬영 모듈 호출
     *
     * @param webview
     * @param context
     * @param json    param = {"type : 카메라 타입 (일반 사진, 신분증, 차량 번호 인식"}
     * @throws JSONException
     */
    @JSApi(invokeMethod = "closeFormStartOCR", explain = "서식액티비티 종료 후 사진촬영 화면 시작",
            param = {"callback : native 작업 후 콜백 보낼 곳", "type : 사진 촬영 화면의 타입 (할부(inst), 메디컬(medi)"})
    public void closeFormStartOCR(WebView webview, Context context, JSONObject json) throws JSONException {
        // 얘만 예외적으로 json 구조가 달라서 아래왁 같이 꺼내서 보낸다.
        if (context instanceof SecondWebViewActivity) {
            ((SecondWebViewActivity) context).closeFormStartOCR(json.getJSONObject(JavaScriptBridge.PARAM));
        } else {
            // 무시
        }
    }



    /*********************************************   보안키패드   ******************************************/
    /**
     * 보안키패드 모듈 호출 한다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "showTransKey", explain = "보안키패드",
            param = {"json : 키패드 속성들.. (타입, 크기, 힌트, 최대 글자수 등"})
    public void showTransKey(WebView webview, Context context, JSONObject json) throws JSONException {
        Intent intent = TranskeyManager.getInstance().getIntentParam(context, json);
        ((BaseActivity) context).startActivityForResult(intent, Const.REQ_TRANSKEY);
    }



    /**
     * 로그인 화면 진입 후 버전 정보를 전달하기 위한 함수이다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "checkVersion", explain = "버전 정보 전달.", param = {"callback : 콜백 명칭"})
    public void checkVersion (WebView webview, Context context, JSONObject json) throws JSONException {
        ((MainActivity) context).checkVersion(json.getString(JavaScriptBridge.CALLBACK));
    }

    /*********************************************   MDM   ******************************************/
    @JSApi(invokeMethod = "doMdmLogin", explain = "대응계 로그인 성공 후 해당 아이디를 사용해서 Mdm 에 로그인 하도록 한다.",
            param = {"callback : mdm login 후 콜백 보낼 곳", "id : 로그인 아이디"})
    public void doMdmLogin(WebView webview, Context context, JSONObject json) throws JSONException {
        if (context instanceof MainActivity) {
            ((MainActivity) context).doMdmLogin(json.getJSONObject(JavaScriptBridge.PARAM), json.getString(JavaScriptBridge.CALLBACK));
        } else {
            // 무시
        }
    }

    /*********************************************   공통기능   ******************************************/
    /**
     * 웹뷰에 10분 타이머로 로그아웃 창이 떠있을 때 hardware back button 처리를 분기하기 위함.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "setPopup", explain = "웹뷰에 10분 타이머로 로그아웃 창이 떠있을 때 hardware back button 처리를 분기하기 위함.", param = {""})
    public void logout(WebView webview, Context context, JSONObject json) throws JSONException {
        ((BaseActivity) context).setIsPopup(json.getJSONObject(JavaScriptBridge.PARAM).getBoolean("show"));
    }

    /**
     * 10분 타이머 로그아웃 창에서 확인 버튼을 눌렀을 때 로그아웃 동작을 함.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "doLogout", explain = "", param = {"10분 타이머 로그아웃 창에서 확인 버튼을 눌렀을 때 로그아웃 동작을 함."})
    public void doLogout(WebView webview, Context context, JSONObject json) throws JSONException {
        ((BaseActivity) context).doLogout();
    }

    /**
     * 웹뷰 히스토리 클리어
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "historyClear", explain = "히스토리 클리어", param = {""})
    public void historyClear(WebView webview, Context context, JSONObject json) throws JSONException {
        webview.clearHistory();
    }

    /**
     * 웹뷰 뒤로가기 </br>
     * 웹뷰에서 하드웨어 백버튼을 제어하기 위해서</br>
     * 하드웨어 백버튼 클릭시 네이티브는 웹뷰의 onAndroidHardwareBackPressed를 호출 하고 </br>
     * 웹뷰 com.util.js 에서 @JSApi(invokeMethod = "onBackPressed") 를 호출하여 webview.goBack(); 을 호출하게 설계됨</br>
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "onBackPressed", explain = "웹뷰 백", param = {""})
    public void onBackPressed(WebView webview, Context context, JSONObject json) throws JSONException {
        if (webview.canGoBack()) {
            webview.goBack();
        }
    }

    /*********************************************   이미지 업로드   ******************************************/
    /**
     * 이미지 업로드를 한다.
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "imageUpload", explain = "할부 신청, 메디컬 신청 중에 찍은 신분증 사진을 가지고 있다가 신청 완료 후 업로드", param = {""})
    public void imageUpload(WebView webview, Context context, JSONObject json) throws JSONException {
        if (context instanceof MainActivity) {
            if (((MainActivity) context).getIDCard() != null) {
                IDCardImageUpload.getInstance().imageUpload(context, ((MainActivity) context).getIDCard(), json.getString(JavaScriptBridge.CALLBACK), webview);
            }
        } else {
            // 무시
        }
    }



}
