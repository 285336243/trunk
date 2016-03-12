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
import feipai.qiangdan.javabean.TransBean;
import feipai.qiangdan.javabean.TransList;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import roboguice.inject.InjectView;

/**
 * 我的订单
 */
public class MyHistoryTransActivity extends DialogFragmentActivity {

    private static final String HISTORY_ORDER_URL = "api/v1/transfer/list";
    private static final int HISTORY_TRANS_CODE = 0XAB1;
    @InjectView(R.id.list_view)
    private ListView listView;
    @InjectView(R.id.no_order_emptyview)
    private TextView noOrderEmptyview;

    @InjectView(R.id.about_back)
    private View backView;
    private int page = 1;
    private int count = 10;
    private String historyTrans;
    @Inject
    private Activity activity;
    List<TransBean> transList = new LinkedList<TransBean>();

    private View moreView;
    /**
     * listview可见的最后一条的位置
     */
    private int lastItem;
    private TextView tv_load_more;
    private ProgressBar pb_load_progress;
    private HistoryTransAdapter adapter;
    /**
     * 是否需要再次加载数据
     */
    private boolean isOnceLoadData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_history_transr);
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
        adapter = new HistoryTransAdapter(activity);
        listView.setAdapter(adapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // listview可见的最后一条的位置等于放入数据的条数，并停止滑动时，加载更多
                if (isOnceLoadData && lastItem == transList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    getHistoryOrder();
//                    Log.v("kan", "加载数据中。。。");
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
                historyTrans = result;
                Message msg = Message.obtain();
                msg.what = HISTORY_TRANS_CODE;
                mHandler.sendMessage(msg);
            }
        });*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        setData(response);
    }

    private void setData(String historyTrans) {
        Gson gson = new Gson();
        TransList data = gson.fromJson(historyTrans, TransList.class);
        int number = data.getSize();
        if (number > 0) {
//                            HistoryOrderAdapter adapter = new HistoryOrderAdapter(activity);
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

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HISTORY_TRANS_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (historyTrans.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, historyTrans);
                    } else {
                        Gson gson = new Gson();
                        TransList data = gson.fromJson(historyTrans, TransList.class);
                        int number = data.getSize();
                        if (number > 0) {
//                            HistoryOrderAdapter adapter = new HistoryOrderAdapter(activity);
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

}
