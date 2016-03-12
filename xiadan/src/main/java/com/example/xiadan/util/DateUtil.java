package com.example.xiadan.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 51wanh on 2015/3/9.
 */
public class DateUtil {
    /**
     * 比较时间差
     *
     * @param DATE1  开始时间
     * @param DATE2 结束时间
     * @param interval 时间差
     * @return 结束时间 - 开始时间是否大于时间差，是返回true，否返回false
     */
    public static boolean compare_twodate(String DATE1, String DATE2, int interval) {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt2.getTime() - dt1.getTime() > interval * 60 * 1000) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 于当前时间比较
     *
     * @param date 输入时间
     * @return 是否满足要求
     */
    public static boolean compare_current(String date, int interval) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(date);
            //取件时间距离当前时间至少interval分钟
            if (dt1.getTime() - System.currentTimeMillis() > interval * 60 * 1000) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 比较两个日期是否是同一天
     * @param day1
     * @param day2
     * @return  是同一天返回 true，不是同同一天返回 false
     */
    public static boolean isSameDay(String day1, String day2) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = day1.substring(0,10);
        String ds2 = day2.substring(0,10);
        if (ds1.equals(ds2)) {
            return true;
        } else {
            return false;
        }
    }
}
