package com.ibkc.product.ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.cardcam.carnum.CarNumRcgn;
import com.ibkc.CLog;
import com.ibkc.ods.webview.JavascriptSender;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by macpro on 2018. 7. 16..
 */

public class ResultLprImageTask extends AsyncTask<byte[], Void, String> {
    private WebView mWebView = null;
    private final int[] cropArea = {0, 0, 0, 0};
    private final int[] status = new int[8];
    private String mCallbackName = null;

    public ResultLprImageTask(WebView webView, String callback) {
        mWebView = webView;
        mCallbackName = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(byte[]... bytes) {
        if (bytes == null) {
            // 실패
            return null;
        } else {
            if (bytes != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;

                Bitmap mCopyBitmap1 = byteArrayToBitmap(bytes[0]).copy(Bitmap.Config.ARGB_8888, true);

                Canvas c1 = new Canvas(mCopyBitmap1);
                Paint paint1 = new Paint();
                paint1.setStyle(Paint.Style.FILL);
                paint1.setColor(Color.BLACK);

                Rect carRect1 = new Rect(0, 0, mCopyBitmap1.getWidth(), mCopyBitmap1.getHeight() / 4);
                Rect carRect2 = new Rect(0, mCopyBitmap1.getHeight() * 3 / 4, mCopyBitmap1.getWidth(), mCopyBitmap1.getHeight());
                Rect carRect3 = new Rect(0, 0, mCopyBitmap1.getWidth() / 4, mCopyBitmap1.getHeight());
                Rect carRect4 = new Rect(mCopyBitmap1.getWidth() * 3 / 4, 0, mCopyBitmap1.getWidth(), mCopyBitmap1.getHeight());

                //차량번호 주변 영역 마스킹
                c1.drawRect(carRect1, paint1);
                c1.drawRect(carRect2, paint1);
                c1.drawRect(carRect3, paint1);
                c1.drawRect(carRect4, paint1);

                return CarNumRcgn.bitmapCarNumRcgn(mCopyBitmap1, cropArea, status);
            } else {
                // 실패
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(String lrpCarNum) {
        super.onPostExecute(lrpCarNum);

        try {
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallbackName, new JSONObject().put("carNo", lrpCarNum));
        } catch (JSONException e) {
            CLog.printException(e);
            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallbackName, "car number parsing error ** please check native source **");
        }

    }

    private Bitmap byteArrayToBitmap(byte[] mbyteArray) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(mbyteArray, 0, mbyteArray.length, options);
        return bitmap;
    }
}
