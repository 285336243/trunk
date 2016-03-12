package com.socialtv;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.view.View;

import com.socialtv.util.AppManager;
import com.socialtv.util.IConstant;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler crashHandler;
    private UncaughtExceptionHandler mDefaultHandler;
	
	public synchronized static CrashHandler getInstance() {
		if(crashHandler == null) {
			crashHandler = new CrashHandler();
		}
		return crashHandler;
	}
	
	public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
        if(!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
	}


    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if(ex == null) {
            return false;
        }
        final Context context = AppManager.getAppManager().currentActivity();
        if(context == null) {
            return false;
        }
        new Thread() {
            public void run() {
                Looper.prepare();
                sendAppCrashReport(context, ex);
                Looper.loop();
            }

        }.start();

        return true;
    }

    /**
     * 发送App异常崩溃报告

     */
    public  void sendAppCrashReport(final Context context, final Throwable ex) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.crash_dialog);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        View updateButton = mDialog.findViewById(R.id.crash_dialog_cancel);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                sendErrorInfo(ex, context);
            }
        });
        View cancelButton = mDialog.findViewById(R.id.crash_dialog_submit);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                sendErrorInfo(ex, context);
            }
        });
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void sendErrorInfo(Throwable ex, final Context context) {
        StringWriter writer = null;
        PrintWriter printWriter = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(new Date());
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            final String errorLog = date + "\n" + writer.toString();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            final String clientVersion = packageInfo.versionName;
            final String phoneModel = Build.MODEL;
            final String phonePlatform = Build.MANUFACTURER + " $ " + Build.VERSION.RELEASE;
            final String fingerPrint = Build.FINGERPRINT;
            final String phoneCpu = Build.CPU_ABI;
//            try {
//                File file = new File(Environment.getExternalStorageDirectory()+"/GuaJi_Crash_info_"+System.currentTimeMillis()+".txt");
//                FileOutputStream mFileOutputStream = new FileOutputStream(file);
//                byte[] b = errorLog.getBytes("UTF-8");
//                mFileOutputStream.write(b, 0, b.length);
//                mFileOutputStream.flush();
//                mFileOutputStream.close();
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put("clientVersion", clientVersion);
            headers.put("phoneModel", phoneModel);
            headers.put("phonePlatform", phonePlatform);
            headers.put("phoneCpu", phoneCpu);
            headers.put("fingerPrint", fingerPrint);
            headers.put("errorLog", errorLog);
            new Thread(){
                @Override
                public void run() {
                    super.run();HttpPost mPost = new HttpPost(getCrashRequest());
                    List<NameValuePair> mValuePair = new ArrayList<NameValuePair>();
                    mValuePair.add(new BasicNameValuePair("clientVersion", clientVersion));
                    mValuePair.add(new BasicNameValuePair("phoneModel", phoneModel));
                    mValuePair.add(new BasicNameValuePair("phonePlatform", phonePlatform));
                    mValuePair.add(new BasicNameValuePair("phoneCpu", phoneCpu));
                    mValuePair.add(new BasicNameValuePair("fingerPrint", fingerPrint));
                    mValuePair.add(new BasicNameValuePair("errorLog", errorLog));
                    try {
                        mPost.setEntity(new UrlEncodedFormEntity(mValuePair, HTTP.UTF_8));
                        HttpResponse mResponse = new DefaultHttpClient().execute(mPost);
                        if(mResponse.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(mResponse.getEntity());
                            JSONObject mObject = new JSONObject(result);
                            long response = mObject.getLong("responseCode");
                            if(0 == response) {

                            }
                            AppManager.getAppManager().AppExit(context);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(printWriter != null) {
                    printWriter.close();
                }
                if(writer != null) {
                    writer.close();
                }
            }catch (Exception e) {

            }
        }
    }

    private String getCrashRequest() {
        return IConstant.DOMAIN  + "system/error.json";
    }
}
