package com.mzs.guaji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.ScanResult;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.Log;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;

public class ScanLogoActivity extends GuaJiActivity {
    private static final String API_KEY = "fo8uegjw9puya49k3nfm";
    private static final String API_SECRET = "KwoUOOmZpY9XHH8M";
    private boolean compatible = false;
    private Scanner scanner = null;
    private AutoScannerSession session = null;
    private long last_sync = 0;
    private static final long DAY = DateUtils.DAY_IN_MILLIS;
	private int ScanOptions = Result.Type.IMAGE | Result.Type.EAN13 | Result.Type.DATAMATRIX;
	public static final String TAG = ScanLogoActivity.class.getSimpleName();
    private Context context = ScanLogoActivity.this;
    private LinearLayout mBackLayout;
    private ScanBroadcastReceiver mScanReceiver;
    private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
            .setPrettyPrinting().setVersion(1.0).create();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_logo_layout);
        compatible = Scanner.isCompatible();
        if (compatible) {
            mScanReceiver = new ScanBroadcastReceiver();
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(BroadcastActionUtil.IS_SCAN_ACTION);
            registerReceiver(mScanReceiver, mFilter);
            mBackLayout = (LinearLayout) findViewById(R.id.scan_back);
            mBackLayout.setOnClickListener(mBackClickListener);
            SurfaceView preview = (SurfaceView) findViewById(R.id.scan_surface);
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

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    AutoScannerSession.Listener mSessionListener = new AutoScannerSession.Listener() {
        @Override
        public void onResult(final Result result) {
            if (result != null) {
                if(LoginUtil.isLogin(context)) {
                    mApi.requestGetData(getScanResultRequest(result.getValue()), ScanResult.class, new Response.Listener<ScanResult>() {
                        @Override
                        public void onResponse(ScanResult response) {
                            if(response != null) {
                                if(response.getResponseCode() == 0) {
                                    if(response.getScan() != null) {
                                        if ("QUESTION".equals(response.getScan().getType())) {
                                            Intent mIntent = new Intent(context, ScanRankingActivity.class);
                                            mIntent.putExtra("code", result.getValue());
                                            startActivity(mIntent);
                                            finish();
                                        }else if ("BROWSER".equals(response.getScan().getType())) {
                                            JsonElement mElement = response.getScan().getParam();
                                            if(mElement != null) {
                                                SkipBrowser mBrowser = mGson.fromJson(mElement, SkipBrowser.class);
                                                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowser.getLink()));
                                                context.startActivity(mIntent);
                                                finish();
                                            }
                                        }else if ("WEB_VIEW".equals(response.getScan().getType())) {
                                            JsonElement mElement = response.getScan().getParam();
                                            if(mElement != null) {
                                                SkipWebView mWebView = mGson.fromJson(mElement, SkipWebView.class);
                                                Intent mIntent = new Intent(context, WebViewActivity.class);
                                                mIntent.putExtra("url", mWebView.getLink());
                                                context.startActivity(mIntent);
                                                finish();
                                            }
                                        }
                                    }else {
                                        session.resume();
                                    }
                                }else {
                                    session.resume();
                                    ToastUtil.showToast(context, response.getResponseMessage());
                                }
                            }
                        }
                    }, null);
                }else {
                    Intent mIntent = new Intent(context, LoginActivity.class);
                    mIntent.putExtra("scan", true);
                    mIntent.putExtra("code", result.getValue());
                    startActivity(mIntent);
                }
            }
        }

        @Override
        public void onCameraOpenFailed(Exception e) {
            // You should inform the user if this occurs!
            ToastUtil.showToast(context, "相机打开失败");
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
        if(mScanReceiver != null) {
            unregisterReceiver(mScanReceiver);
        }
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
            try {
                Log.d("Moodstocks SDK", "Sync succeeded ("+scanner.count()+" images)");
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSyncFailed(MoodstocksError e) {
            Log.d("Moodstocks SDK", "Sync error #"+e.getErrorCode()+": "+e.getMessage());
            switch (e.getErrorCode()) {
                case MoodstocksError.Code.ENOCONN:
                    //无网络连接
                    ToastUtil.showToast(context, "当前网络不稳定");
                    break;
                case MoodstocksError.Code.ETIMEOUT:
                    //网络超时
                    ToastUtil.showToast(context, "网络超时");
                    break;
                case MoodstocksError.Code.EIMG:
                    ToastUtil.showToast(context, "图片无法识别");
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
            Log.d("Moodstocks SDK", "Sync progressing: "+percent+"%");
        }
    };
    /**
     * 获取扫一扫成功的URL
     * @return
     */
    private String getScanResultRequest(final String code) {
        return DOMAIN + "scan/request.json" + "?code=" + code + "&platform=" + "ANDROID";
    }

    private class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent != null && BroadcastActionUtil.IS_SCAN_ACTION.equals(intent.getAction())) {
               final String code = intent.getStringExtra("code");
                mApi.requestGetData(getScanResultRequest(code), ScanResult.class, new Response.Listener<ScanResult>() {
                    @Override
                    public void onResponse(ScanResult response) {
                        if(response != null) {
                            if(response.getResponseCode() == 0) {
                                Intent mIntent = new Intent(context, ScanRankingActivity.class);
                                mIntent.putExtra("code", code);
                                startActivity(mIntent);
                                finish();
                            }else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }
                }, null);
            }
        }
    }
}
