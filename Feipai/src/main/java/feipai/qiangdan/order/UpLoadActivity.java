package feipai.qiangdan.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

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
import com.tencent.utils.HttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.Log;
import feipai.qiangdan.core.ProgressDialogTask;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.LoginBean;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.PostFile;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.StringUtils;
import feipai.qiangdan.util.http.HttpMultipartPost;
import roboguice.inject.InjectView;

/**
 * 上传位置
 */
public class UpLoadActivity extends PictrueActivity {

    private static final String UP_URL = IConstant.DOMAIN + "api/v1/order/affirmimg";
    private MyLocationListenner myListener = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true;// 是否首次定位
    private LocationClient mLocClient;


    @InjectView(R.id.return_back)
    private View returnBack;
    //设置照片
    @InjectView(R.id.set_picture)
    private View setPicture;
    //要上传的照片
    @InjectView(R.id.upload_picture)
    private ImageView mPicture;
    //上传照片的button
    @InjectView(R.id.upload_button)
    private Button upLoadButton;
    private OrderItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_up_load);
        item = (OrderItem) getIntent().getSerializableExtra(IConstant.ITEM);
        if (item != null) {
            orderNumber = "订单编号：" + item.getOrderNum();
            emploee = "快递员：" + item.getEmployee();
            if (item.getState().equals("取件中")) {
                address = "取件地址：" + item.getsAddress();
            } else {
                address = "送件地址：" + item.getsAddress();
            }
//            mylocation = "订单编号：" + orderNumber + "\n快递员：" + emploee + "\n应送地址：" + address;
        }
        locationMe();
        listener();

    }

    private void listener() {
        mAvatarImage = mPicture;
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveFile == null) {
                    ToastUtils.show(activity, "请设置图片");
                    return;
                }
                BrowseActivity.launch(activity, saveFile.getAbsolutePath());
            }
        });
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setPicture.setOnClickListener(userPhotoClickListeners);
        upLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveFile == null) {
                    ToastUtils.show(activity, "请设置图片");
                    return;
                }
                upLoadPicture();
            }
        });
    }

    private void upLoadPicture() {
        if (!NetWorkConnect.isConnect(activity)) {
            ToastUtils.show(activity, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        // [必填] 登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(activity));
        // [必填] 订单id
        map.put("orderid", String.valueOf(item.getId()));
        // [必填] 上传图片
        map.put("img", String.valueOf(saveFile));

        final String url = generateUrl(map);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String request = PostFile.uploadFile(saveFile, url);
//            }
//        }).start();
        Map<String, String> paramap = sorting(map);
        HttpMultipartPost post = new HttpMultipartPost(activity, UP_URL, saveFile, paramap);

        post.execute();
    }

    private String generateUrl(HashMap<String, String> map) {
        Map<String, String> paramap = sorting(map);
        // StringBuilder是用来组拼请求地址和参数
        final StringBuilder sb = new StringBuilder();
        sb.append(UP_URL).append("?");
        if (paramap != null && paramap.size() != 0) {
            for (Map.Entry<String, String> entry : paramap.entrySet()) {
                //如果请求参数中有中文，需要进行URLEncoder编码
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 请求参数
     *
     * @param maptest 传入map
     * @return 返回 map
     */
    public static Map<String, String> sorting(Map maptest) {


        Collection<String> keyset = maptest.keySet();
        List<String> list = new ArrayList<String>(keyset);
        list.remove("img");
        //对key键值按字典升序排序
        Collections.sort(list);
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i) + "=" + maptest.get(list.get(i)));
        }
        String str = buffer.toString();
        String tokenPara = StringUtils.securityMd5(str);
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(maptest);
        map.put("token", tokenPara);
        return map;
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
        option.setScanSpan(5000);
        option.setPoiDistance(2.0f);
        option.disableCache(false);
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


            Log.v("kan", "拍照mylocation = " + location.getAddrStr());
            if (isFirstLoc && (null != location.getAddrStr())) {
                isFirstLoc = false;
                mylocation = location.getAddrStr();

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
