package com.mzs.guaji.util;

/**
 * Created by wlanjie on 13-12-18.
 */
public class ListViewLastItemVisibleUtil {
    public static boolean isLastItemVisible(long page ,long count ,long total) {
        if(page == ((total / count) + (total % count > 0 ? 1 : 0))) {
            return true;
        }else {
            return false;
        }
    }
}
