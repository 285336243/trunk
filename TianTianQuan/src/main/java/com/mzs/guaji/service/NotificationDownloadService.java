package com.mzs.guaji.service;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mzs.guaji.R;
import com.mzs.guaji.ui.MainActivity;
import com.mzs.guaji.util.ProgressAsyncTask;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.Utils;

/**
 * notification 下载service
 * @author lenovo
 *
 */
public class NotificationDownloadService extends Service {

	public static final int NOTIFY_ID_PROGRESS = 1;
	private  NotificationManager nm;
	private static final String TITLE = "更新提示";
	private Context context = NotificationDownloadService.this;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            int id = intent.getIntExtra("id", -1);
            String url = intent.getStringExtra("url");
            notifyPtogressNotification(url, "/GuaJi/apks",id);
        }
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		nm = null;
		context = null;
	}

	/**
	 * 
	 * @param context
	 * @param url 下载apk的地址	
	 * @param saveNewApkPath 下载下来apk的保存路径 如: "/apks"
	 * @param notifyId
	 */
	public void notifyPtogressNotification(String url, String saveNewApkPath, int notifyId) {
		if (nm == null) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		final Builder mBuilder = new Builder(context);

		String savePath = getSavePath(context, saveNewApkPath);
		if (TextUtils.isEmpty(savePath)) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("market://details?id="
					+ context.getPackageName()));
			mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, i,
					PendingIntent.FLAG_UPDATE_CURRENT));
			mBuilder.setContentText("您的SD卡不可用,建议到市场更新本程序");
			mBuilder.setContentTitle("更新提示");
			setNotificationIcon(context, mBuilder);
			nm.notify(notifyId, mBuilder.build());
			return;
		}
		// 当前手机版本>=4.0
		if (Utils.hasIceCreamSandwich()) {
//			Intent intent = new Intent(context, MainActivity.class);
//			PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//			mBuilder.setContentIntent(pi);
			mBuilder.setContentTitle(TITLE).setContentText("下载进度：");
			setNotificationIcon(context, mBuilder);
			if(TextUtils.isEmpty(url)) {
				Toast.makeText(context, "下载地址出错了", Toast.LENGTH_SHORT).show();
			}else {
				new ProgressAsyncTask(context, url, savePath, null, true, nm, mBuilder,
						notifyId).execute();
			}
		} else {// 4.0以前版本
			// // 一定要设置Icon,否则不显示
			// setNotificationIcon(context, mBuilder);
			new ProgressAsyncTask(context, url, savePath, null, false, nm, notifyId,
					getNotification(context, TITLE)).execute();
		}
	}

	/**
	 * 设置Notification的icon,<br>
	 * 如果不设置图标则无法显示Notification,并且LargeIcon,SmallIcon都需要设置
	 * 
	 * @param context
	 * @param mBuilder
	 */
	private static void setNotificationIcon(Context context,
			final Builder mBuilder) {
		// 如果不设置小图标则无法显示Notification
		mBuilder.setLargeIcon(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.icon));
		mBuilder.setSmallIcon(R.drawable.icon);
	}

	/**
	 * 兼容4.0以下设备
	 * 
	 * @param context
	 * @param title
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Notification getNotification(Context context, String title) {
		Notification notification = new Notification(
				R.drawable.ic_launcher, "下载提醒",
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_progress_layout);
		notification.contentView.setProgressBar(
				R.id.notification_progress_layout_pb, 100, 0, false);
		notification.contentView.setTextViewText(
				R.id.notification_progress_layout_tv_title, title);
		notification.contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		return notification;
	}

	/**
	 * 获取文件保存目录
	 * 
	 * @return
	 */
	private static String getSavePath(Context context, String subDir) {
		// 判断SD卡是否存在
		boolean sdCardExist = StorageUtil.isExternalStorageAvailable();
		File file = null;
		if (sdCardExist) {
			file = Environment.getExternalStorageDirectory();
		} else {// 内存存储空间
			file = context.getFilesDir();
			return getPath(file, subDir);
		}
		return getPath(file, subDir);
	}

	private static String getPath(File f, String subDir) {
		File file = new File(f.getAbsolutePath() + subDir);
		if (!file.exists()) {
			boolean isSuccess = file.mkdirs();
		}
		return file.getAbsolutePath();
	}
}
