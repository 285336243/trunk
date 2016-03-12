package com.jiujie8.choice;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.jiujie8.choice.core.CacheRepository;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.StringUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14/11/20.
 * 纠结的Application
 */
public class ChoiceApplication extends Application {

    private final static String CLIENT_VERSION = "client_version";
    private final static String CLIENT_PLATFORM = "client_platform";
    private final static String CLIENT_MODEL = "client_model";
    private final static String CLIENT_DEVICEiD = "client_deviceId";
    private final static String JIUJIE8_TOKEN = "jiujie8_token";

    public static String clientVersion;

    public final static String clientModel = Build.MODEL;

    public static String deviceId;

    private final static Map<String, String> MAP = new HashMap<String, String>();

    private static Context mContext;
    public static boolean isUpdate = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        CrashHandler.getInstance().init();
        initImageLoader();
        HttpUtils.init(getApplicationContext());

        try {
            final Context context = getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            clientVersion = packageInfo.versionName;
            deviceId = getWifiAddressAndIMEI();

            MAP.put(CLIENT_VERSION, clientVersion);
            MAP.put(CLIENT_PLATFORM, "android");
            MAP.put(CLIENT_MODEL, clientModel);
            MAP.put(CLIENT_DEVICEiD, deviceId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 ApplicationContext, ApplicationContext不能用于创建Dialog,PopupWindow
     *
     * @return ApplicationContext
     */
    public final static Context getContext() {
        return mContext;
    }

    /**
     * ImageLoader配置初始化
     */
    public void initImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    /**
     * @return
     */
    public final static String getRequestSign() {
        return StringUtils.securityMd5(StringUtils.mapSortToString(MAP));
    }

    /**
     * 获取参数的MD5
     *
     * @return
     */
    public final static Map<String, String> getSignMap() {
        MAP.put(JIUJIE8_TOKEN, CacheRepository.getInstance().fromContext(mContext).getString(IConstant.USER_LOGIN, IConstant.USER_TOKEN));
        return MAP;
    }

    /**
     * 获取设备唯一ID
     *
     * @return
     */
    private String getWifiAddressAndIMEI() {
        //获取wifi地址
        final StringBuilder builder = new StringBuilder();
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                final String wifiAddress = wifiInfo.getMacAddress();
                if (!TextUtils.isEmpty(wifiAddress)) {
                    builder.append(wifiAddress);
                    builder.append("_");
                } else {
                    builder.append("");
                    builder.append("_");
                }
            }
        }

        //获取imei
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            final String imei = telephonyManager.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                builder.append(imei);
            } else {
                builder.append("");
            }
        }
        return builder.toString();
    }
}
