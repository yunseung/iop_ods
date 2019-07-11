package com.ibkc.product.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.ibkc.common.util.CommonUtils;
import com.ibkc.ods.webview.JavascriptSender;
import com.rosisit.idcardcapture.CameraActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by macpro on 2018. 8. 22..
 */

public class ResultIdCardMaskingTask extends AsyncTask<Intent, Void, JSONObject> {
    private byte[] mMaskingImage = null;

    public interface TaskCompleted {
        void onImageMaskingCompleted(byte[] maskingImage);
    }

    private TaskCompleted mListener = null;

    private WebView mWebView = null;
    private String mCallback = null;

    public ResultIdCardMaskingTask(WebView webView, String callback, TaskCompleted listener) {
        mWebView = webView;
        mCallback = callback;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Intent... data) {
        // 주민번호 마스킹 영역
        Rect rrnRect = data[0].getParcelableExtra(CameraActivity.DATA_RRN_RECT);

        Bitmap idCardBitmap = CommonUtils.convertByteArrayToBitmap(data[0].getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY));

        if (idCardBitmap != null && rrnRect != null) {
            Canvas c = new Canvas(idCardBitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            //주민번호 영역 마스킹
            c.drawRect(rrnRect, paint);

            mMaskingImage = CommonUtils.convertBitmapToByteArray(idCardBitmap);

            ArrayList<String> resultText = data[0].getStringArrayListExtra(CameraActivity.DATA_RESULT_TEXT);

            JSONObject obj = new JSONObject();

            try {
                obj.put("resultCode", CameraActivity.RETURN_OK);
                obj.put("idcdKnNm", resultText.get(0));
                obj.put("custKrnNm", resultText.get(1));
                obj.put("reNbrn", resultText.get(2));
                obj.put("IssDt", resultText.get(3));
                obj.put("DlNo", resultText.get(4));
                obj.put("DrlcSrno", resultText.get(8));
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        super.onPostExecute(obj);
        if (mWebView != null && mCallback != null) {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, obj);
        }
        // 주민등록번호 영역 마스킹 된 이미지를 callback 해준다.
        mListener.onImageMaskingCompleted(mMaskingImage);
    }
}
