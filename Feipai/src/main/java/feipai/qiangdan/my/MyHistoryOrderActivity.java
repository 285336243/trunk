package feipai.qiangdan.my;

import android.app.Activity;
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
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.order.HandlerOrderAdapter;
import feipai.qiangdan.order.WaitOrderAdapter;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import roboguice.inject.InjectView;

/**
 * 我的订单
 */
public class MyHistoryOrderActivity extends DialogFragmentActivity {

    private static final String HISTORY_ORDER_URL = "api/v1/order/myendorder/list";
    private static final String ORDER_LIST = "api/v1/order/neworder";
    private static final int HISTORY_ORDER_CODE = 0XAB1;
    @InjectView(R.id.list_view)
    private ListView listView;
    @InjectView(R.id.no_order_emptyview)
    private TextView noOrderEmptyview;

    @InjectView(R.id.about_back)
    private View backView;
    private int page = 1;
    private int count = 10;
    private String historyOrder;
    @Inject
    private Activity activity;
    List<OrderItem> orderItemList = new LinkedList<OrderItem>();

    private View moreView;
    /**
     * listview可见的最后一条的位置
     */
    private int lastItem;
    private TextView tv_load_more;
    private ProgressBar pb_load_progress;
    private HistoryOrderAdapter adapter;
    /**
     * 是否需要再次加载数据
     */
    private boolean isOnceLoadData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_history_order);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        noOrderEmptyview.setVisibility(View.VISIBLE);
//        listView.setEmptyView(noOrderEmptyview);
        initView();
        getHistoryOrder();
    }

    private void initView() {
//        ProgressDlgUtil.showProgressDlg(activity, "正在加载");
        LayoutInflater inflater = LayoutInflater.from(activity);
        moreView = inflater.inflate(R.layout.footer_more, null);
        tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);
        pb_load_progress = (ProgressBar) moreView.findViewById(R.id.pb_load_progress);
        tv_load_more.setText(R.string.activity_loading);
        pb_load_progress.setVisibility(View.VISIBLE);

        listView.addFooterView(moreView);
        adapter = new HistoryOrderAdapter(activity);
        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // listview可见的最后一条的位置等于放入数据的条数，并停止滑动时，加载更多
                if (isOnceLoadData && lastItem == orderItemList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    getHistoryOrder();
                    Log.v("kan", "加载数据中。。。");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }


    private void getHistoryOrder() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("pageIndex", String.valueOf(page));// [必填] 当前页（分页用，1开始）
        map.put("pageSize", String.valueOf(count));// [必填] 每页显示条数（分页用）
        volley_get(HISTORY_ORDER_URL, map);
/*        HttpClientUtil.getInstance().doGetAsyn(HISTORY_ORDER_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                historyOrder = result;
                Message msg = Message.obtain();
                msg.what = HISTORY_ORDER_CODE;
                mHandler.sendMessage(msg);
            }
        });*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        OrderListBean data = gson.fromJson(response, OrderListBean.class);
        int number = data.getSize();
        if (number > 0) {
            orderItemList.addAll(data.getList());
            adapter.setItems(orderItemList);
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


/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HISTORY_ORDER_CODE:
//                    ProgressDlgUtil.stopProgressDlg();
                    if (historyOrder.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(activity, historyOrder);
                    } else {
                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(historyOrder, OrderListBean.class);
                        int number = data.getSize();
                        if (number > 0) {
//                            HistoryOrderAdapter adapter = new HistoryOrderAdapter(activity);
                            orderItemList.addAll(data.getList());
                            adapter.setItems(orderItemList);
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
