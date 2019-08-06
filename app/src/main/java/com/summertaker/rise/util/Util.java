package com.summertaker.rise.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.summertaker.rise.common.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    public static String readFile(String fileName) {
        String data = "";
        File file = new File(BaseApplication.getDataPath(), fileName);
        if (file.exists()) {
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append('\n');
                }
                reader.close();
                data = builder.toString();
                //Log.e(TAG, "READ: " + file.getAbsolutePath());
                //Log.e(TAG, "DATA" + data);
            } catch (IOException e) {
                Log.e(TAG, "FILE: " + file.getAbsolutePath());
                Log.e(TAG, "ERROR: " + e.getLocalizedMessage());
            }
        } else {
            Log.e(TAG, "FILE: " + fileName);
            Log.e(TAG, "ERROR: file not exists.");
        }

        return data;
    }

    public static void writeFile(Context context, String fileName, String data) {
        String path = BaseApplication.getDataPath();
        File dir = new File(path);
        boolean success = true;
        if (!dir.exists()) {
            success = dir.mkdirs();
        }
        if (success) {
            File file = new File(path, fileName);
            if (file.isFile()) {
                success = file.delete();
                if (!success) {
                    String msg = "ERROR: file.delete() failed.";
                    Log.e(TAG, msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
            if (success) {
                try {
                    success = file.createNewFile();
                    if (success) {
                        FileOutputStream fOut = new FileOutputStream(file);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(data);
                        myOutWriter.close();
                        fOut.flush();
                        fOut.close();
                        //Log.e(TAG, "WRITE: " + file.getAbsolutePath());
                        //Log.e(TAG, "DATA: " + data);
                    } else {
                        String msg = "ERROR: file.createNewFile() failed.";
                        Log.e(TAG, msg);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "FILE: " + file.getAbsolutePath());
                    Log.e(TAG, "ERROR: " + e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            String msg = "ERROR: dir.mkdirs() failed.";
            Log.e(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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
