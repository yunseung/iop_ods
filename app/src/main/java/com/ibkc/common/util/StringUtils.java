package com.ibkc.common.util;

import com.ibkc.common.Common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StringUtils 클래스
 */
public class StringUtils {

    /**
     * 빈 문자인지 체크
     *
     * @param charSequence 체크할 문자
     * @return 비어있다면 true, 아니면 false
     */
    public static boolean isEmpty(CharSequence charSequence) {

        return charSequence == null || charSequence.length() <= 0;

    }

    /**
     * 빈문자 && trim 체크
     *
     * @param string 체크할 문자
     * @return
     */
    public static boolean isEmptyTrim(String string) {

        return string == null || string.trim().length() <= 0;
    }

    /**
     * 바이트 길이
     *
     * @param string
     * @return
     */
    public static int getByteSize(String string) {

        return getByteSize(string, (char) 0);
    }

    /**
     * 바이트 길이
     *
     * @param string String 데이터
     * @param except 제외할 단어
     * @return
     */
    public static int getByteSize(String string, char except) {

        int size = 0;

        for (int i = 0; i < string.length(); i++) {
            if (except != 0 && string.charAt(i) == except)
                continue;

            if (string.charAt(i) > 127)
                size += 2;
            else
                size++;
        }

        return size;
    }

    /**
     * 같은 문자열인지 비교
     *
     * @param originString 원본 문자열
     * @param strings      비교할 문자열 리스트
     * @return 같으면 true, 다르면 false
     */
    public static boolean isEquals(String originString, String... strings) {

        if (originString == null || strings == null) {
            return false;
        }

        for (String string : strings) {
            if (!originString.equals(string)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 같은 문자열인지 비교(대소문자 구분 없이)
     *
     * @param originString 원본 문자열
     * @param strings      비교할 문자열 리스트
     * @return 같으면 true, 다르면 false
     */
    public static boolean isEqualsIgnoreCase(String originString, String... strings) {

        if (originString == null || strings == null) {
            return false;
        }

        for (String string : strings) {
            if (!originString.equalsIgnoreCase(string)) {
                return false;
            }
        }

        return true;
    }


    public static String sha256Encript(String string){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Common.printException(e);
        }
        if (md != null) {
            md.update(string.getBytes());
            byte byteData[] = md.digest();



            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } else {
            return null;
        }
    }

    /**
     * @param str
     * @return
     */
    public static boolean isNull(CharSequence str) {
        if (null == str)
            return true;
        return false;
    }

    /**
     * @param str
     * @return
     */
    public static boolean isTrimEmpty(String str) {
        if (isNull(str))
            return true;
        if (str.trim().length() <= 0)
            return true;
        return false;
    }

    /**
     * @param src
     * @param dest
     * @return
     */
    public static boolean equalDataNull(String src, String dest) {

        if (null == src || null == dest) {
            return false;
        }

        return src.equals(dest);
    }

    /**
     * 밀리세컨->해당 포맷으로 시간표시
     *
     * @param format 시간 포맷(ex. yyyy/MM/dd)
     * @param time   밀리세컨드
     * @return
     */
    public static String changeDate(String format, long time) {
        String newDate = null;
        Date orgDate = new Date(time);
        SimpleDateFormat newFormat = new SimpleDateFormat(format);
        newDate = newFormat.format(orgDate);
        return newDate;
    }


    /**
     * 깨진 URL 을 UTF-8 형식을 사용하여 decoding 한다.
     *
     * @param url
     * @param decodingType
     */

    public static String decodingUrl(String url, String decodingType) {
        try {
            return URLDecoder.decode(url, decodingType).toString();
        } catch (UnsupportedEncodingException e) {
            Common.printException(e);
            return null;
        }
    }

    /**
     * 깨진 URL 을 UTF-8 형식을 사용하여 decoding 한다.
     *
     * @param url
     * @param decodingType
     */

    public static String encodingUrl(String url, String decodingType) {
        try {
            return URLEncoder.encode(url, decodingType).toString();
        } catch (UnsupportedEncodingException e) {
            Common.printException(e);
            return null;
        }
    }
}





