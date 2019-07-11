package com.ibkc.common.util.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
    /**
     * JSONObject Value 값을 가져온다
     *
     * @param job JSONOBJECT
     * @param get value name
     * @param def 실패시 초기값
     * @return
     */
    private static Object getValue(JSONObject job, String get, Object def) {
        if (get == null || job == null) return def;
        if (!job.has(get)) {
            return def;
        }
        try {
            if (job.get(get) != null) {
                return job.get(get);
            } else {
                return null;
            }
        } catch (JSONException e) {
            return def;
        }
    }

    public static JSONObject getJSONObject(JSONObject job, String get, JSONObject def) {
        return (JSONObject) getValue(job, get, def);
    }

    public static String getString(JSONObject job, String get, String def) {
        return (String) getValue(job, get, def);

    }

    public static int getInt(JSONObject job, String get, int def) {
        return (int) getValue(job, get, def);
    }

    public static long getLong(JSONObject job, String get, long def) {
        return (long) getValue(job, get, def);
    }

    public static double getDouble(JSONObject job, String get, double def) {
        return (double) getValue(job, get, def);
    }

    public static boolean getBoolean(JSONObject job, String get, boolean def) {
        return (boolean) getValue(job, get, def);
    }
}
