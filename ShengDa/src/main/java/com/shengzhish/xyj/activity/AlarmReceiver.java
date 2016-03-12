package com.shengzhish.xyj.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.util.IConstant;

/**
 * Created by wlanjie on 14-6-10.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final static String SOON_TEXT = "%s的%s已经开始";
    private final static String TEXT = "%s的%s将在5分钟后开始";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String location = intent.getStringExtra(IConstant.ACTIVITY_LOCATION);
            String title = intent.getStringExtra(IConstant.ACTIVITY_TITLE);
            String isStart = intent.getStringExtra("start");
            int notificationId = intent.getIntExtra(IConstant.NOTIFICATION_ID, -1);
            if (!TextUtils.isEmpty(location) && !TextUtils.isEmpty(title)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String notificationText;
                if (TextUtils.isEmpty(isStart)) {
                    notificationText = String.format(TEXT, location, title);
                } else {
                    notificationText = String.format(SOON_TEXT, location, title);
                }
                Notification notification = new Notification(R.drawable.icon, notificationText, System.currentTimeMillis());
                Intent activityIntent = new Intent(context, ActivityDetailsActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
                notification.setLatestEventInfo(context, "提醒", notificationText, pendingIntent);

                manager.notify(notificationId, notification);
            }
        }
    }
}
