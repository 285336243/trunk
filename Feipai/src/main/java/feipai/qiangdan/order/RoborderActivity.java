package feipai.qiangdan.order;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.map.B;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.Log;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.util.AudioHelp;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.ScreenUtil;
import feipai.qiangdan.util.Utils;
import feipai.qiangdan.util.VibrateHelper;
import feipai.qiangdan.util.VolleyUtil;
import roboguice.inject.InjectView;

/**
 *
 */
public class RoborderActivity extends DialogFragmentActivity {
    private static final String ROB_ORDER_URL = "api/v1/order/roborder";
    private static final int ROB_ORDER_CODE = 0xAF;
    @InjectView(R.id.order_info)
    private TextView orderInfo;
    //抢单button
    @InjectView(R.id.rob_button)
    private Button robButton;
    //据我多少公里
    @InjectView(R.id.distance_me)
    private TextView mDistance;

    //订单详情
    @InjectView(R.id.order_detail_text)
    private TextView orderDetail;
    //定时取
    @InjectView(R.id.time_to_take)
    private TextView timeTake;
    //定时送
    @InjectView(R.id.time_to_send)
    private TextView timeSend;
    //其他信息
    @InjectView(R.id.other_info)
    private TextView otherInfo;
    //下部订单背景
    @InjectView(R.id.oder_backgrd)
    private View orderBackgrd;


    @InjectView(R.id.colose_imageview)
    private View coloseView;
    private String message;
    private Dialog mDialog;
    private String robResponse;
    private ProgressDialog progressdialog;
    //订单id
    private int orderId;
    private Dialog opreDialog;
    private Activity context;
    private MyLocationListenner myListener = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true;// 是否首次定位
    private LocationClient mLocClient;
    /**
     * 寄件地址
     */
    private String jaddress;
    /**
     * 我的位置的经纬度
     */
    private LatLng myLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_rob_order);
        context = RoborderActivity.this;
        coloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

  /*       if (Build.VERSION.SDK_INT >= 11) {
            setFinishOnTouchOutside(false);
        }
       int heighted = ScreenUtil.getViewHeight(shareLayout);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //设置窗口的大小及透明度
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = heighted;
        layoutParams.gravity = Gravity.BOTTOM;
//        layoutParams.alpha = 0.5f;
        window.setAttributes(layoutParams);*/
        locationMe();
        //是否从首页跳入
        boolean isFromHome = getIntent().getBooleanExtra(IConstant.FROM_HOME, false);
        feipai.qiangdan.core.Log.v("kan", "isFromHome= " + isFromHome);
        feipai.qiangdan.core.Log.v("kan", "receiverData= " + Utils.receiverData);
        if (null == Utils.receiverData) {
            if (isFromHome) {
                feipai.qiangdan.core.Log.v("kan", "进入oder");
                final OrderItem order = (OrderItem) getIntent().getSerializableExtra(IConstant.ORDER_ITEM);
                orderId = order.getId();
                setOrderDate(order);
            } else {
                feipai.qiangdan.core.Log.v("kan", "finish activity");
                finish();
            }
        } else {
            receviePushMeassage();

        }

        robButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//             startActivity(new Intent(RoborderActivity.this,OrderDetaisActivity.class).putExtra(IConstant.ORDER_ID,String.valueOf(order.getId())));
                //点击抢单时关闭声音
                AudioHelp.releaseMediaPlayer();
                openOpreDialog();

            }
        });
    }

    /**
     * 定位我的位置
     */
    private void locationMe() {
        // 地图初始化
        mMapView = new MapView(this);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setPoiDistance(2.0f);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            Log.v("kan","我的位置："+location.getAddrStr()+",  经度："+location.getLatitude());
            Log.v("kan", "抢单location = " + location.getAddrStr());
            if (isFirstLoc) {
                isFirstLoc = false;
                myLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(myLatLng);
                mBaiduMap.animateMapStatus(u);
                //取得寄件人经纬度
                getJaddresslatlng();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 得到寄件地址坐标
     */
    private void getJaddresslatlng() {
        GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        // Geo搜索
        mSearch.geocode(new GeoCodeOption().city("").address(jaddress));
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {

//                    ToastUtils.show(context, "抱歉，找不到寄件人地址，请重新填写或更改为相近的地址");
                    return;
                }
                double distance = DistanceUtil.getDistance(myLatLng, result.getLocation());//单位 米
                DecimalFormat df = new DecimalFormat("#.0");//保留一位小数
                mDistance.setText("据您" + df.format(distance / 1000 * 1.5) + "公里");
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });

    }

    private void robOrder(int id) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("orderid", String.valueOf(id));
        map.put("sid", SaveSettingUtil.getUserSid(this));
        VolleyUtil.getInstance(context).volley_put(ROB_ORDER_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                Gson gson = new Gson();
                OrderListBean data = gson.fromJson(str, OrderListBean.class);
                //抢单结果 data.getState()
                // 抢单结果(0:抢单失败;1:抢单成功;2:禁止抢单)
                startProgressDialog(data.getState());
            }

            @Override
            public void error(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    ToastUtils.show(context, "登陆超时,请重登陆");
                    startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });
    }
