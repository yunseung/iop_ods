package com.ibkc.ods.vo;

import com.ibkc.CLog;
import com.ibkc.common.util.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by macpro on 2018. 7. 5..
 */

/**
 * 신분증 객체를 만들어주는 vo class.
 */
@SuppressWarnings("serial")
public class IDCard implements Serializable {
    private byte[] imageByteArray;
    private JSONObject mParam = null; //신분증 사진 촬영 용 파람

    public byte[] getImageByteArray() {
        byte[] b = new byte[imageByteArray.length];
        for (int i = 0; i < imageByteArray.length; i++) {
            b[i] = imageByteArray[i];
        }
        return b;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        byte[] b = new byte[imageByteArray.length];
        for (int i = 0; i < imageByteArray.length; i++) {
            b[i] = imageByteArray[i];
        }

        this.imageByteArray = b;
    }

    public JSONObject getParam() {
        return mParam;
    }

    public void setParam(JSONObject mParam) {
        this.mParam = mParam;
    }

    public JSONArray getUploadJson(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("contentBase64", CommonUtils.convertByteArrayToBase64(imageByteArray));
            object.put("sheetCode", Report.REPORT.신분증.code);
        } catch (JSONException e) {
            CLog.printException(e);
            object = null;
        }
        array.put(object);
        return array;
    }
}
