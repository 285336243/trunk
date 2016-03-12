package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.DistributeAPI;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Splash;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends GuaJiActivity {

	private Context context = SplashActivity.this;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private File mSplashFile = null;

    /**
     * 是否请求网络下载loading视频,0为不请求,1为请求
     */
    private long isDownloadVideo = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.splash_activity);
//		DistributeAPI.activateDevice(this);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Object object = applicationInfo.metaData.get("TIANTIANQUAN_CHANNEL");
            isDownloadVideo = Long.parseLong(applicationInfo.metaData.get("SYNC_LOADING_VIDEO").toString());
            if (object != null) {
                String marketHouseId = object.toString();
                if (marketHouseId.length() == 1) {
                    marketHouseId = "0" + marketHouseId;
                }
                DistributeAPI.activateDevice(this, marketHouseId, getPackageName(), versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (hasShortcut() != false) {
            addShortcut();
        }
        if(Utils.hasGingerbread()) {
            Intent mIntent = new Intent(context, MainActivity.class);
            startActivity(mIntent);
        }else {
            mSurfaceView = (SurfaceView) findViewById(R.id.splash_surface);
            mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mSurfaceView.getHolder().addCallback(callback);
        }

        if (isDownloadVideo == 0) {
            return;
        }

        mApi.requestGetData(getSplashRequest(), Splash.class, new Response.Listener<Splash>() {
            @Override
            public void onResponse(Splash response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        String videoUrl = response.getLink();
                        String videoName = videoUrl.substring(videoUrl.lastIndexOf("/"), videoUrl.length());
                        if(videoName != null && !"".equals(videoName)) {
                            if(!videoName.equals(mRepository.getString(LoginUtil.SPLASHVIDEO, LoginUtil.SPLASHVIDEO_NAME))) {
                                startDownloadSplashVideo(response.getLink());
                            }
                        }
                    }
                }
            }
        }, null);
	}

    private void showFirstOpenApp() {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.dialog_view);
        mDialog.setCanceledOnTouchOutside(false);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
        Button updateButton = (Button) mDialog.findViewById(R.id.update_dialog_ok);
        updateButton.setText("我知道了");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 16;
        params.leftMargin = 16;
        updateButton.setLayoutParams(params);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepository.putLong(LoginUtil.CONFIG, "first_open_app", 0L);
                mMediaPlayer.start();
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        Button cancelButton = (Button) mDialog.findViewById(R.id.update_dialog_cancel);
        cancelButton.setVisibility(View.GONE);
        mDialog.findViewById(R.id.dialog_title).setVisibility(View.GONE);
        TextView mVersionNoText = (TextView) mDialog.findViewById(R.id.update_version);
        mVersionNoText.setText("提示");
        mVersionNoText.setTextSize(24);
        TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
        mUpdateMessageText.setText("欢迎使用天天圈，天天圈在使用过程中会产生流量，建议在Wifi环境下使用。");
    }

    private void startDownloadSplashVideo(final String videoUrl) {
        Thread mThread = new Thread(){
            @Override
            public void run() {
                super.run();
                InputStream mInputStream = null;
                FileOutputStream mOutputStream = null;
                try {
                    URL mURL = new URL(videoUrl);
                    URLConnection mConnection = mURL.openConnection();
                    mConnection.connect();
                    mInputStream = mConnection.getInputStream();
                    long size = mConnection.getContentLength();
                    if(size > 0) {
                        String videoName = videoUrl.substring(videoUrl.lastIndexOf("/"), videoUrl.length());
                        mSplashFile = new File(StorageUtil.getSDCardPath()+videoName);
                        mOutputStream = new FileOutputStream(mSplashFile);
                        byte[] buffer = new byte[1024];
                        int length = -1;
                        while((length = mInputStream.read(buffer)) != -1) {
                            mOutputStream.write(buffer, 0, length);
                        }
                        mRepository.putString(LoginUtil.SPLASHVIDEO, LoginUtil.SPLASHVIDEO_NAME, videoName);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try{
                        if(mOutputStream != null) {
                            mOutputStream.close();
                        }
                        if(mInputStream != null) {
                            mInputStream.close();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            long firstOpneApp = mRepository.getLong(LoginUtil.CONFIG, "first_open_app");
            if (firstOpneApp == 0) {
                mMediaPlayer.start();
            } else {
                showFirstOpenApp();
            }
        }
    };

    MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
             mMediaPlayer.stop();
            Intent mIntent = new Intent(context, MainActivity.class);
            startActivity(mIntent);
            overridePendingTransition(R.anim.splash_in,R.anim.splash_out);
        }
    };

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(final SurfaceHolder surfaceHolder) {
            mSplashFile = StorageUtil.getFilePath(mRepository.getString(LoginUtil.SPLASHVIDEO, LoginUtil.SPLASHVIDEO_NAME));
            if(mSplashFile != null) {
                mMediaPlayer = new MediaPlayer();
                try {
                    if(mMediaPlayer != null) {
                        mMediaPlayer.setDisplay(surfaceHolder);
                        if(mSplashFile != null) {
                            mMediaPlayer.setDataSource(mSplashFile.getAbsolutePath());
                            mMediaPlayer.prepare();
                            mMediaPlayer.setOnCompletionListener(mCompletionListener);
                            mMediaPlayer.setOnPreparedListener(mPreparedListener);
                        }
                    }else {
                        Intent mIntent = new Intent(context, MainActivity.class);
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.splash_in,R.anim.splash_out);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                mMediaPlayer = MediaPlayer.create(context, R.raw.splash);
                if(mMediaPlayer != null) {
                    long firstOpneApp = mRepository.getLong(LoginUtil.CONFIG, "first_open_app");
                    if (firstOpneApp == 0) {
                        mMediaPlayer.start();
                    } else {
                        showFirstOpenApp();
                    }
                    mMediaPlayer.setDisplay(surfaceHolder);
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }else {
                    Intent mIntent = new Intent(context, MainActivity.class);
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.splash_in,R.anim.splash_out);
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };
	
	/**
	 * 添加快捷方式
	 */
	private void addShortcut() {

		// //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// //注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		// ComponentName comp = new ComponentName(this.getPackageName(),
		// "."+this.getLocalClassName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
		// Intent(Intent.ACTION_MAIN).setComponent(comp));

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		Intent shortcutIntent = getPackageManager().getLaunchIntentForPackage(
				getPackageName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		String title = null;
		try {
			final PackageManager pm = getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}
		// 快捷方式名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 不允许重复创建（不一定有效）
		shortcut.putExtra("duplicate", false);
		// 快捷方式的图标
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

		sendBroadcast(shortcut);
	}

	/**
	 * 判断是否添加过快捷方式
	 * 
	 * @return
	 */
	public boolean hasShortcut() {
		boolean result = false;
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}

		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		final Cursor c = getContentResolver().query(CONTENT_URI, null,
				"title=?", new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		return result;
	}

    @Override
    protected void onPause() {
        super.onPause();
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            Intent mIntent = new Intent(context, MainActivity.class);
            startActivity(mIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private String getSplashRequest() {
        return DOMAIN + "system/load_video.json";
    }

//    public void createShut() {
//        // 创建添加快捷方式的Intent
//        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        String title = getResources().getString(R.string.app_name);
//        // 加载快捷方式的图标
//        Parcelable icon = Intent.ShortcutIconResource.fromContext(SplashActivity.this, R.drawable.icon);
//        // 创建点击快捷方式后操作Intent,该处当点击创建的快捷方式后，再次启动该程序
//        Intent myIntent = new Intent(SplashActivity.this,SplashActivity.class);
//        // 设置快捷方式的标题
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
//        // 设置快捷方式的图标
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
//        // 设置快捷方式对应的Intent
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
//        // 发送广播添加快捷方式
//        sendBroadcast(addIntent);
//    }
}