/*    private void robOrder(int id) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("orderid", String.valueOf(id));
        map.put("sid", SaveSettingUtil.getUserSid(this));

        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = Message.obtain();
                    //待处理订单请求
                    robResponse = HttpClientUtil.getInstance().getPutData(ROB_ORDER_URL, map);
                    msg.what = ROB_ORDER_CODE;
                    mHandler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

    private void setOrderDate(OrderItem item) {
        //如果是定时的显示为黄色，否则显示为蓝色,蓝色为布局背景不需要设
        if (item.getIsTime() == 1 || item.getIsSong() == 1) {
            orderBackgrd.setBackgroundResource(R.drawable.time_background);
        } /*else {
            orderBackgrd.setBackgroundResource(R.drawable.rober_background);
        }*/
        jaddress = item.getjAddress();

        orderId = item.getId();
        String info = "：¥" + item.getPrice() + "/" + item.getDistance() + "公里/" + item.getWeight() + "公斤/";
        if ("送".equals(item.getType())) {
            orderInfo.setText("【快送】" + info + item.getPaymentType());
            if (item.getIsSong() == 1) {
                orderInfo.setText("【定时送】" + info + item.getPaymentType());
            }
        }
        if ("买".equals(item.getType())) {
            orderInfo.setText("【代买】" + info + item.getPaymentType());
        }
//        mDistance.setText("据您  公里");
        orderDetail.setText("下单时间: " + item.getPublishTime() + "\n取件地址：" + item.getjAddress() + "\n收件地址：" + item.getsAddress());
        //指定取件时间（根据实际情况显示）
        // 指定送达时间（根据实际情况显示）
        timeTake.setVisibility(View.GONE);
        timeSend.setVisibility(View.GONE);
        if (item.getIsTime() == 1) {
            timeTake.setVisibility(View.VISIBLE);
            timeTake.setText("指定取件时间：" + item.getjTime());
        }
        if (item.getIsSong() == 1) {
            timeSend.setVisibility(View.VISIBLE);
            timeSend.setText("指定送达时间：" + item.getEndTime());
        }
        otherInfo.setText("物品名称: " + item.getGoodsName() + "\n客户备注: " + item.getRemark());
    }

    private void receviePushMeassage() {
        ScreenUtil.wakeAndUnlock(this);
        listenerSetting();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle)
            message = bundle.getString("message");
