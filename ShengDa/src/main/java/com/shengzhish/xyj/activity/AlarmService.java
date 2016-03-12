package com.shengzhish.xyj.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shengzhish.xyj.R;

/**
 * Created by wlanjie on 14-6-10.
 */
public class AlarmService extends Service {

    private final static String SOON_TEXT = "%s的%s已经开始";
    private final static String TEXT = "%s的%s将在5分钟后开始";
    private NotificationManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, TEXT,  System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActivityDetailsActivity.class), 0);
        notification.setLatestEventInfo(this, "notification", "services", pendingIntent);
        manager.notify(0, notification);
    }
}
