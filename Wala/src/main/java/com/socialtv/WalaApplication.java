package com.socialtv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.GsonUtils;
import com.android.volley.Request;
import com.google.gson.Gson;
import com.mobisage.android.MobiSageManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.http.BodyRequest;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.log.App;
import com.socialtv.log.CliRequest;
import com.socialtv.log.Device;
import com.socialtv.publicentity.MessageResource;
import com.socialtv.util.IConstant;
import com.socialtv.util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wlanjie on 14-6-23.
 */
public class WalaApplication extends Application {

    private static int requestNo = 1;
    private final static String PUSH_ID = "identity/token.json";
    private final static String APP_LOG = "app/log.json";
    private final static String MESSAGE_RESOURCE = "resource/messages.json";
    private final static String BIND_MOBILE = "绑定手机号，并上传本机通讯录，可以让你找到更多的好友。哇啦将以加密的方式上传你的通讯录信息，且该信息也仅做邀请好友使用。不会将你的个人信息泄露给其他的用户，请放心填写";
    private final static String REGISTER_MOBILE = "通过手机号码可以找到更多的好友，也可用于找回密码。哇啦不会再任何地方泄漏您的手机号码";
    private final static String VERIFY_CODE_MESSAGE = "哇啦将给你的手机发送一条短信，请将短信中的验证码填写到上面。";
    private final static String INVITE_SCORE = "有邀请码，可以获得更多的番茄币";

    private static Context mContext = null;

    private final App app = new App();

    private final CliRequest cliRequest = new CliRequest();

    private final Device device = new Device();

    private final com.socialtv.log.Request request = new com.socialtv.log.Request();

    private final Gson gson = GsonUtils.createGson();

    private String uniqueId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init();
        mContext = getApplicationContext();
        initImageLoader(getApplicationContext());
        HttpUtils.init(getApplicationContext());
        final SharedPreferences preferences = getSharedPreferences(IConstant.MESSAGE, MODE_PRIVATE);
        uniqueId = MD5Util.getMD5Str(getWifiAddressAndIMEI());
        initAdvert();
        new AbstractRoboAsyncTask<MessageResource>(getApplicationContext()){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected MessageResource run(Object data) throws Exception {
                GsonRequest<MessageResource> request = new GsonRequest<MessageResource>(Request.Method.GET, MESSAGE_RESOURCE);
                request.setClazz(MessageResource.class);
                return (MessageResource) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(MessageResource messageResource) throws Exception {
                super.onSuccess(messageResource);
                if (messageResource != null) {
                    preferences.edit().putString(IConstant.BIND_MOBILE, messageResource.getBIND_MOBILE())
                            .putString(IConstant.REGISTER_MOBILE, messageResource.getREGISTER_MOBILE())
                            .putString(IConstant.VERIFY_CODE_MESSAGE, messageResource.getVERIFY_CODE_MESSAGE())
                            .putString(IConstant.INVITE_SCORE, messageResource.getINVITE_SCORE()).commit();
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                preferences.edit().putString(IConstant.BIND_MOBILE, BIND_MOBILE)
                        .putString(IConstant.REGISTER_MOBILE, REGISTER_MOBILE)
                        .putString(IConstant.VERIFY_CODE_MESSAGE, VERIFY_CODE_MESSAGE)
                        .putString(IConstant.INVITE_SCORE, INVITE_SCORE).commit();
            }

            @Override
            protected void onThrowable(Throwable t) throws RuntimeException {
                super.onThrowable(t);
                preferences.edit().putString(IConstant.BIND_MOBILE, BIND_MOBILE)
                        .putString(IConstant.REGISTER_MOBILE, REGISTER_MOBILE)
                        .putString(IConstant.VERIFY_CODE_MESSAGE, VERIFY_CODE_MESSAGE)
                        .putString(IConstant.INVITE_SCORE, INVITE_SCORE).commit();
            }
        }.execute();

        new AbstractRoboAsyncTask<Response>(getApplicationContext()){
            @Override
            protected Response run(Object data) throws Exception {
                getAppLogInfo();
                List<CliRequest> requests = new ArrayList<CliRequest>();
                requests.add(cliRequest);

                BodyRequest<Response> request = new BodyRequest<Response>(Request.Method.POST, APP_LOG, gson.toJson(requests));
                request.setClazz(Response.class);
                return (Response) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
            }
        }.execute();

        postPushId();
    }

    /**
     * 初始化艾德思奇广告
     */
    private void initAdvert() {
        MobiSageManager.getInstance().initMobiSageManager(this,
                "4OHgM490bfrQFNG6Zw==", "AnZhi_v1.1.3.1");
    }

    /**
     * 上传设备唯一ID,推送使用
     */
    public void postPushId() {
        new AbstractRoboAsyncTask<Response>(getApplicationContext()){
            @Override
            protected Response run(Object data) throws Exception {
                final String uniqueId = MD5Util.getMD5Str(getUniqueId());
                final Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("token", uniqueId);
                bodys.put("p", "ANDROID");
                GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, PUSH_ID);
                request.setHeaders(bodys);
                request.setClazz(Response.class);
                return (Response) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
            }
        }.execute();
    }

