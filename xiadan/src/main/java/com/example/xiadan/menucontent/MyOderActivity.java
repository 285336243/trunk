package com.example.xiadan.menucontent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.xiadan.R;
import com.example.xiadan.bean.JiFenBean;
import com.example.xiadan.bean.JiFenListBean;
import com.example.xiadan.bean.OrderItem;
import com.example.xiadan.bean.OrderListBean;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.NetWorkConnect;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 我的订单
 */
public class MyOderActivity extends BaseActivity {
    /**
     * 积分url
     */
    private static final String MY_JIFEN_URL = "page/api/v1/order/list/my";
    private static final int MY_ORDERLIST_CODE = 0X13;
    /**
     * 记录listview
     */
    private ListView listView;
    /**
     * 空view
     */
    private TextView noOrderEmptyview;


    private int page = 1;
    private int count = 10;
    /**
     * 网络请求结果
     */
    private String ordersResult;

    private List<OrderItem> transList = new LinkedList<OrderItem>();

    private View moreView;
    /**
     * listview可见的最后一条的位置
     */
    private int lastItem;
    private TextView tv_load_more;
    private ProgressBar pb_load_progress;
    private OrderAdapter adapter;
    /**
     * 是否需要再次加载数据
     */
    private boolean isOnceLoadData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_oders);
        actionBar.setTitle("我的订单");
        activity = MyOderActivity.this;
        initView();
        getOrderData();
//        getHistoryOrder();
    }

    private void getOrderData() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));//登录验证码
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        volley_get(MY_JIFEN_URL, map);
    }

    @Override
    protected void onceGetData() {
        super.onceGetData();
        initView();
        getOrderData();
//        getHistoryOrder();
    }



    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        OrderListBean data = gson.fromJson(response, OrderListBean.class);
        int number = data.getSize();
        if (number > 0) {
            transList.addAll(data.getList());
            adapter.setItems(transList);
            if (number < count) {
                tv_load_more.setText("没有更多数据了");
                pb_load_progress.setVisibility(View.GONE);
                isOnceLoadData = false;
            }

        } else {
            noOrderEmptyview.setVisibility(View.VISIBLE);
            listView.setEmptyView(noOrderEmptyview);
        }

    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
        startActivityForResult();
    }
    private void initView() {
        //记录列表
        listView = (ListView) findViewById(R.id.list_view);
        //空v记录显示的view
        noOrderEmptyview = (TextView) findViewById(R.id.no_order_emptyview);
        //加载提示框
//        ProgressDlgUtil.showProgressDlg(activity, "正在加载");
        LayoutInflater inflater = LayoutInflater.from(activity);
        moreView = inflater.inflate(R.layout.footer_more, null);
        tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);
        pb_load_progress = (ProgressBar) moreView.findViewById(R.id.pb_load_progress);
        tv_load_more.setText(R.string.activity_loading);
        pb_load_progress.setVisibility(View.VISIBLE);

        listView.addFooterView(moreView);
        adapter = new OrderAdapter((MyOderActivity) activity);
        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // listview可见的最后一条的位置等于放入数据的条数，并停止滑动时，加载更多
                if (isOnceLoadData && lastItem == transList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    getOrderData();
//                    getHistoryOrder();
                    Log.v("kan", "加载数据中。。。");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

/*    private void getHistoryOrder() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));//登录验证码
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        HttpClientUtil.getInstance().doGetAsyn(MY_JIFEN_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                ordersResult = result;
                Message msg = Message.obtain();
                msg.what = MY_ORDERLIST_CODE;
                mHandler.sendMessage(msg);
            }
        });

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MY_ORDERLIST_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (ordersResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, ordersResult);
                          startActivityForResult();

                    } else {
                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(ordersResult, OrderListBean.class);
                        int number = data.getSize();
                        if (number > 0) {
                            transList.addAll(data.getList());
                            adapter.setItems(transList);
                            if (number < count) {
                                tv_load_more.setText("没有更多数据了");
                                pb_load_progress.setVisibility(View.GONE);
                                isOnceLoadData = false;
                            }

                        } else {
                            noOrderEmptyview.setVisibility(View.VISIBLE);
                            listView.setEmptyView(noOrderEmptyview);
                        }
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    public void freshOrderList() {
        if (!transList.isEmpty()) {
            transList.clear();
        }
        page = 1;
        getOrderData();
//        getHistoryOrder();
    }
}
