package com.mzs.guaji.util;

import android.text.TextUtils;

public class StringUtil {

	public static String assemblyRequestUrl(String entity, int page, int count) {
		return entity+"?p="+page+"&cnt="+count;
	}
	
	public static String getUrlFileName(String fileUrl) {
		int index = fileUrl.lastIndexOf("/");
		if (index != -1) {
			String result = fileUrl.substring(index + 1);
			if (TextUtils.isEmpty(result)) {
				return MD5Util.getMD5Str(fileUrl);
			}
			return result;
		} else {
			return MD5Util.getMD5Str(fileUrl);
		}
	}

    public static String getShareText(String shareTitle, String shareMessage) {
        int textLength = shareTitle.length() + shareMessage.length();
        if(textLength < 140) {
            return shareTitle + shareMessage;
        }else {
            return (shareTitle + "---" + shareMessage).substring(0, 140) + "...";
        }
    }
}
