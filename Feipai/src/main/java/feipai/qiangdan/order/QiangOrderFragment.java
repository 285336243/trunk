package feipai.qiangdan.order;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragment;
import feipai.qiangdan.core.Log;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.home.HomeActivity;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.javabean.VersionBean;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.other.UpgradeVersonDialog;
import feipai.qiangdan.util.AnimationHelper;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.Utils;
import feipai.qiangdan.util.VolleyUtil;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/1/14.
 */
public class QiangOrderFragment extends DialogFragment {

    private static final String ORDER_LIST = "api/v1/order/neworder";
    private static final String HAVE_ORDER_LIST = "api/v1/order/myorder";
    private static final int ORDER_ACCEPT_CODE = 0x34;
    private static final int HANdDLER_ACCEPT_CODE = 0x224;

    private static final String CHECK_VERSION_URL = "api/v1/version";
    private static final int VERSION_INFOR = 0XB6;
    private String veisionInfol;

    @InjectView(R.id.rg)
    private RadioGroup radioGroup;

    @InjectView(R.id.vf)
    private ViewFlipper viewFlipper;

    @InjectView(R.id.rb01)
    private RadioButton radioButton1;

    @InjectView(R.id.date_textview)
    private TextView dateTextView;

    @InjectView(R.id.loading)
    private View mLoading;

    @InjectView(R.id.count_wait_order)
    private TextView countWaitOrder;

    @InjectView(R.id.count_handler_order)
    private TextView countHandlerOrder;

    @InjectView(R.id.rb02)
    private RadioButton radioButton2;

    //刷新
    @InjectView(R.id.refresh_imageview)
    private View refresh;

    //待抢订单页面
    @InjectView(R.id.list_view)
    private ListView listView;
    @InjectView(R.id.no_order_emptyview)
    private TextView noOrderEmptyview;
    //待处理订单页面
    @InjectView(R.id.handlerlist_view)
    private ListView handlerListView;
    @InjectView(R.id.handlerno_order_emptyview)
    private TextView handlerNoOrderEmptyview;
    //请求数据结果
    //待抢订单数据
    private String response;
    //待处理订单数据
    private String handlerResponse;
    private HashMap<String, String> map;
    private HomeActivity homeActivity;
    private static boolean isCheckbtn2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qiang_dan, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Log.v("kan", "QiangOrderFragment onViewCreated");
        radioGroup.check(R.id.rb01);