//        orderInfo.setText("接受消息为：" + Utils.receiverData);
        Gson gson = new Gson();
        //接受数据进行json解析
        OrderItem order = gson.fromJson(Utils.receiverData, OrderItem.class);
        setOrderDate(order);
        Utils.receiverData = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 开启震动和声音
     */
    private void listenerSetting() {
/*        Log.v("kan", "isVibrate状态 :" + SaveSettingUtil.isVibrate(this));
        Log.v("kan", "isSound :" + SaveSettingUtil.isSound(this));*/

        if (SaveSettingUtil.isVibrate(this))
            VibrateHelper.Vibrate(this, new long[]{50, 1000, 100, 1000, 50}, false);
        if (SaveSettingUtil.isSound(this))
            AudioHelp.playVoice(this, R.raw.tipsound);
    }

    @Override
    protected void onStop() {
        super.onStop();
//         Log.v("kan", "run onStop");
        if (Utils.receiverData != null) {
            Utils.receiverData = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭震动
        VibrateHelper.CancelVibrate(this);
        //关闭声音
        AudioHelp.releaseMediaPlayer();
//        Log.v("kan", "run onDestroy");
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    /**
     * 启动抢单对话框
     *
     * @param state
     */
    private void startProgressDialog(int state) {
        mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.layout_rob_result_dialog);
        mDialog.setCanceledOnTouchOutside(false);
        ImageView imageView = (ImageView) mDialog.findViewById(R.id.hintImageView);
        TextView orderResult = (TextView) mDialog.findViewById(R.id.order_result);
        TextView lookOrder = (TextView) mDialog.findViewById(R.id.look_order_details);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Utils.isRober = true;
            }
        });
        // 抢单结果(0:抢单失败;1:抢单成功;2:禁止抢单)
        switch (state) {
            case 1:
                imageView.setBackgroundResource(R.drawable.rob_result_success);
                orderResult.setText("恭喜，抢单成功！");
                lookOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopProgressDialog();
                        finish();
                        // 跳入订单详情
                        startActivity(new Intent(RoborderActivity.this, OrderDetaisActivity.class).putExtra(IConstant.ORDER_ID, String.valueOf(orderId)));
                    }
                });
                break;
            case 0:
                orderResult.setText("抢单失败！");
                lookOrder.setVisibility(View.GONE);
                imageView.setBackgroundResource(R.drawable.rob_result_fail);
                break;
            case 2:
                orderResult.setText("禁止抢单！");
                lookOrder.setVisibility(View.GONE);
                imageView.setBackgroundResource(R.drawable.rob_result_forbid);
                break;
            default:
                orderResult.setText("禁止抢单！");
                lookOrder.setVisibility(View.GONE);
                imageView.setBackgroundResource(R.drawable.rob_result_forbid);
                break;
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 关掉抢单对话框
     */
    private void stopProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

 /*   Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ROB_ORDER_CODE:
                    //进度条消失
                    if (null != progressdialog || progressdialog.isShowing())
                        progressdialog.dismiss();
                    if (robResponse.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(RoborderActivity.this, robResponse);
                        ToastUtils.show(context, "登陆超时,请重登陆");
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    } else {
                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(robResponse, OrderListBean.class);
                        //抢单结果 data.getState()
                        // 抢单结果(0:抢单失败;1:抢单成功;2:禁止抢单)
                        startProgressDialog(data.getState());

                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void openOpreDialog() {
        opreDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        opreDialog.setCanceledOnTouchOutside(false);
        opreDialog.setContentView(R.layout.opre_hint_dialog);
        View yes = opreDialog.findViewById(R.id.yes_button);
        View no = opreDialog.findViewById(R.id.no_button);
        yes.setOnClickListener(listens);
        no.setOnClickListener(listens);
        if (!opreDialog.isShowing())
            opreDialog.show();
    }

    private View.OnClickListener listens = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yes_button:
                    robOrder(orderId);
                    robButton.setText("已 抢 单");
                    robButton.setBackgroundColor(getResources().getColor(R.color.grey));
                    robButton.setClickable(false);
                    //网络请求进度条
/*                    progressdialog = new ProgressDialog(RoborderActivity.this);
                    progressdialog.setMessage("努力抢单中...");
                    progressdialog.setCancelable(true); //点击返回键是否消失 true为消失
                    progressdialog.show();*/
                    cancelDialog();
                    break;
                case R.id.no_button:
                    cancelDialog();
                    break;
                default:
                    break;
            }

        }
    };

    private void cancelDialog() {
        if (opreDialog.isShowing() || opreDialog != null)
            opreDialog.dismiss();
    }
}
