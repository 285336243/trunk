package com.example.xiadan.menucontent;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.xiadan.R;
import com.example.xiadan.bean.ConponBean;
import com.example.xiadan.bean.ConponBeanList;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.NetWorkConnect;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.VolleyUtil;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 我的优惠卷列表
 */
public class CouponActivity extends BaseActivity {
    /**
     * 优惠券列表
     */
    private static final String COUPON_LIST_URL = "page/api/v1/code/list/my";
    private static final int COUPON_LIST_CODE = 0X1D;
    /**
     * 绑定优惠卷
     */
    private static final String ADD_COUPON__URL = "page/api/v1/code/add";
    private static final int COUPON_ADD_CODE = 0X22;
    private Activity activity;
    private ListView listView;
    /**
     * 没有记录时的数据
     */
    private TextView noOrderEmptyview;
    private int page = 1;
    private int count = 10;
    /**
     * 优惠卷结果
     */
    private String couponList;
    private List<ConponBean> transList = new LinkedList<ConponBean>();
    private View moreView;
    /**
     * listview可见的最后一条的位置
     */
    private int lastItem;
    private TextView tv_load_more;
    private ProgressBar pb_load_progress;
    /**
     * 是否需要再次加载数据
     */
    private boolean isOnceLoadData = true;
    private CouponListAdapter adapter;

    /**
     * 添加请求的view
     */
    private View addView;
    /**
     * 填入优惠码
     */
    private EditText huiCode;
    /**
     * 添加优惠卷结果
     */
    private String couponAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coupon);
        actionBar.setTitle("我的优惠券");
        activity = CouponActivity.this;
        initUi();
        //得到优惠卷列表
//        getCouponList();
        getYouHuiList();
    }

    /**
     * 优惠卷列表
     */
    private void getYouHuiList() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        volley_get(COUPON_LIST_URL, map);
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        ConponBeanList data = gson.fromJson(response, ConponBeanList.class);
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

/*    private void getCouponList() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        HttpClientUtil.getInstance().doGetAsyn(COUPON_LIST_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                couponList = result;
                Message msg = Message.obtain();
                msg.what = COUPON_LIST_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }*/

    private void initUi() {
        addView = findViewById(R.id.add_request_text);
        huiCode = (EditText) findViewById(R.id.youhui_code_edit);

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = huiCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.show(activity, "请填入优惠码");
                    return;
                }
//                addCode(code);
                putCode(code);

            }
        });


        //记录列表
        listView = (ListView) findViewById(R.id.list_view);
        //空v记录显示的view
        noOrderEmptyview = (TextView) findViewById(R.id.no_youhui_text);
        //加载更多设置
//        ProgressDlgUtil.showProgressDlg(activity, "正在加载");
        LayoutInflater inflater = LayoutInflater.from(activity);
        moreView = inflater.inflate(R.layout.footer_more, null);
        tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);
        pb_load_progress = (ProgressBar) moreView.findViewById(R.id.pb_load_progress);
        tv_load_more.setText(R.string.activity_loading);
        pb_load_progress.setVisibility(View.VISIBLE);

        listView.addFooterView(moreView);
        adapter = new CouponListAdapter(activity);
        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // listview可见的最后一条的位置等于放入数据的条数，并停止滑动时，加载更多
                if (isOnceLoadData && lastItem == transList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    getYouHuiList();
//                    getCouponList();
//                    Log.v("kan", "加载数据中。。。");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    private void putCode(String code) {
        final HashMap<String, String> map = new HashMap<String, String>();
        // [必填] 登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(activity));
        // [必填] 优惠码
        map.put("code", code);
        VolleyUtil.getInstance(activity).volley_post(ADD_COUPON__URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                //status: 1，  // 操作成功
                ToastUtils.show(activity, "添加成功");
                //输入框置空
                huiCode.setText("");

                //更新列表
                if (!transList.isEmpty()) {
                    transList.clear();
                }
                getYouHuiList();
            }

            @Override
            public void error(VolleyError str) {
                if (str.networkResponse.statusCode == 401) {
                    startActivity(LoginActivity.class);
                } else {
                    ToastUtils.show(activity, "优惠码错误");
                }
            }
        });
    }

/*    private void addCode(String code) {
        final HashMap<String, String> map = new HashMap<String, String>();
        // [必填] 登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(activity));
        // [必填] 优惠码
        map.put("code", code);
        HttpClientUtil.getInstance().doPostAsyn(ADD_COUPON__URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                couponAdd = result;
                Message msg = Message.obtain();
                msg.what = COUPON_ADD_CODE;
                mHandler.sendMessage(msg);
            }
        });

    }*/

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
*//*                case COUPON_LIST_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (couponList.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, couponList);
                        startActivity(LoginActivity.class);
                    } else {
                        Gson gson = new Gson();
                        ConponBeanList data = gson.fromJson(couponList, ConponBeanList.class);
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
                    break;*//*
                case COUPON_ADD_CODE:
                    if (couponAdd.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, couponAdd);
                        ToastUtils.show(activity, "优惠码错误");
                    } else {
                        //status: 1，  // 操作成功
                        ToastUtils.show(activity, "添加成功");
                        //输入框置空
                        huiCode.setText("");

                        //更新列表
                        if (!transList.isEmpty()) {
                            transList.clear();
                        }
                        getYouHuiList();
//                        getCouponList();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/
}
