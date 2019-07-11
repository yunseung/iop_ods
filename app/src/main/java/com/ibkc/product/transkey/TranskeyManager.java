package com.ibkc.product.transkey;

import android.content.Context;
import android.content.Intent;

import com.ibkc.CLog;
import com.ibkc.common.jsbridge.JavaScriptBridge;


import com.ibkc.common.util.json.JSONUtil;
import com.ibkc.common.util.reflection.ReflectionUtil;
import com.ibkc.ods.activity.BaseActivity;

import com.softsecurity.transkey.TransKeyActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class TranskeyManager {
    private static class LazyHolder {
        private static final TranskeyManager instance = new TranskeyManager();
    }

    private static String randKey = null;

    public static TranskeyManager getInstance() {
        return LazyHolder.instance;
    }


    public TranskeyManager() {
//        if (randKey == null) { ods는 공인인증 랜덤키를 사용하지 않는다
//            randKey = MTransKeyXSafeRandomKey.makeSecureRandomKey();
//        }
    }
    public Intent getIntentParam(Context context, JSONObject json) {


        JSONObject reqJson = null;
        try {
            reqJson = json.getJSONObject(JavaScriptBridge.PARAM);
        } catch (JSONException e) {
            CLog.printException(e);
            return null;
        }
        // 콜백 설정
        String mCallBackFunc = JSONUtil.getString(json, "callback", "");
        ((BaseActivity)context).mTransKeyCallbackFunc = mCallBackFunc;

        //키패드 타입
        int keyPadType = (int) ReflectionUtil.getMemberFiled(TransKeyActivity.class, JSONUtil.getString(reqJson, "keyPadType", "1"));
        //키보드 입력 타입
        int textType = (int) ReflectionUtil.getMemberFiled(TransKeyActivity.class, JSONUtil.getString(reqJson, "textType", "1"));
        //키패드입력필드의 입력 라벨
        String label = JSONUtil.getString(reqJson, "label", "");
        //입력필드 힌트 텍스트
        String hint = JSONUtil.getString(reqJson, "hint", "");
        //최대 입력길이값
        int maxLength =  JSONUtil.getInt(reqJson, "maxLength", 20);
        //최대 입력길이 초과시 메세지
        String maxLengthMessage =  JSONUtil.getString(reqJson, "maxLengthMessage", "");
        //최소 입력값
        int minLength =  JSONUtil.getInt(reqJson, "minLength", 0);
        //최소 입력길이 초과시 메세지
        String minLengthMessage =  JSONUtil.getString(reqJson, "minLengthMessage", "");
        //쿼티형 키패드에서 입력완료 버튼과 삭제 버튼의 간격
        int line3Padding =  JSONUtil.getInt(reqJson, "line3Padding", 0);
        //에디트 박스안의 글자 크기조절값
        int reduceRate = (int) JSONUtil.getInt(reqJson, "reduceRate", 0);

        boolean isCert =  JSONUtil.getBoolean(reqJson, "isCert", false);


        Intent newIntent = new Intent(context.getApplicationContext(), TransKeyActivity.class);

        newIntent.putExtra(TransKeyActivity.mTK_PARAM_USE_CUSTOM_DUMMY, true);

        /**
         * 키패드 타입
         * TransKeyActivity.mTK_TYPE_KEYPAD_NUMBER			:	숫자전용
         * TransKeyActivity.mTK_TYPE_KEYPAD_QWERTY_LOWER	:	소문자 쿼티
         * TransKeyActivity.mTK_TYPE_KEYPAD_QWERTY_UPPER	:	대문자 쿼티
         * TransKeyActivity.mTK_TYPE_KEYPAD_ABCD_LOWER		:	소문자 순열자판
         * TransKeyActivity.mTK_TYPE_KEYPAD_ABCD_UPPER		:	대문자 순열자판
         * TransKeyActivity.mTK_TYPE_KEYPAD_SYMBOL			:	심벌자판
         */
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_KEYPAD_TYPE, keyPadType);

        /**
         * 키보드가 입력되는 형태
         * TransKeyActivity.mTK_TYPE_TEXT_IMAGE 				:	보안 텍스트 입력
         * TransKeyActivity.mTK_TYPE_TEXT_PASSWORD 			:	패스워드 입력
         * TransKeyActivity.mTK_TYPE_TEXT_PASSWORD_EX 			:	마지막 글자 보여주는 패스워드 입력
         * mTK_TYPE_TEXT_PASSWORD_IMAGE                        :   Text Area에 이미지 * 표시
         * TransKeyActivity.mTK_TYPE_TEXT_PASSWORD_LAST_IMAGE	:	마지막 글자를 제외한 나머지를 *표시.
         */
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_INPUT_TYPE, textType);


        //공인인증서 로그인시 이니텍 + 라온 암호화 적용함
