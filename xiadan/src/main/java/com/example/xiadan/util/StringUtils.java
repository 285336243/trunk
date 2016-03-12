package com.example.xiadan.util;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by wlanjie on 14/12/1.
 */
public class StringUtils {

    /**
     * 请求参数,加入token
     * @param maptest 传入map
     * @return 返回 map
     */
    public static Map<String, String> sorting(Map maptest){
        Collection<String> keyset= maptest.keySet();
        List<String> list = new ArrayList<String>(keyset);

        //对key键值按字典升序排序
        Collections.sort(list);
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i)+"="+maptest.get(list.get(i)));
        }
        String str = buffer.toString();
        String tokenPara=StringUtils.securityMd5(str);
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(maptest);
        map.put("token",tokenPara);
        return  map;
    }

    /**
     * MD5 加密
     */
    public static String securityMd5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toUpperCase();
    }

    public static String mapSortToString(final Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        Map<String, String> treeMap = new TreeMap<String, String>(map);
        Set set = treeMap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (!TextUtils.isEmpty((String) entry.getValue()) && !(entry.getValue() instanceof File)) {
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return builder.toString();
    }

    public static String mapSortToParams(final Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?");
        Map<String, String> treeMap = new TreeMap<String, String>(map);
        Set set = treeMap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (!TextUtils.isEmpty((String) entry.getValue()) && !(entry.getValue() instanceof File)) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return builder.toString();
    }

    public static Map<String, String> mapSortToMap(final Map<String, String> map) {
        if (map == null) return null;
        Map<String, String> temp = new TreeMap<String, String>();
        Map<String, String> treeMap = new TreeMap<String, String>(map);
        Set set = treeMap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (entry.getValue() != null && !"".equals(entry.getValue())) {
                temp.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return temp;
    }
}
