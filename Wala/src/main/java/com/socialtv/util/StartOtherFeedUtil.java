package com.socialtv.util;

import android.app.Activity;

import com.socialtv.core.Intents;
import com.socialtv.feed.OthersFeedActivity;

/**
 * Created by wlanjie on 14-7-9.
 */
public class StartOtherFeedUtil {

    public final static void startOtherFeed(final Activity activity, final String userId) {
        if (!userId.equals(LoginUtil.getUserId(activity))) {
            activity.startActivity(new Intents(activity, OthersFeedActivity.class)
                    .add(IConstant.USER_ID, userId).toIntent());
        }
    }
}
