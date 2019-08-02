package com.summertaker.rise.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.rise.data.Item;
import com.summertaker.rise.parser.NaverParser;
import com.summertaker.rise.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DataManager {

    private String TAG;

    private Context mContext;
    private SharedPreferences mPreferences;
    private String mDateFormatString = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat mSimpleDateFormat;

    private String mSiteId;

    private boolean mIsDataOnLoading = false;
    private int mUrlLoadCount = 0;
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Item> mItems = new ArrayList<>();

    public DataManager(Context context) {
        TAG = getClass().getSimpleName();
        mContext = context;
        mPreferences = context.getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE);
        mSimpleDateFormat = new SimpleDateFormat(mDateFormatString, Locale.getDefault());
    }

    /**
     * [네이버 금융 > 국내 > 상승, 하락, 급등, 급락] 가져오기
     */
    public interface FluctuationCallback {
        void onParse(int count);

        void onLoad(ArrayList<Item> items);
    }

    private FluctuationCallback mFluctuationCallback;

    public void setOnFlucLoaded(FluctuationCallback callback) {
        mFluctuationCallback = callback;
    }

    public void loadFluctuation(String siteId) {
        mSiteId = siteId;

        mItems.clear();
        mUrls.clear();

        if (siteId.equals(Config.KEY_FLUCTUATION_RISE)) { // 상승
            mUrls.add(Config.URL_NAVER_FLUCTUATION_RISE_LIST_KOSPI);
            mUrls.add(Config.URL_NAVER_FLUCTUATION_RISE_LIST_KOSDAQ);
        } else if (siteId.equals(Config.KEY_FLUCTUATION_JUMP)) { // 급등
            mUrls.add(Config.URL_NAVER_FLUCTUATION_JUMP_LIST_KOSPI);
            mUrls.add(Config.URL_NAVER_FLUCTUATION_JUMP_LIST_KOSDAQ);
        } else if (siteId.equals(Config.KEY_FLUCTUATION_FALL)) { // 하락
            mUrls.add(Config.URL_NAVER_FLUCTUATION_FALL_LIST_KOSPI);
            mUrls.add(Config.URL_NAVER_FLUCTUATION_FALL_LIST_KOSDAQ);
        } else if (siteId.equals(Config.KEY_FLUCTUATION_CRASH)) { // 급락
            mUrls.add(Config.URL_NAVER_FLUCTUATION_CRASH_LIST_KOSPI);
            mUrls.add(Config.URL_NAVER_FLUCTUATION_CRASH_LIST_KOSDAQ);
        }

        if (mUrls.size() > 0) {
            mUrlLoadCount = 0;
            requestFluctuation();
        } else {
            Toast.makeText(mContext, "Invalid Site ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestFluctuation() {
        String url = mUrls.get(mUrlLoadCount);
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseFluctuation(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                parseFluctuation("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, TAG);
    }

    private void parseFluctuation(String response) {
        NaverParser naverParser = new NaverParser();
        naverParser.parseFluctuation(response, mItems);

        mUrlLoadCount++;
        if (mUrlLoadCount < mUrls.size()) {
            mFluctuationCallback.onParse(mUrlLoadCount);
            requestFluctuation();
        } else {
            //Log.e(TAG, "Fluc: mItems.size() = " + mItems.size());

            // 등락률 정렬
            if (mSiteId.equals(Config.KEY_FLUCTUATION_RISE) || mSiteId.equals(Config.KEY_FLUCTUATION_JUMP)) { // 상승, 급등
                Collections.sort(mItems, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        if (a.getRof() < b.getRof()) {
                            return 1;
                        } else if (a.getRof() > b.getRof()) {
                            return -1;
                        }
                        return 0;
                    }
                });
            } else if (mSiteId.equals(Config.KEY_FLUCTUATION_FALL) || mSiteId.equals(Config.KEY_FLUCTUATION_CRASH)) { // 하락, 급락
                Collections.sort(mItems, new Comparator<Item>() {
                    @Override
                    public int compare(Item a, Item b) {
                        if (a.getRof() < b.getRof()) {
                            return -1;
                        } else if (a.getRof() > b.getRof()) {
                            return 1;
                        }
                        return 0;
                    }
                });
            }

            //writeCacheItems(mSiteId, mItems);
            mFluctuationCallback.onLoad(mItems);
        }
    }

    public String readPreferences(String key) {
        return mPreferences.getString(key, "");
    }

    public void writePreferences(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public ArrayList<Item> readFavorites() {
        //BaseApplication.getInstance().getFavoriteItems().clear();
        ArrayList<Item> items = new ArrayList<>();

        //String cacheData = Util.readFile(Config.PREFERENCE_PORTFOLIOS);
        String data = readPreferences(Config.PREFERENCE_FAVORITE);
        if (!data.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                //String cacheDate = jsonObject.getString("date");
                //Log.e(TAG, cacheDate);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Item item = new Item();
                    item.setCode(Util.getString(obj, "code"));
                    //Log.e(TAG, "- Favorite loaded: " + item.getCode());
                    items.add(item);
                    //BaseApplication.getInstance().getFavoriteItems().add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public void writeFavorites(ArrayList<Item> items) {
        try {
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();

            //for (Item item : BaseApplication.getInstance().getFavoriteItems()) {
            for (Item item : items) {
                if (!item.isFavorite()) {
                    continue;
                }
                JSONObject obj = new JSONObject();
                obj.put("code", item.getCode());
                //Log.e(TAG, "- Favorite added: " + item.getCode());
                array.put(obj);
            }
            //String date = Util.getToday(mDateFormatString);
            //json.put("date", date);
            json.put("data", array);
            //Log.e(TAG, date);
            //Log.e(TAG, json.toString());

            //Util.writeFile(mContext, Config.PREFERENCE_PORTFOLIOS, json.toString());
            writePreferences(Config.PREFERENCE_FAVORITE, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
