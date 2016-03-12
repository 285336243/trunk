package com.mzs.guaji.util;

import android.content.Context;
import android.content.Intent;

import com.mzs.guaji.core.Intents;
import com.mzs.guaji.ui.OthersInformationActivity;
import com.mzs.guaji.ui.StarCenterActivity;

/**
 * Created by wlanjie on 14-1-8.
 */
public class SkipPersonalCenterUtil {

    public static void skipPersonalCenter(Context context, long userId) {
        if(userId != LoginUtil.getUserId(context)) {
            Intent mIntent = new Intent(context, OthersInformationActivity.class);
            mIntent.putExtra("userId", userId);
            context.startActivity(mIntent);
        }
    }

    public static void startPersonalCore(final Context context, final long userId, final String userRenderTo) {
//        if (userId != LoginUtil.getUserId(context)) {
            if ("USER".equals(userRenderTo)) {
                context.startActivity(new Intents(context, OthersInformationActivity.class).add("userId", userId).toIntent());
            } else if ("CELEBRITY".equals(userRenderTo)) {
                context.startActivity(new Intents(context, StarCenterActivity.class).add("userId", userId).toIntent());
            }
//        }
    }
}
