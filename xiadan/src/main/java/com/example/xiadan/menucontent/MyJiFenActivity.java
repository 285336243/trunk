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
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.NetWorkConnect;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 我的积分
 */
public class MyJiFenActivity extends BaseActivity {
    /**
     * 积分url
     */
    private static final String MY_JIFEN_URL = "page/api/v1/integral/list/my";
    private static final int MY_JIFEN_CODE = 0XD3;
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
    private String jifenResult;

    private List<JiFenBean> transList = new LinkedList<JiFenBean>();

    private View moreView;
    /**
     * listview可见的最后一条的位置
     */
    private int lastItem;
    private TextView tv_load_more;
    private ProgressBar pb_load_progress;
    private JiFenAdapter adapter;
    /**
     * 是否需要再次加载数据
     */
    private boolean isOnceLoadData=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_jifen);
        activity=MyJiFenActivity.this;
        actionBar.setTitle("我的飞豆");
        initView();
        //加载数据
//        getHistoryOrder();
        getFeiDou();
    }

    /**
     * 得到我的飞豆
     */
    private void getFeiDou(){
        if(!NetWorkConnect.isConnect(this)){
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        volley_get(MY_JIFEN_URL, map);
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        JiFenListBean data = gson.fromJson(response, JiFenListBean.class);
        int number = data.getSize();
        ((TextView)findViewById(R.id.total_jifen)).setText("当前飞豆："+data.getIntegral());
        if (number > 0) {
            transList.addAll(data.getList());
            adapter.setItems(transList);
            if(number<count){
                tv_load_more.setText("没有更多数据了");
                pb_load_progress.setVisibility(View.GONE);
                isOnceLoadData=false;
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
        adapter = new JiFenAdapter(activity);
        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // listview可见的最后一条的位置等于放入数据的条数，并停止滑动时，加载更多
                if (isOnceLoadData&&lastItem == transList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    getFeiDou();
//                    getHistoryOrder();
//                    Log.v("kan", "加载数据中。。。");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

/*    private void getHistoryOrder() {
        if(!NetWorkConnect.isConnect(this)){
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        HttpClientUtil.getInstance().doGetAsyn(MY_JIFEN_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                jifenResult = result;
                Message msg = Message.obtain();
                msg.what = MY_JIFEN_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }*/


/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MY_JIFEN_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (jifenResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, jifenResult);
                        startActivity(LoginActivity.class);
                    } else {
                        Gson gson = new Gson();
                        JiFenListBean data = gson.fromJson(jifenResult, JiFenListBean.class);
                        int number = data.getSize();
                        ((TextView)findViewById(R.id.total_jifen)).setText("当前飞豆："+data.getIntegral());
                        if (number > 0) {
                            transList.addAll(data.getList());
                            adapter.setItems(transList);
                             if(number<count){
                                 tv_load_more.setText("没有更多数据了");
                                 pb_load_progress.setVisibility(View.GONE);
                                 isOnceLoadData=false;
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

}
