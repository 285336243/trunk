package com.example.xiadan.menucontent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.example.xiadan.R;
import com.example.xiadan.bean.OrderItem;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.order.OrderDetailActivity;
import com.example.xiadan.order.ResultActivity;
import com.example.xiadan.util.DialogUtil;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;

import java.util.HashMap;


/**
 * Created by wlanjie on 14-6-18.
 */
public class OrderAdapter extends SingleTypeAdapter<OrderItem> {

    private static final String DELETE_URL = "page/api/v1/order/delete";
    private static final int DELETE_CODE = 0xe3;
    private final MyOderActivity activity;
    private Drawable drawable;
    /**
     * 删除数据结果
     */
    private String deleteResult;

    /**
     * Create adapter
     *
     * @param activity
     */
    public OrderAdapter(MyOderActivity activity) {
        super(activity, R.layout.layout_my_order_adapter);
        this.activity = activity;
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.order_number_text,//单号       0
                R.id.person_text,//人员   1
                R.id.state_code_text, //状态和收寄件码    2
                R.id.summary_text,  //订单概述     3
                R.id.confirm_order,//确认订单   4
                R.id.delete_order   //删除订单 5
        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final OrderItem item) {
        if (item != null) {
            setText(0, "单号：" + item.getOrderNum());
            setText(1, "收件人：" + item.getsName() + ", 寄件人：" + item.getjName() + ", 飞派员：" + item.getEmployee());
            if (!TextUtils.isEmpty(item.getjCode())) {
                setText(2, "状态：" + item.getState() + ",寄件码：" + item.getjCode() + ",收件码:" + item.getsCode());
            } else {
                setText(2, "状态：" + item.getState());
            }
            if (!TextUtils.isEmpty(item.getPaymentType())) {
                setText(3, "费用：" + item.getDistance() + "公里/" + item.getWeight() + "公斤/" + item.getPrice() + "元/" +
                        item.getPaymentType() + "/" + item.getType());
            } else {
                setText(3, "费用：" + item.getDistance() + "公里/" + item.getWeight() + "公斤/" + item.getPrice() + "元/" + item.getType());
            }
/*            setText(4,"流水帐号："+item.getSerial_number());
            setText(5,"申请时间："+item.getPublishTime());*/
            view(4).setVisibility(View.GONE);
            view(5).setVisibility(View.GONE);
            //删除按钮显示并点击响应
            if (item.getState().equals("未确认") || item.getState().equals("已完结")) {
                view(5).setVisibility(View.VISIBLE);
                view(5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.getInstance().showCancelDialog(activity, "确定删除此订单?").setLisener(new DialogUtil.CallBack() {
                            @Override
                            public void setConfirm() {
//                                ToastUtils.show(activity,"hahaha");
                                deleteOrder(item.getOrderNum());
                            }
                        });
                    }
                });

            }
            // 未确认的订单要确认显示并点击响应
            if (item.getState().equals("未确认")) {
                view(4).setVisibility(View.VISIBLE);
                view(4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        String costResult = gson.toJson(item);
                        activity.startActivity(new Intent(activity, ResultActivity.class).putExtra(IConstant.ORDER_RESULT, costResult));
                        activity.finish();
                        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
            }
            // 点击进入订单详情
            view(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, OrderDetailActivity.class).putExtra(IConstant.ORDER_ID, item.getId()));
                }
            });
        }
    }

    private void deleteOrder(String orderNum) {
        ProgressDlgUtil.showProgressDlg(activity, "正在加载...");
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(activity));//登录验证码
        map.put("orderNum", orderNum); // [必填] 订单编号
        HttpClientUtil.getInstance().doPutAsyn(DELETE_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                deleteResult = result;
                Message msg = Message.obtain();
                msg.what = DELETE_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (deleteResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, deleteResult);
                    } else {
                        // status:1  //操作成功
                        ToastUtils.show(activity, "删除成功");
                        //删除后刷新
                        activity.freshOrderList();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
