package com.summertaker.rise.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseParser {

    protected String TAG;

    public BaseParser() {
        TAG = this.getClass().getSimpleName();
    }

    protected String getCodeFromUrl(String url) {
        // /item/main.nhn?code=18064K
        //String code = "";
        //Pattern pattern = Pattern.compile("code=(\\d+)");
        //Matcher matcher = pattern.matcher(url);
        //while (matcher.find()) {
        //    code = matcher.group(1);
        //}
        //return code;
        String[] arr1 = url.split("code=");
        String[] arr2 = arr1[1].split("&");
        return arr2[0].replace(" ", "");
    }
}