//        if (isCert) {
//            CLog.d("++ 공인인증서 로그인시 이니텍 + 라온 암호화 적용함");
//
//            MTransKeyXSafeRandomKey.setSecureRandomKey(randKey);
//            byte[] pbkdfKey = randKey.getBytes();
//            byte[] PBKDF2_SALT = {1, 2, 1, 6, 0, 1, 0, 1, 8, 1, 3, 7, 0, 6, 4, 2, 3, 1, 0, 1};
//            int PBKDF2_IT = 1024;
//            newIntent.putExtra(TransKeyActivity.mTK_PARAM_CRYPT_TYPE, TransKeyActivity.mTK_TYPE_CRYPT_SERVER);
//            newIntent.putExtra(TransKeyActivity.mTK_PARAM_PBKDF2_RANDKEY, pbkdfKey);
//            newIntent.putExtra(TransKeyActivity.mTK_PARAM_PBKDF2_SALT, PBKDF2_SALT);
//            newIntent.putExtra(TransKeyActivity.mTK_PARAM_PBKDF2_IT, PBKDF2_IT);
//
//        } else {
            newIntent.putExtra(TransKeyActivity.mTK_PARAM_CRYPT_TYPE, TransKeyActivity.mTK_TYPE_CRYPT_LOCAL);
//        }


        newIntent.putExtra(TransKeyActivity.mTK_PARAM_NAME_LABEL, label);//	키패드입력화면의 입력필드 라벨
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_DISABLE_SPACE, false);//	입력필드에 스페이스바 입력을 무시

        /**
         * 최대 입력 길이 설정 ( Default : 설정이 없을 경우 16글자까지 입력가능하도록 설정된다.)
         * 암호화한 데이터의 경우 보안성 강화 적용된 데이터의 경우 입력 글자수에 상관없이 최대 입력 길이 설정값만큼
         * 항상 데이터가 채워져서 암호화 데이터가 적용되므로 사용목적에 따라 적당한 값을 사용하는 것을 권장.
         */


        newIntent.putExtra(TransKeyActivity.mTK_PARAM_INPUT_MAXLENGTH, maxLength);//Maxlength 설정
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_MAX_LENGTH_MESSAGE, maxLengthMessage);//maxLength 설정된 값보다 길이가 초과할 경우 보여줄 메세지 설정.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_INPUT_MINLENGTH, minLength);// Minlength 설정
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_MIN_LENGTH_MESSAGE, minLengthMessage);//minLength 설정된 값보다 길이가 미만일 경우 보여줄 메세지 설정.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_SET_HINT, hint);//입력 필드에 보여지는 힌트를 설정.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_SET_HINT_TEXT_SIZE, 0);//	Hint 텍스트 사이즈를 설정.(단위 dip, 0이면 디폴트 크기로 보여준다.)
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_SHOW_CURSOR, true);//커서를 보여준다.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_EDIT_CHAR_REDUCE_RATE, reduceRate);//	에디트 박스안의 글자 크기를 조절한다.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_DISABLE_SYMBOL, false);//	심볼 변환 버튼을 비활성화 시킨다.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_DISABLE_SYMBOL_MESSAGE, "심볼키는 사용할 수 없습니다.");//심볼 변환 버튼을 비활성화 시킬 경우 팝업 메시지를 설정한다.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_NUMPAD_USE_CANCEL_BUTTON, false);// 숫자키패드에 '취소' 버튼 추가여부
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_KEYPAD_MARGIN, line3Padding);
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_USE_TALKBACK, false);//TalkBack 설정 시 음성이 나올 수 있도록 설정.
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_USE_SHIFT_OPTION, false);//SHIFT고정 옵션 설정
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_USE_CLEAR_BUTTON, false);// 전체삭제 X버튼 사용여부 설정
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_USE_NAVIBAR, false);//네비바 사용여부를 설정
        newIntent.putExtra(TransKeyActivity.mTK_PARAM_KEYPAD_HIGHEST_TOP_MARGIN, 4);

        return newIntent;
    }

    public static byte[] toByteArray(String hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character
                    .digit(hexStr.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * byte[]를 hex문자열로 변환하는 함수
     *
     * @return hexstring
     */
    public static String toHexString(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte val : buf) {
            sb.append(Integer.toHexString(0x0100 + (val & 0x00FF)).substring(1));
        }
        return sb.toString();
    }

    /**
     * securerandomkey 생성
     *
     * @return hexString
     */
    private static String makeSecureRandomKey() {
        byte[] securekey = new byte[10];
        String seckey = "";
        for (int i = 0; i < 10; i++) {
            securekey[i] = (byte) ((int) (Math.random() * 128.0));
        }
        seckey = toHexString(securekey);
        return seckey;
    }


}
