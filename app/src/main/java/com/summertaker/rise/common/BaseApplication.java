package com.summertaker.rise.common;

import android.app.Application;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.summertaker.rise.R;
import com.summertaker.rise.data.Item;

import java.util.ArrayList;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    public static String DATA_PATH;
    //public static boolean CACHE_MODE = true;

    public static int COLOR_INK;
    public static int COLOR_PRIMARY;
    public static int COLOR_DANGER;
    public static int COLOR_SUCCESS;
    public static int COLOR_INFO;
    public static int COLOR_WARNING;

    private ArrayList<Item> mFavoriteItems = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        COLOR_INK = ResourcesCompat.getColor(this.getResources(), R.color.ink, null);
        COLOR_PRIMARY = ResourcesCompat.getColor(this.getResources(), R.color.primary, null);
        COLOR_DANGER = ResourcesCompat.getColor(this.getResources(), R.color.danger, null);
        COLOR_SUCCESS = ResourcesCompat.getColor(this.getResources(), R.color.success, null);
        COLOR_INFO = ResourcesCompat.getColor(this.getResources(), R.color.info, null);
        COLOR_WARNING = ResourcesCompat.getColor(this.getResources(), R.color.warning, null);
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public static String getDataPath() {
        return DATA_PATH;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag_list if tag_list is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ArrayList<Item> getFavoriteItems() {
        return mFavoriteItems;
    }

    public void setFavoriteItems(ArrayList<Item> favoriteItems) {
        this.mFavoriteItems = favoriteItems;
    }

    // 현재가
    public void renderPrice(Item item, TextView tvPrice) {
        //if (item.getRof() > 0) {
        //    if (tvPrice != null) {
        //        tvPrice.setTextColor(COLOR_DANGER);
        //    }
        //} else if (item.getRof() < 0) {
        //    if (tvPrice != null) {
        //        tvPrice.setTextColor(COLOR_PRIMARY);
        //    }
        //} else {
        //    if (tvPrice != null) {
        //        tvPrice.setTextColor(COLOR_INK);
        //    }
        //}
        String price = Config.NUMBER_FORMAT.format(item.getPrice());
        if (tvPrice != null) {
            tvPrice.setText(price);
        }
    }

    // 등락률
    public void renderRof(Item item, TextView tvRof) {
        if (item.getRof() > 0) {
            if (tvRof != null) {
                tvRof.setTextColor(COLOR_DANGER);
            }
        } else if (item.getRof() < 0) {
            if (tvRof != null) {
                tvRof.setTextColor(COLOR_PRIMARY);
            }
        } else {
            if (tvRof != null) {
                tvRof.setTextColor(COLOR_INK);
            }
        }
        //String text = Config.DECIMAL_FORMAT.format(item.getRof() * 100) + "%";
        String text = item.getRof() + "%";
        if (tvRof != null) {
            tvRof.setText(text);
        }
    }
}
