package com.ibkc.ods.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ibkc.CLog;
import com.ibkc.common.util.network.NetRequestQueue;
import com.ibkc.ods.R;
import com.ibkc.ods.config.ProjectUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ImageUpload 에 사용되는 class.
 * ib20 image server 와 통신.
 */
public class IB20Connector {
    public static final String IMAGE_UPLOAD = "/ib20/act/COMIMGS001A001";
    public static final String GET_VERSION = "/ib20/act/SMPVERSION001A";

    private static class LazyHolder {
        private static final IB20Connector instance = new IB20Connector();
    }

    public static IB20Connector getInstance() {
        return LazyHolder.instance;
    }


    public interface IB20ConnectorCallbackListener {
        public void onSuccess(JSONObject body) throws JSONException;

        public void onErrorResponse(String errorMsg);
    }

    public void conn(final Context context, String url, final JSONObject reqJson, final IB20ConnectorCallbackListener listener) {
        ProjectUrl.initWebUrl(context);
        CLog.d("++ connect : " + ProjectUrl.connWebUrl + url);
        StringRequest strRequest = new StringRequest(Request.Method.POST, (ProjectUrl.connWebUrl + url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CLog.d(response.toString());
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONObject body = res.getJSONObject("_msg_").getJSONObject("_body_");
                            listener.onSuccess(body);
                        } catch (JSONException e) {
                            Toast.makeText(context, "서버 응답 데이터가 잘못되었습니다." ,Toast.LENGTH_LONG).show();
                            CLog.printException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = context.getString(R.string.ib20error);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            msg = "서버 응답이 없습니다.";
                        } else if (error instanceof AuthFailureError) {
                            msg = "서버 인증 실패입니다.";
                        } else if (error instanceof ServerError) {
                            msg = "서버 에러입니다.";
                        } else if (error instanceof NetworkError) {
                            msg = "서버 네트워크 에러입니다.";
                        } else if (error instanceof ParseError) {
                            msg = "서버 응답 에러입니다.";
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("param", reqJson.toString());
                return param;
            }
        };

        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000*60*2,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        NetRequestQueue.getInstance(context).addToRequestQueue(strRequest, strRequest);
    }


}
