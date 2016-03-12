package feipai.qiangdan.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragment;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.MyInfoBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.RouteSaveUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.Utils;
import roboguice.inject.InjectView;

/**
 * 个人中心
 */
public class MapFragment extends DialogFragment implements BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener, OnGetGeoCoderResultListener, View.OnClickListener {
    private static final String LOCATIOB_URL = "api/v1/position";
    private static final int LOCATION_CODE = 0X2C3;
    private static String UFRUFJF = "DMSKDDMKDM";
    //界面

    //起点
    @InjectView(R.id.start)
    private TextView mStart;
    //起点布局
    @InjectView(R.id.start_layout)
    private View startLayout;
    //终点布局
    @InjectView(R.id.end_layout)
    private View endLayout;

    //终点
    @InjectView(R.id.end)
    private TextView mEnd;
    @InjectView(R.id.transit)
    private Button driveBtn;

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true;// 是否首次定位


    GeoCoder mGreSearch = null; // 地理编码搜索模块，也可去掉地图模块独立使用

    //搜索相关
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private int nodeIndex = -1;//节点索引,供浏览节点时使用
    private RouteLine route = null;
    private TextView popupText = null;//泡泡view
    //浏览路线节点相关
    //上一个节点
    @InjectView(R.id.pre)
    private Button mBtnPre;
    //下一个节点
    @InjectView(R.id.next)
    private Button mBtnNext;
    //驾车路径
    @InjectView(R.id.drive)
    private Button mBtnDrive;
    //公交路径
    @InjectView(R.id.transit)
    private Button mBtnTransit;
    //步行路径
    @InjectView(R.id.walk)
    private Button mBtnWalk;
    //重定位
    @InjectView(R.id.reset_location)
    private View resetLocation;

    @InjectView(R.id.bmapView)
    private MapView mMapView;
    @InjectView(R.id.map_text)
    private TextView mText;
    private boolean useDefaultIcon = true;
    private OverlayManager routeOverlay = null;
    private String locationtResponse;
    /**
     * 我位置的经纬度
     */
    private LatLng myLocationLatLon;
    private static String myLocation;
    private static boolean isFirstLocation = true;

    PlanNode stNode = null;
    PlanNode enNode = null;
    private Timer time;
    private boolean isUpLocation = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_location, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //地址页面不可见
        startLayout.setVisibility(View.GONE);
        endLayout.setVisibility(View.GONE);

        fixedLocation();
        initListeners();
//        routePlan();
        geoInit();
        //定时上传位置信息
        timeUpLocation();
    }

    private void timeUpLocation() {
        Task task = new Task();
        time = new Timer();
        //每15分钟上传一次位置信息
        time.schedule(task, 1000, 15 * 60 * 1000);
    }

    class Task extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            isUpLocation = !isUpLocation;
        }

    }

    private void initListeners() {
        //绑定点击监听器
        mBtnWalk.setOnClickListener(this);
        mBtnTransit.setOnClickListener(this);
        mBtnDrive.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        //知识图标设为不可见
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        //路径
        //地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        //清空路线信息，便于重设
        mBaiduMap.clear();
    }

    /**
     * 经纬度初始化
     */
    private void geoInit() {
        // 初始化搜索模块，注册事件监听
        mGreSearch = GeoCoder.newInstance();
        mGreSearch.setOnGetGeoCodeResultListener(this);
    }


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        if (null != mSearch) {
            mSearch.destroy();
        }
        if (null != mGreSearch) {
            mGreSearch.destroy();
        }
        //退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }
        //定时退出
        time.cancel();
        super.onDestroy();
    }

    /**
     * 路径规划
     *
     * @param sendLatLon
     */
    private void routePlan(LatLng sendLatLon) {

        //设置起终点信息，对于tranist search 来说，城市名无意义
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("上海", "张江");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("上海", "人民广场");
//        PlanNode stNode = null;
//        PlanNode enNode = null;
        if (!TextUtils.isEmpty(Utils.TRANS_MODE) && !TextUtils.isEmpty(Utils.FROM_ADDRESS)/* && !TextUtils.isEmpty(Utils.TO_ADDRESS)*/) {
            startLayout.setVisibility(View.VISIBLE);
            endLayout.setVisibility(View.VISIBLE);
            mStart.setText(myLocation);
            mEnd.setText(Utils.FROM_ADDRESS);
//            stNode = PlanNode.withCityNameAndPlaceName("上海", Utils.FROM_ADDRESS);
//            enNode = PlanNode.withCityNameAndPlaceName("上海", Utils.TO_ADDRESS);
            //从我的位置到寄件地址的路径
            stNode = PlanNode.withLocation(myLocationLatLon);
            enNode = PlanNode.withLocation(sendLatLon);
            if ("汽车".equals(Utils.TRANS_MODE)) {
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));

            }
            if ("公交".equals(Utils.TRANS_MODE)) {
                mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city("上海").to(enNode));
            }
            if ("电动车".equals(Utils.TRANS_MODE)) {
                mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
            }
        }
        Utils.TRANS_MODE = null;
        Utils.FROM_ADDRESS = null;
        Utils.TO_ADDRESS = null;
