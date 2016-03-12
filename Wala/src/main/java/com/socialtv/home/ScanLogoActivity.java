package com.socialtv.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.SurfaceView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request;
import com.google.inject.Inject;
import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.Log;
import com.socialtv.core.ToastUtils;
import com.socialtv.home.entity.ScanLogo;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 扫描Logo类
 */
@ContentView(R.layout.scan_logo)
public class ScanLogoActivity extends DialogFragmentActivity {
    private static final String URL = "event/scan.json?code=%s";
    private static final String API_KEY = "fo8uegjw9puya49k3nfm";
    private static final String API_SECRET = "KwoUOOmZpY9XHH8M";
    private boolean compatible = false;
    private Scanner scanner = null;
    private AutoScannerSession session = null;
    private long last_sync = 0;
    private static final long DAY = DateUtils.DAY_IN_MILLIS;
	private int ScanOptions = Result.Type.IMAGE | Result.Type.EAN13 | Result.Type.DATAMATRIX;

    @Inject
    private Activity activity;

    @InjectView(R.id.scan_surface)
    private SurfaceView preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        compatible = Scanner.isCompatible();
        if (compatible) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setLogo(R.drawable.btn_back_tomato);
            actionBar.setTitle("扫一扫");
            actionBar.setDisplayHomeAsUpEnabled(true);
            try {
                scanner = Scanner.get();
                String path = Scanner.pathFromFilesDir(this, "scanner.db");
                scanner.open(path, API_KEY, API_SECRET);
                scanner.setSyncListener(mSyncListener);
                scanner.sync();
                session = new AutoScannerSession(this, scanner, mSessionListener, preview);
                session.setResultTypes(ScanOptions);
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    AutoScannerSession.Listener mSessionListener = new AutoScannerSession.Listener() {
        @Override
        public void onResult(final Result result) {
            if (result != null) {
                new AbstractRoboAsyncTask<ScanLogo>(activity){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected ScanLogo run(Object data) throws Exception {
                        GsonRequest<ScanLogo> request = new GsonRequest<ScanLogo>(Request.Method.GET, String.format(URL, result.getValue()));
                        request.setClazz(ScanLogo.class);
                        return (ScanLogo) HttpUtils.doRequest(request).result;
                    }

                    @Override
                    protected void onSuccess(ScanLogo scanLogo) throws Exception {
                        super.onSuccess(scanLogo);
                        if (scanLogo != null) {
                            if (scanLogo.getResponseCode() == 0) {
                                Scan scan = scanLogo.getScan();
                                if (scan != null && scan.getParam() != null) {
                                    if ("BROWSER".equals(scan.getType())) {
                                        activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, scan.getParam().getLink()).toIntent());
                                        finish();
                                    } else if ("WEB_VIEW".equals(scan.getType())) {
                                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scan.getParam().getLink()));
                                        activity.startActivity(mIntent);
                                        finish();
                                    }
                                }
                            } else {
                                ToastUtils.show(activity, scanLogo.getResponseMessage());
                            }
                        }
                    }
                }.execute();
            }

        }

        @Override
        public void onCameraOpenFailed(Exception e) {
            // You should inform the user if this occurs!
            ToastUtils.show(activity, "相机打开失败");
        }

        @Override
        public void onWarning(String debugMessage) {
            // Useful for debugging!
        }
    };

	@Override
	protected void onResume() {
		super.onResume();
        if(session != null) {
            session.start();
            session.resume();
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
        if (session != null)
		    session.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        if (compatible) {
            try {
                scanner.close();
                scanner.destroy();
                scanner = null;
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
	}

    Scanner.SyncListener mSyncListener = new Scanner.SyncListener() {

        @Override
        public void onSyncStart() {
            Log.d("Moodstocks SDK", "Sync will start.");
        }

        @Override
        public void onSyncComplete() {
        }

        @Override
        public void onSyncFailed(MoodstocksError e) {
            Log.d("Moodstocks SDK", "Sync error #"+e.getErrorCode()+": "+e.getMessage());
            switch (e.getErrorCode()) {
                case MoodstocksError.Code.ENOCONN:
                    //无网络连接
                    ToastUtils.show(activity, "当前网络不稳定");
                    break;
                case MoodstocksError.Code.ETIMEOUT:
                    //网络超时
                    ToastUtils.show(activity, "网络超时");
                    break;
                case MoodstocksError.Code.EIMG:
                    ToastUtils.show(activity, "图片无法识别");
                    break;
                case MoodstocksError.Code.SUCCESS:
                    break;
                default:
//                    ToastUtil.showToast(context, "系统错误");
                    break;
            }
        }

        @Override
        public void onSyncProgress(int total, int current) {
            int percent = (int) ((float) current / (float) total * 100);
            Log.d("Moodstocks SDK", "Sync progressing: " + percent + "%");
        }
    };

}
