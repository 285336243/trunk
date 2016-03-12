package feipai.qiangdan.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import feipai.qiangdan.core.Log;

/**
 *判断程序是否在前台
 */
public class ActivityIsRun {
    /**
     *
     * @param context 上下文
     * @return 是否在运行
     */
    public static boolean isApplicationBroughtToFront(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            Log.v("kan","topActivity ="+topActivity.getPackageName()+",context.getPac="+context.getPackageName());
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }
}
