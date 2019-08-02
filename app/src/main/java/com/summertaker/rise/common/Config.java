package com.summertaker.rise.common;

import java.text.DecimalFormat;

//import okhttp3.MediaType;

public class Config {

    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final static String PACKAGE_NAME = "com.summertaker.rise";
    public final static String PREFERENCE_KEY = PACKAGE_NAME;
    public final static String PREFERENCE_FAVORITE = "favorite";

    //public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5";
    //public static String USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

    public static String KEY_FLUCTUATION_RISE = "fluc_rise";
    public static String KEY_FLUCTUATION_JUMP = "fluc_jump";
    //public static String KEY_FLUC_CEILING = "fluc_ceiling";
    public static String KEY_FLUCTUATION_FALL = "fluc_fall";
    public static String KEY_FLUCTUATION_CRASH = "fluc_crash";
    //public static String KEY_FLUC_FLOOR = "fluc_floor";

    public static String URL_NAVER_FLUCTUATION_RISE_LIST_KOSPI = "https://finance.naver.com/sise/sise_rise.nhn?sosok=0"; // 상승(코스피)
    public static String URL_NAVER_FLUCTUATION_RISE_LIST_KOSDAQ = "https://finance.naver.com/sise/sise_rise.nhn?sosok=1"; // 상승(코스닥)
    public static String URL_NAVER_FLUCTUATION_JUMP_LIST_KOSPI = "https://finance.naver.com/sise/sise_low_up.nhn?sosok=0"; // 급등(코스피)
    public static String URL_NAVER_FLUCTUATION_JUMP_LIST_KOSDAQ = "https://finance.naver.com/sise/sise_low_up.nhn?sosok=1"; // 급등(코스닥)
    //public static String URL_NAVER_FLUCTUATION_CEILING_LIST = "https://finance.naver.com/sise/sise_upper.nhn"; // 상한가
    public static String URL_NAVER_FLUCTUATION_FALL_LIST_KOSPI = "https://finance.naver.com/sise/sise_fall.nhn?sosok=0"; // 하락(코스피)
    public static String URL_NAVER_FLUCTUATION_FALL_LIST_KOSDAQ = "https://finance.naver.com/sise/sise_fall.nhn?sosok=1"; // 하락(코스닥)
    public static String URL_NAVER_FLUCTUATION_CRASH_LIST_KOSPI = "https://finance.naver.com/sise/sise_high_down.nhn?sosok=0"; // 급락(코스피)
    public static String URL_NAVER_FLUCTUATION_CRASH_LIST_KOSDAQ = "https://finance.naver.com/sise/sise_high_down.nhn?sosok=1"; // 급락(코스닥)
    //public static String URL_NAVER_FLUCTUATION_FLOOR_LIST = "https://finance.naver.com/sise/sise_lower.nhn"; // 하한가

    public static DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###,###");
    public static DecimalFormat SIGNED_FORMAT = new DecimalFormat("+#,###,###;-#");
    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("+#,##0.00;-#");
}
