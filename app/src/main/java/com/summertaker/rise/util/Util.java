package com.summertaker.rise.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

    private static String TAG = "== Util";

    public static String getString(JSONObject json, String key) {
        try {
            String str = json.getString(key);
            if ("null".equals(str)) {
                str = "";
            }
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public static int getInt(JSONObject json, String key) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static boolean getBoolean(JSONObject json, String key) {
        try {
            return json.getBoolean(key);
        } catch (JSONException e) {
            return false;
        }
    }

    public static double getDouble(JSONObject json, String key) {
        try {
            return json.getDouble(key);
        } catch (JSONException e) {
            return 0.0;
        }
    }

    // 카카오 스탁 앱 실행가기
    public static void startKakaoStock(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.dunamu.stockplus");
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    // 카카오 스탁 앱 내부 액티비티 실행하기
    public static void startKakaoStockDeepLink(Context context, String code) {
        //startKakaoStockDeepLink(context, code, 0, 1); // marketIndex: 0=시세, 1=차트
        startKakaoStockDeepLink(context, code, 0, 0); // marketIndex: 0=시세, 0=호가
    }

    // 카카오 스탁 앱 내부 액티비티 실행하기
    public static void startKakaoStockDeepLink(Context context, String code, int tabIndex, int marketIndex) {
        // 카카오 스탁 - 내부 링크 (앱 화면 순서대로)
        // tabIndex: 0=시세, 1=뉴스/공시, 2=객장, 3=수익/노트, 4=종목정보
        // marketIndex: [시세] 0=호가, 1=차트, 2=체결, 3=일별, 4=거래원, 5=투자자

        // 카카오 스탁: 시세 > 호가
        String url = "stockplus://viewStock?code=A" + code + "&tabIndex=" + tabIndex + "&marketIndex=" + marketIndex;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
