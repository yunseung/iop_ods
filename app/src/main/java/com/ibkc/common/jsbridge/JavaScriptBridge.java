package com.ibkc.common.jsbridge;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ibkc.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 네이티브 기능을 수행하기 위한 자바스크립트 인터페이스 메소드 html -> Android 연결 객체 </br>
 * api 파라미터에 들어온 값을 </br></br>
 * <p>
 * JavascriptAPI.class 파일 안에 JSBridge annotaion{invokeMethod}으로 설정된 api와 이름이 같은 메소드를 실행한다.</br></br>
 * <p>
 * 호출방법 html 에서 Mobilebridge.invoke(Json)으로 호출  </br>
 * var obj = {  api: "importCert",                  </br>
 * param: {                            </br>
 * authCode: "1234"                   </br>
 * },                                  </br>
 * callback: "importCertCallback"      </br>
 * }                                      </br>
 * Mobilebridge.invoke(obj);                        </br>
 */
public class JavaScriptBridge {
    public static final String CALL_NAME = "android";
    public static final String API = "api";
    public static final String PARAM = "param";
    public static final String CALLBACK = "callback";
    public static final String RESULTCODE = "resultCode";


    private Context mContext;
    private Handler mUiHandler;
    private WebView mWebView;
    private Class<? extends JSBridge> javaScriptApiClass;

    public JavaScriptBridge(Context context, WebView webView, Class<? extends JSBridge> javaScriptApiClass) {
        mContext = context;
        mWebView = webView;
        mUiHandler = new Handler();
        this.javaScriptApiClass = javaScriptApiClass;
    }

    /**
     * 네이티브 기능을 수행하기 위한 자바스크립트 인터페이스 메소드
     *
     * @param jsonString json 문자열
     */
    @JavascriptInterface
    public void invoke(final String jsonString) {
        mUiHandler.post(new Runnable() {
            public void run() {
                try {
                    final JSONObject json = new JSONObject(jsonString);

                    //JavascriptAPI.class 파일 안에 JSBridge annotaion{invokeMethod}에 설정된 api와 이름이 같은 메소드를 실행한다.
                    final Method[] methods = javaScriptApiClass.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Annotation annotation = (Annotation) methods[i].getAnnotation(JSBridge.JSApi.class);
                        if (annotation instanceof JSBridge.JSApi) {
                            JSBridge.JSApi bridge = (JSBridge.JSApi) annotation;
                            if (json.getString(JavaScriptBridge.API).equals(bridge.invokeMethod())) {
                                if (Common.debug)
                                    Log.d("JavaScriptBridge", " ++실행: " + bridge.invokeMethod() + "() 설명:" + bridge.explain() + " param:" + Arrays.toString(bridge.param()));
                                methods[i].invoke(javaScriptApiClass.newInstance(), mWebView, mContext, json);
                                return;
                            }
                        }
                    }
                    Toast.makeText(mContext, "Not Found appBridge", Toast.LENGTH_LONG).show();
                } catch (IllegalAccessException e) {
                    Common.printException(e);
                    Toast.makeText(mContext, "잘못된 appBridge 호출 입니다.", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(mContext, "잘못된 파라미터 호출 입니다.", Toast.LENGTH_LONG).show();
                    Common.printException(e);
                } catch (InvocationTargetException e) {
                    Toast.makeText(mContext, "잘못된 기능 호출 입니다.", Toast.LENGTH_LONG).show();
                    Common.printException(e);
                } catch (RuntimeException e) {
                    Toast.makeText(mContext, "잘못된 실행 호출 입니다.", Toast.LENGTH_LONG).show();
                    Common.printException(e);
                } catch (InstantiationException e) {
                    Toast.makeText(mContext, "잘못된 호출 입니다.", Toast.LENGTH_LONG).show();
                    Common.printException(e);
                }
            }
        });
    }
}