//        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
        //计算两点间距离
//       DistanceUtil.getDistance()
    }

    /**
     * 定位
     */
    private void fixedLocation() {
        //
        //定位模式为跟随
        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 修改为自己位置的自定义图标，如果mCurrentMarker=null，则为百度系统图标
        // mBaiduMap.setMyLocationConfigeration()如果用系统图标，可以不写
//        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.map_me);
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

/*    */

    /**
     * 手动请求定位的方法
     *//*
    public void requestLocation() {
        if (mLocClient != null && mLocClient.isStarted()) {
            Toast.makeText(getActivity(), "正在定位......", Toast.LENGTH_SHORT).show();
            mLocClient.start();
        } else {
            Toast.makeText(getActivity(), "暂时无法定位......", Toast.LENGTH_SHORT).show();
        }
    }*/
    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果,请自定义搜索", Toast.LENGTH_LONG).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果,请自定义搜索", Toast.LENGTH_LONG).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果,请自定义搜索", Toast.LENGTH_LONG).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {

            mBtnPre.setVisibility(View.VISIBLE);//路径指引图标
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
        //计算驾车距离
//        result.getRouteLines().get(0).getDistance()
    }

    /**
     * 查询坐标结果
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
 /*       mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));*/
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
/*        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();*/
        //得到经纬度确定的位置
        LatLng sendLatLon = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
        //传入送货经纬度，去做路径规划
        routePlan(sendLatLon);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
       /* mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));*/
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        myLocation = result.getAddress();
   /*     Toast.makeText(GeoCoderDemo.this, result.getAddress(),
                Toast.LENGTH_LONG).show();*/


    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

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
            if (isUpLocation) {
                //上传位置信息
                upLoadLocation(location.getLongitude(), location.getLatitude());
                isUpLocation = false;
//                Log.v("kan", "上传位置信息");
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                //location.getLatitude()纬度
                //location.getLongitude()经度
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
                // 由经纬度位置得到我的位置   反Geo搜索
                if (isFirstLocation) {
                    mGreSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
                    isFirstLocation = false;
                }
                //得到我的位置的经纬度
                myLocationLatLon = ll;
                catchSendLatLon();

//                if (Utils.isFirstUpLoad) {
                //上传位置信息
/*                upLoadLocation(location.getLongitude(), location.getLatitude());
                Log.v("kan", "上传位置信息");*/
//                    Utils.isFirstUpLoad = false;
//                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 将寄件地址转换为经纬度坐标
     */
    private void catchSendLatLon() {
//        mGreSearch.geocode(new GeoCodeOption().city("").address("上海市徐汇区田林路142号H座601室"));
        //传入的地址先转为经纬度，再由经纬度去搜索线路
        // Geo搜索 由地址得到坐标
        if (!TextUtils.isEmpty(Utils.FROM_ADDRESS)) {
            mGreSearch.geocode(new GeoCodeOption().city("").address(Utils.FROM_ADDRESS));//.city("")必须加上不然报错
        }
    }

    /**
     * 上传经纬度
     *
     * @param longitude 纬度
     * @param latitude  经度
     */
    private void upLoadLocation(double longitude, double latitude) {
        if (!NetWorkConnect.isConnect(getActivity())) {
            ToastUtils.show(getActivity(), "您的网络不可用，请检查您的网络状况");
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
/*        //百度地图经纬度转高德地图经纬度消除误差
        map.put("lat", String.valueOf(latitude - 0.006356d));
        map.put("lng", String.valueOf(longitude - 0.0063915d));*/
        //上传百度经纬度
        map.put("lat", String.valueOf(latitude));
        map.put("lng", String.valueOf(longitude));
        HttpClientUtil.getInstance().doPutAsyn(LOCATIOB_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                locationtResponse = result;
                Message msg = Message.obtain();
                msg.what = LOCATION_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (locationtResponse.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), locationtResponse);
                        Log.v("kan","发送位置  失败："+locationtResponse);
                    } else {
                        Gson gson = new Gson();
                        MyInfoBean data = gson.fromJson(locationtResponse, MyInfoBean.class);
                       // status: 1 ， 操作结果(1:成功)
//                        ToastUtils.show(getActivity(), "发送成功，谢谢您的反馈");
                         Log.v("kan","发送位置 成功 ："+locationtResponse);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 驾车路径
     */
    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        /**
         * 驾车路径
         *
         * @param baiduMap 参数
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    /**
     * 步行路径
     */
    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    /**
     * 公交规划
     */
    private class MyTransitRouteOverlay extends TransitRouteOverlay {
        /**
         * 公交规划
         */
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }


    /**
     * 点击时间
     *
     * @param v 点击view
     */
    @Override
    public void onClick(View v) {
        //-------------------------发起路线规划搜索示例-----------------------------------------------
        if (v.getId() == R.id.drive || v.getId() == R.id.transit || v.getId() == R.id.walk) {
            //发起路线规划搜索示例
            //重置浏览节点的路线数据
            route = null;
            mBtnPre.setVisibility(View.INVISIBLE);
            mBtnNext.setVisibility(View.INVISIBLE);
            mBaiduMap.clear();
/*        // 处理搜索按钮响应
        EditText editSt = (EditText) findViewById(R.id.start);
        EditText editEn = (EditText) findViewById(R.id.end);*/

/*            String start = mStart.getText().toString().toString().trim();
            String end = mEnd.getText().toString().toString().trim();
            if (TextUtils.isEmpty(start)) {
                ToastUtils.show(getActivity(), "起点为空，请输入");
                return;
            }
            if (TextUtils.isEmpty(end)) {
                ToastUtils.show(getActivity(), "终点为空，请输入");
                return;
            }*/
//            //设置起终点信息，对于tranist search 来说，城市名无意义
//            PlanNode stNode = PlanNode.withCityNameAndPlaceName("上海", start);
//            PlanNode enNode = PlanNode.withCityNameAndPlaceName("上海", end);
            // 实际使用中请对起点终点城市进行正确的设定
            if (null != stNode && null != enNode) {
                if (v.getId() == R.id.drive) {
                    mSearch.drivingSearch((new DrivingRoutePlanOption())
                            .from(stNode)
                            .to(enNode));
                } else if (v.getId() == R.id.transit) {
                    mSearch.transitSearch((new TransitRoutePlanOption())
                            .from(stNode)
                            .city("上海")
                            .to(enNode));
                } else if (v.getId() == R.id.walk) {
                    mSearch.walkingSearch((new WalkingRoutePlanOption())
                            .from(stNode)
                            .to(enNode));
                }
            }
        }
        //-----------------接点点击时间----------------------
        if (v.getId() == R.id.next || v.getId() == R.id.pre) {
            if (route == null ||
                    route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            //设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            //获取节结果信息
            LatLng nodeLocation = null;
            String nodeTitle = null;
            Object step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            }

            if (nodeLocation == null || nodeTitle == null) {
                return;
            }
            //移动节点至中心
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
            // show popup
            popupText = new TextView(getActivity());
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setTextColor(0xFF000000);
            popupText.setText(nodeTitle);
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
        }
    }
}