        setListener();
        getOrderInfor();
        setTextView();
        refresh();
    }

    /**
     * 刷新
     */
    private void refresh() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoading.getVisibility() == View.GONE)
                    mLoading.setVisibility(View.VISIBLE);
                getOrderInfor();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.v("kan", "QiangOrderFragment onResume");
        if (null != Utils.receiverData) {
            Log.v("kan", "RoborderActivity 启动");
            Intent intent = new Intent(getActivity(), RoborderActivity.class);
            startActivity(intent);
        }
        //抢单后刷新数据
        if (Utils.isRober) {
            setListener();
            getOrderInfor();
            Utils.isRober = false;
        }

        //订单推送后刷新数据
        if (Utils.isPushCome) {
            setListener();
            getOrderInfor();
            Utils.isPushCome = false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.v("kan", "QiangOrderFragment onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.v("kan", "QiangOrderFragment onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.v("kan", "QiangOrderFragment onStop");
    }

    private void getOrderInfor() {
        if (!NetWorkConnect.isConnect(getActivity())) {
            ToastUtils.show(getActivity(), "您的网络不可用，请检查您的网络状况");
            return;
        }
        map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
//        getOrderData(map);

//        getHandlerOrderData(map);
        getNewOrder(map);
        getExecuteOrder(map);
    }

    /**
     * 执行中订单
     *
     * @param map
     */
    private void getExecuteOrder(HashMap<String, String> map) {
        VolleyUtil.getInstance(getActivity()).volley_get(HAVE_ORDER_LIST, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                if (mLoading.getVisibility() == View.VISIBLE)
                    mLoading.setVisibility(View.GONE);
                setExecuteData(str);
            }

            @Override
            public void error(VolleyError error) {

            }
        });
    }

    private void setExecuteData(String str) {
        Gson gson = new Gson();
        OrderListBean data = gson.fromJson(str, OrderListBean.class);
        int number = data.getSize();
        if (number > 0) {
            countHandlerOrder.setText("执行中: " + number + " 单");
            if (null != getActivity()) {
                HandlerOrderAdapter handlerAdapter = new HandlerOrderAdapter(getActivity());
                handlerAdapter.setItems(data.getList());
                handlerListView.setAdapter(handlerAdapter);
            }
        } else {
            handlerNoOrderEmptyview.setVisibility(View.VISIBLE);
            handlerListView.setEmptyView(handlerNoOrderEmptyview);
        }
    }

    /**
     * 新订单请求
     *
     * @param map 参数
     */
    private void getNewOrder(HashMap<String, String> map) {
        VolleyUtil.getInstance(getActivity()).volley_get(ORDER_LIST, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                if (mLoading.getVisibility() == View.VISIBLE)
                    mLoading.setVisibility(View.GONE);
                setNewData(str);
            }

            @Override
            public void error(VolleyError error) {
                if (mLoading.getVisibility() == View.VISIBLE)
                    mLoading.setVisibility(View.GONE);
                if (error.networkResponse.statusCode == 401) {
                    ToastUtils.show(getActivity(), "登陆超时,请重登陆");
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    private void setNewData(String response) {
        Gson gson = new Gson();
        OrderListBean data = gson.fromJson(response, OrderListBean.class);
        int number = data.getSize();
        if (number > 0) {
            countWaitOrder.setText("新订单: " + data.getNewSize() + " 单");
//                            Log.v("kan", "getActivity = " + getActivity());
            if (null != getActivity()) {
                WaitOrderAdapter adapter = new WaitOrderAdapter(getActivity(), data.getIsForbid(), data.getForbidMsg());
                adapter.setItems(data.getList());
                listView.setAdapter(adapter);
            }
        } else {
            noOrderEmptyview.setVisibility(View.VISIBLE);
            listView.setEmptyView(noOrderEmptyview);
        }
    }

    /**
     * 执行中订单
     *
     * @param map 参数
     */
    private void getHandlerOrderData(final Map<String, String> map) {
        new Thread() {
            @Override
            public void run() {
                try {
//                    synchronized (this) {
                    Message msg = Message.obtain();
                    //待处理订单请求
                    handlerResponse = HttpClientUtil.getInstance().getGetData(HAVE_ORDER_LIST, map);
                    msg.what = HANdDLER_ACCEPT_CODE;
                    mHandler.sendMessage(msg);
//                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setTextView() {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");//星期几
        String week = dateFm.format(date);
        dateFm = new SimpleDateFormat("MM月dd日");//日期：XX月xx日

        String day = dateFm.format(date);
        dateTextView.setText(day + "    " + week);//XX月xx日
    }

/*    */
    /**
     * 待抢订单请求
     *
     * @param map 参数
     *//*
    private void getOrderData(final Map<String, String> map) {
        new Thread() {
            @Override
            public void run() {
                try {
//                    synchronized (this) {
                    Message msg2 = Message.obtain();
                    //待抢订单请求
                    response = HttpClientUtil.getInstance().getGetData(ORDER_LIST, map);
                    msg2.what = ORDER_ACCEPT_CODE;
                    mHandler.sendMessage(msg2);
//                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
/*                //待抢订单数据
                case ORDER_ACCEPT_CODE:
                    if (mLoading.getVisibility() == View.VISIBLE)
                        mLoading.setVisibility(View.GONE);
                    if (response.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), response);
                        ToastUtils.show(getActivity(), "登陆超时,请重登陆");
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    } else {
                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(response, OrderListBean.class);
                        int number = data.getSize();
                        if (number > 0) {
                            countWaitOrder.setText("新订单: " + data.getNewSize() + " 单");
//                            Log.v("kan", "getActivity = " + getActivity());
                            if (null != getActivity()) {
                                WaitOrderAdapter adapter = new WaitOrderAdapter(getActivity(), data.getIsForbid(), data.getForbidMsg());
                                adapter.setItems(data.getList());
                                listView.setAdapter(adapter);
                            }
                        } else {
                            noOrderEmptyview.setVisibility(View.VISIBLE);
                            listView.setEmptyView(noOrderEmptyview);
                        }
                    }
                    break;*/
                //已抢订单数据
             /*   case HANdDLER_ACCEPT_CODE:
                    if (mLoading.getVisibility() == View.VISIBLE)
                        mLoading.setVisibility(View.GONE);
                    if (handlerResponse.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), handlerResponse);
                    } else {

                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(handlerResponse, OrderListBean.class);
                        int number = data.getSize();
                        if (number > 0) {
                            countHandlerOrder.setText("执行中: " + number + " 单");
                            if (null != getActivity()) {
                                HandlerOrderAdapter handlerAdapter = new HandlerOrderAdapter(getActivity());
                                handlerAdapter.setItems(data.getList());
                                handlerListView.setAdapter(handlerAdapter);
                            }
                        } else {
                            handlerNoOrderEmptyview.setVisibility(View.VISIBLE);
                            handlerListView.setEmptyView(handlerNoOrderEmptyview);
                        }
                    }
                    break;*/
/*                case VERSION_INFOR:
                    homeActivity.isShowUpdateDialog = true;
                    int code = 1;
                    try {
                        code = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (veisionInfol.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), veisionInfol);
                    } else {
                        Gson gson = new Gson();
                        VersionBean response = gson.fromJson(veisionInfol, VersionBean.class);
                        if (response.getVersionNum() > code) {
                            UpgradeVersonDialog.createUpdateDialog(getActivity(), response.getLink(),
                                    response.getVersion(), response.getInfo());
                        }
                    }
                    break;*/
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setListener() {
        viewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
        viewFlipper.setFlipInterval(300);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                checkedChange(checkedId);
            }
        });
/*        //检测标题加载完成
        robOrderTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //At this point the layout is complete and the
                //dimensions of myView and any child views are known.
                if (mLoading.getVisibility() == View.VISIBLE)
                    mLoading.setVisibility(View.GONE);
            }
        });*/
    }

    private void checkedChange(int id) {
        switch (id) {
            case R.id.rb01:
                viewFlipper.setInAnimation(AnimationHelper.inFromRight());
                viewFlipper.setOutAnimation(AnimationHelper.outToLeft());
                viewFlipper.setDisplayedChild(0);
                break;
            case R.id.rb02:
                viewFlipper.setInAnimation(AnimationHelper.inFromLeft());
                viewFlipper.setOutAnimation(AnimationHelper.outToRight());
                viewFlipper.setDisplayedChild(1);
                break;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        homeActivity = (HomeActivity) activity;
        if (homeActivity.isShowUpdateDialog)
            return;
        //版本升级
        checkVersionUpdate();
    }

    private void checkVersionUpdate() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
        VolleyUtil.getInstance(getActivity()).volley_get(CHECK_VERSION_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                setUpdateDialog(str);
            }

            @Override
            public void error(VolleyError error) {

            }
        });
    }

    private void setUpdateDialog(String veisionInfol) {
        homeActivity.isShowUpdateDialog = true;
        int code = 1;
        try {
            code = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        VersionBean response = gson.fromJson(veisionInfol, VersionBean.class);
        if (response.getVersionNum() > code) {
            UpgradeVersonDialog.createUpdateDialog(getActivity(), response.getLink(),
                    response.getVersion(), response.getInfo());
        }
    }

}
