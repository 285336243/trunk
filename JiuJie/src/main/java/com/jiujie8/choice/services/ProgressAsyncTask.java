package com.jiujie8.choice.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;

import com.jiujie8.choice.R;
import com.jiujie8.choice.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class ProgressAsyncTask extends AsyncTask<Object, Integer, Object> {

	public static final String NOTIFICATION_DOWNLOAD_ERROR = "下载出错";
	public static final String NOTIFICATION_DOWNLOADING = "正在下载  ";
	public static final String NOTIFICATION_DOWNLOAD_DONE = "下载完成";
	public static final String NOTIFICATION_UNKOWN_ERROR = "下载出错,未知错误";
	public static final String NOTIFICATION_DOWENLOAD_SPEED_UNITS = " KB/S";
	public static final String NOTIFICATION_DOWENLOAD_FILESIZE_UNITS = " MB";

	public static final int NOTIFICATION_STATUS_FLAG_ERROR = -1;
	public static final int NOTIFICATION_STATUS_FLAG_NORMAL = 1;
	public static final int NOTIFICATION_STATUS_FLAG_DONE = 2;
	public static final int NOTIFICATION_STATUS_FLAG_UNKOWN_ERROR = 3;

	/** 更新间隔 */
	private static final long UPDATE_INTERVAL = 1000;
	/** 文件的下载地址 */
	private String fileUrl;
	/** 文件保存的路径 */
	private String fileSavePath;
	/** 文件名称 */
	private String fileName;

	private NotificationManager nm;
	private Notification notification;
	private Builder mBuilder;
	private int progress;
	private int fileSize;
	private String fileSizeM;
	// 通知栏标识
	private int notifyId;
	// 系统版本是否大于4.0
	private boolean isHighVersion;
	private Context context;

	/**
	 * 
	 * @param fileUrl
	 * @param fileSavePath
	 * @param fileName
	 * @param isHighVersion
	 *            系统版本是否大于4.0
	 * @param nm
	 * @param mBuilder
	 * @param notifyId
	 */
	public ProgressAsyncTask(Context context, String fileUrl,
                             String fileSavePath, String fileName, boolean isHighVersion,
                             NotificationManager nm, Builder mBuilder, int notifyId) {
		this.context = context;
		this.fileUrl = fileUrl;
		this.fileSavePath = fileSavePath;
		this.fileName = fileName;
		this.nm = nm;
		this.mBuilder = mBuilder;
		this.notifyId = notifyId;
		this.isHighVersion = isHighVersion;
	}

	/**
	 * 
	 * @param fileUrl
	 * @param fileSavePath
	 * @param fileName
	 * @param isGreatVersion
	 *            系统版本是否大于4.0
	 * @param nm
	 * @param notifyId
	 * @param notification
	 */
	public ProgressAsyncTask(Context context, String fileUrl,
                             String fileSavePath, String fileName, boolean isGreatVersion,
                             NotificationManager nm, int notifyId, Notification notification) {
		this(context, fileUrl, fileSavePath, fileName, isGreatVersion, nm,
				null, notifyId);
		this.notification = notification;
	}

    public String getUrlFileName(String fileUrl) {
        int index = fileUrl.lastIndexOf("/");
        if (index != -1) {
            String result = fileUrl.substring(index + 1);
            if (TextUtils.isEmpty(result)) {
                return StringUtils.securityMd5(fileUrl);
            }
            return result;
        } else {
            return StringUtils.securityMd5(fileUrl);
        }
    }

	@Override
	protected Object doInBackground(Object... params) {
		disableConnectionReuseIfNecessary();
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try {
			if (TextUtils.isEmpty(fileName)) {
				fileName = getUrlFileName(fileUrl);
			}
			final URL url = new URL(fileUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.connect();
			fileSize = urlConnection.getContentLength();
			fileSizeM = String.format(Locale.CHINA, "%.2f",
					(float) fileSize / 1024 / 1024);
			bis = new BufferedInputStream(urlConnection.getInputStream());
			File saveFile = new File(fileSavePath, fileName);
			fos = new FileOutputStream(saveFile);
			// version >=4.0
			if (isHighVersion) {
				downloadFile(bis, fos, isHighVersion);
				// 下载进度完成
				mBuilder.setContentText(getBuildNotificationContent(0,
						100 + "", fileSizeM + "", NOTIFICATION_STATUS_FLAG_DONE));
				mBuilder.setProgress(fileSize, fileSize, false);
				nm.notify(notifyId, mBuilder.build());
			} else {
				downloadFile(bis, fos, isHighVersion);
				// 下载完毕
				notifyLowVersionNotification(0, 100, fileSize,
						NOTIFICATION_STATUS_FLAG_DONE);
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			notifyNotification(0, NOTIFICATION_STATUS_FLAG_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			// 需要处理出错情况,没有网络不能显示通知的问题，
			// 下载错误
			notifyNotification(0, NOTIFICATION_STATUS_FLAG_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			// 未知错误
			notifyNotification(0, NOTIFICATION_STATUS_FLAG_UNKOWN_ERROR);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 文件下载
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param isGreatVersion
	 *            if version>=4 return true
	 * @throws java.io.IOException
	 */
	public void downloadFile(InputStream in, OutputStream out,
			boolean isGreatVersion) throws IOException {
		int len = 0;
		byte[] buffer = new byte[1024 * 2];
		int current = 0;
		int bytesInThreshold = 0;
		long updateDelta = 0;
		long updateStart = System.currentTimeMillis();
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
			out.flush();
			current += len;
			bytesInThreshold += len;
			// 因为Notification状态栏不能更新太过于频繁
			// 该算法有待优化
			// if (percent % 5 == 0) {
			// if (percent > random.nextInt(10)) {
			if (updateDelta > UPDATE_INTERVAL) {
				int percent = (current * 100) / fileSize;
				long downloadSpeed = bytesInThreshold / updateDelta;
				if (isGreatVersion) {
					updateProgress(current, percent, downloadSpeed);
				} else {
					// 更新进度
					publishProgress(current, percent, (int) downloadSpeed);
				}
				// reset data
				updateStart = System.currentTimeMillis();
				bytesInThreshold = 0;
			}
			updateDelta = System.currentTimeMillis() - updateStart;
		}
	}

	/**
	 * 更新进度
	 * 
	 * @param current
	 * @param percent
	 * @param downloadSpeed
	 */
	private void updateProgress(int current, int percent, long downloadSpeed) {
		mBuilder.setContentText(getBuildNotificationContent(downloadSpeed,
				percent, fileSizeM, NOTIFICATION_STATUS_FLAG_NORMAL));
		mBuilder.setProgress(fileSize, current, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(notifyId, notification);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		notifyNotification(100, NOTIFICATION_STATUS_FLAG_NORMAL);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		// 可以在此做一些下载的操作
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		progress = values[0];
		int percent = values[1];
		int speed = values[2];
		notifyLowVersionNotification(speed, percent, fileSize,
				NOTIFICATION_STATUS_FLAG_NORMAL);
	}

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * 获取通知栏内容
	 * 
	 * @param speed
	 *            下载速度
	 * @param percent
	 *            完成率
	 * @param fileSize
	 *            文件大小
	 * @param type
	 *            -1 is wrong;1 is normal ;2 is done; the other digit is unkown
	 *            error
	 * @return
	 */
	public String getBuildNotificationContent(Serializable speed,
			Serializable percent, String fileSize, int type) {
		StringBuilder builder = new StringBuilder();
		switch (type) {
		case NOTIFICATION_STATUS_FLAG_ERROR:// 下载出错
			builder.append(NOTIFICATION_DOWNLOAD_ERROR);
			break;
		case NOTIFICATION_STATUS_FLAG_NORMAL:// 正常
			builder.append(NOTIFICATION_DOWNLOADING).append(speed)
					.append(NOTIFICATION_DOWENLOAD_SPEED_UNITS);
			builder.append("            ").append(percent).append("%")
					.append("  ").append(fileSize)
					.append(NOTIFICATION_DOWENLOAD_FILESIZE_UNITS);
			break;
		case NOTIFICATION_STATUS_FLAG_DONE:// 完成
			builder.append(NOTIFICATION_DOWNLOAD_DONE);
			install();
			break;
		case NOTIFICATION_STATUS_FLAG_UNKOWN_ERROR:
			builder.append(NOTIFICATION_UNKOWN_ERROR);
			break;
		}
		return builder.toString();
	}

	/**
	 * 显示高版本的Notification
	 * 
	 * @param speed
	 *            下载速度
	 * @param percent
	 *            完成率
	 * @param fileSize
	 *            文件大小
	 * @param type
	 *            -1 is wrong;1 is normal ;2 is done; the other digit is unkown
	 *            error
	 * @return
	 */
	private void notifyHighVersionNotification(Serializable speed,
			Serializable percent, int fileSize, int type) {
		mBuilder.setContentText(getBuildNotificationContent(0, 0,
				TextUtils.isEmpty(fileSizeM) ? "0" : fileSizeM, type));
		mBuilder.setProgress(fileSize, 0, false);
		nm.notify(notifyId, mBuilder.build());
	}

	/**
	 * 显示低版本的Notification
	 * 
	 * @param speed
	 * @param percent
	 * @param type
	 *            -1 is wrong;1 is normal ;2 is done; the other digit is unkown
	 *            error
	 */
	private void notifyLowVersionNotification(Serializable speed,
			Serializable percent, int fileSize, int type) {
		switch (type) {
		case NOTIFICATION_STATUS_FLAG_ERROR:// 下载出错
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content,
					NOTIFICATION_DOWNLOAD_ERROR);
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content2, "");
			notification.contentView.setProgressBar(
					R.id.notification_progress_layout_pb, 0, 0, false);

			break;
		case NOTIFICATION_STATUS_FLAG_NORMAL:// 正在下载
			StringBuilder builder = new StringBuilder();
			builder.append(NOTIFICATION_DOWNLOADING).append(speed)
					.append(NOTIFICATION_DOWENLOAD_SPEED_UNITS);
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content,
					builder.toString());
			builder.delete(0, builder.length());
			builder.append(percent).append("%  ")
					.append(TextUtils.isEmpty(fileSizeM) ? "0" : fileSizeM)
					.append(NOTIFICATION_DOWENLOAD_FILESIZE_UNITS);
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content2,
					builder.toString());
			notification.contentView.setProgressBar(
					R.id.notification_progress_layout_pb, fileSize, progress,
					false);
			break;
		case NOTIFICATION_STATUS_FLAG_DONE:// 完成
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content,
					NOTIFICATION_DOWNLOAD_DONE);
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content2, "");
			notification.contentView.setProgressBar(
					R.id.notification_progress_layout_pb, 100, 100, false);
			install();
			break;
		case NOTIFICATION_STATUS_FLAG_UNKOWN_ERROR:// 位未错误
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content,
					NOTIFICATION_UNKOWN_ERROR);
			notification.contentView.setTextViewText(
					R.id.notification_progress_layout_tv_content2, "");
			notification.contentView.setProgressBar(
					R.id.notification_progress_layout_pb, 0, 0, false);
			break;
		}
		nm.notify(notifyId, notification);
	}

	/**
	 * 安装apk
	 * 
	 */
	private void install() {
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		installIntent.setDataAndType(
				Uri.fromFile(new File(fileSavePath + "/" + fileName)),
				"application/vnd.android.package-archive");
        context.startActivity(installIntent);
		((Service) context).stopSelf();
		nm.cancel(notifyId);
        ((Service) context).stopForeground(true);
	}

	/**
	 * 该方法不适合用于频繁的显示Notification
	 */
	private void notifyNotification(int fileSize, int type) {
		if (isHighVersion) {
			notifyHighVersionNotification(0, 0, fileSize, type);
		} else {
			notifyLowVersionNotification(0, 0, fileSize, type);
		}
	}

}