    /**
     * 暴露一个方法给外部获取设备的唯一ID
     * @return
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * 获取设备唯一ID
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

    private void getAppLogInfo() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
            String mobileModel = Build.MODEL;
            String mobileBrand = Build.MANUFACTURER;
            String sdkVersion = Build.VERSION.RELEASE;
            app.setClientVersion(versionName);
            app.setAppKey("Wala");

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            //获取运营商
            final String operator = telephonyManager.getSimOperator();
            if (!TextUtils.isEmpty(operator)) {
                List<String> operators = new ArrayList<String>();
                if ("46000".equals(operator) || "46002".equals(operator) || "46007".equals(operator)) {
                    //中国移动
                    operators.add("中国移动");
                } else if ("46001".equals(operator)) {
                    //中国联通
                    operators.add("中国联通");
                } else if ("46003".equals(operator)) {
                    //中国电信
                    operators.add("中国电信");
                }
                device.setCarrier(operators);
            }

            //获取网络连接类型
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    List<String> netWorks = new ArrayList<String>();
                    int type = networkInfo.getType();
                    if (type == ConnectivityManager.TYPE_MOBILE) {
                        int mobileNetWorkType = getNetworkClass(telephonyManager.getNetworkType());
                        String netWorkType = "";
                        switch (mobileNetWorkType) {
                            case NETWORK_CLASS_2_G:
                                netWorkType = "2G";
                                break;
                            case NETWORK_CLASS_3_G:
                                netWorkType = "3G";
                                break;
                            case NETWORK_CLASS_4_G:
                                netWorkType = "4G";
                                break;
                        }
                        netWorks.add(netWorkType);
                        device.setNetwork(netWorks);
                    }
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        netWorks.add("wifi");
                        device.setNetwork(netWorks);
                    }
                }
            }

            device.setDeviceId(getUniqueId());
            device.setPhoneModel(mobileBrand + "-" + mobileModel);

            request.setName("START_UP");
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            final String date = format.format(new Date());
            request.setRequestNo(date + getRequstLogNumber());

            final String sessionId = MD5Util.getMD5Str(UUID.randomUUID().toString());
            request.setSessionId(sessionId);

            cliRequest.setVersion(1);
            cliRequest.setApp(app);
            cliRequest.setDevice(device);
            cliRequest.setRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRequstLogNumber() {
        String number = "";
        if (requestNo < 10) {
            number = "00000" + requestNo;
        } else if (requestNo < 100) {
            number = "0000" + requestNo;
        } else if (requestNo < 1000) {
            number = "000" + requestNo;
        } else if (requestNo < 10000) {
            number = "00" + requestNo;
        } else if (requestNo < 100000) {
            number = "0" + requestNo;
        }
        return number;
    }

    public String getRequestNo() {
        if (requestNo == 999999) {
            requestNo = 0;
        }
        requestNo++;
        return getRequstLogNumber();
    }

    /** Network type is unknown */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B*/
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    public static final int NETWORK_TYPE_HSPAP = 15;

    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. {@hide} */
    public static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. {@hide} */
    public static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. {@hide} */
    public static final int NETWORK_CLASS_4_G = 3;

    /**
     * Return general class of network type, such as "3G" or "4G". In cases
     * where classification is contentious, this method is conservative.
     *
     * @hide
     */
    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public void initImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())
                    .discCacheSize(20 * 1024 * 1024)
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    public static Context getContext() {
        return mContext;
    }
}
