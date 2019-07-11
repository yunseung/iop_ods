package com.ibkc.ods.network;

import android.content.Context;
import android.webkit.WebView;

import com.ibkc.CLog;
import com.ibkc.ods.Const;
import com.ibkc.ods.util.CProgressDialog;
import com.ibkc.ods.vo.IDCard;
import com.ibkc.ods.webview.JavascriptSender;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by macpro on 2018. 8. 30..
 */

/**
 * 할부 등 프로세스 진행 중에 찍은 신분증 사진을 가지고 있다가 ImageUpload 에 사용되는 class.
 * ib20 image server 와 통신.
 */
public class IDCardImageUpload {
    private static class LazyHolder {
        private static final IDCardImageUpload instance = new IDCardImageUpload();
    }

    public static IDCardImageUpload getInstance() {
        return IDCardImageUpload.LazyHolder.instance;
    }

    /**
     * 신분증 사진 업로드
     * @param context context
     * @param idCard 신분증 객체
     * @param callback 콜백 명칭
     * @param webView webView
     */
    public void imageUpload(final Context context, final IDCard idCard, final String callback, final WebView webView) {
        // 사진 촬영 목록과 사진 시트코드를 jsonObject 로 묶어서 jsp page 에 전송.
        // 신분증 사진은 한 장인데 Array 로 만든 이유는 AddReportEquipGridActivity 에서 사진 전송 할 때의 Json 구조와 일치시켜 달라는 web 단의 요구사항.
        JSONObject uploadJson = new JSONObject();
        try {
            uploadJson.put("data", idCard.getUploadJson());
            uploadJson.put("imgMngNo", idCard.getParam().getString("imgMngNo"));
            uploadJson.put("demdDstc", "upload");
            uploadJson.put("custMngNo", idCard.getParam().getString("custMngNo"));
        } catch (JSONException e) {
            CLog.printException(e);
            uploadJson = null;
        }

        if (uploadJson != null) {
            final android.app.AlertDialog alert = CProgressDialog.getInstance().showProgress(context);
            IB20Connector.getInstance().conn(context, IB20Connector.IMAGE_UPLOAD, uploadJson, new IB20Connector.IB20ConnectorCallbackListener() {
                @Override
                public void onSuccess(JSONObject body) throws JSONException {
                    alert.dismiss();
                    JSONObject param = new JSONObject();
                    try {
                        param.put(Const.RESULT, body);
                    } catch (JSONException e) {
                        CLog.printException(e);
                        param = null;
                    }

                    if (param != null) {
                        JavascriptSender.getInstance().callJavascriptFunc(webView, callback, param);
                    } else {
                        JavascriptSender.getInstance().callJavascriptFunc(webView, callback, "ib20 image upload success result is null");
                    }
                }

                @Override
                public void onErrorResponse(String errorMsg) {
                    alert.dismiss();
                    JavascriptSender.getInstance().callJavascriptFunc(webView, callback, errorMsg);
                }
            });
        } else {
            JavascriptSender.getInstance().callJavascriptFunc(webView, callback, "!!upload data is null!! ** please check native source **");
        }
    }
}
