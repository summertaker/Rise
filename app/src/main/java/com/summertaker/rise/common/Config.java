package com.summertaker.rise.common;

import java.text.DecimalFormat;

public class Config {

    private final static String PACKAGE_NAME = "com.summertaker.rise";
    public final static String PREFERENCE_KEY = PACKAGE_NAME;
    public final static String PREFERENCE_RISE = "rise";
    public final static String PREFERENCE_FAVORITE = "favorite";

    public static String KEY_FLUCTUATION_RISE = "fluc_rise";
    public static String KEY_FLUCTUATION_JUMP = "fluc_jump";
    public static String KEY_FLUCTUATION_FALL = "fluc_fall";
    public static String KEY_FLUCTUATION_CRASH = "fluc_crash";

    public static String URL_NAVER_FLUCTUATION_RISE_LIST_KOSPI = "https://finance.naver.com/sise/sise_rise.nhn?sosok=0"; // 상승(코스피)
    public static String URL_NAVER_FLUCTUATION_RISE_LIST_KOSDAQ = "https://finance.naver.com/sise/sise_rise.nhn?sosok=1"; // 상승(코스닥)

    public static DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###,###");
    public static DecimalFormat SIGNED_FORMAT = new DecimalFormat("+#,###,###;-#");
    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("+#,##0.00;-#");
}
