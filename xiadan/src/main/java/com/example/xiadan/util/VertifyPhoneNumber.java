package com.example.xiadan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 51wanh on 2015/3/9.
 */
public class VertifyPhoneNumber {
    /**
     * 验证手机号的正则表达式
     * @param mobiles  手机号
     * @return  是手机号 为true，不是为 否
     */
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
