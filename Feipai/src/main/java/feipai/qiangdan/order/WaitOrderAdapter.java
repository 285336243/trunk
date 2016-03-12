package feipai.qiangdan.order;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;


import feipai.qiangdan.R;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.util.IConstant;


public class WaitOrderAdapter extends SingleTypeAdapter<OrderItem> {


    private final String reason;
    private int isForbid;
    private final Activity activity;

    /**
     * Create adapter
     *
     * @param activity 上下文
     */
    public WaitOrderAdapter(Activity activity, int isForbid, String reason) {
        super(activity, R.layout.layout_wait_order_adapter);
        this.isForbid = isForbid;
        this.activity = activity;
        this.reason = reason;
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
                R.id.text_publish_time,//下单时间 0
                R.id.text_overview_info,//概述信息 1
                R.id.text_two_address, //两个地址 2
                R.id.button_qiangdan,  //抢单按钮 3
                R.id.reason_text,     //禁止抢单原因 4
                R.id.text_type,           //订单类型 5
                R.id.to_distance,   //据您距离  6
                R.id.see_details,    //查看详情  7
                R.id.order_button_layout,  //查看详情  8
                R.id.order_already_rob,  //单已经被抢 9
                R.id.time_to_take,    // 定时取  10
                R.id.time_to_send,   //定时送  11
                R.id.single_item_layout //整个layout 12
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, final OrderItem item) {
        if (item != null) {
            view(10).setVisibility(View.GONE);
            view(11).setVisibility(View.GONE);
            //如果是定时的显示为黄色，否则显示为蓝色
            if (item.getIsTime() == 1 || item.getIsSong() == 1) {
                view(12).setBackgroundResource(R.drawable.time_background);
            } else {
                view(12).setBackgroundResource(R.drawable.rober_background);
            }
            if (item.getIsTime() == 1) {
                view(10).setVisibility(View.VISIBLE);
                setText(10, "指定取件时间：" + item.getjTime());
            }
            if (item.getIsSong() == 1) {
                view(11).setVisibility(View.VISIBLE);
                setText(11, "指定送达时间：" + item.getEndTime());
            }
            if ("送".equals(item.getType())) {

                textView(5).setText("【快送】");
                if (item.getIsSong() == 1) {
                    textView(5).setText("【定时送】");
                }
            }
            if ("买".equals(item.getType())) {

                textView(5).setText("【代买】");
            }
            setText(0, "下单时间：" + item.getPublishTime());
            setText(1, "：¥" + item.getPrice() + "元 /" + item.getDistance() + "公里/" + item.getWeight() + "公斤/" + item.getPaymentType());
            setText(2, "取件地址：" + item.getjAddress() + "\n收件地址：" + item.getsAddress());
            setText(6, "物品名称：" + item.getGoodsName() + "\n客户备注：" + item.getRemark());
            if (isForbid == 1) {
                view(7).setVisibility(View.GONE);
                setText(3, "禁止抢单");
                textView(3).setBackgroundResource(R.drawable.corner_button_grey);
                textView(3).setClickable(false);
                textView(4).setVisibility(View.VISIBLE);
                textView(4).setText("原因:" + reason);
            } else {
                textView(4).setVisibility(View.GONE);
                if (item.getCanRob() == 1) {
                    view(9).setVisibility(View.GONE);
                    view(3).setVisibility(View.VISIBLE);
                    view(3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(IConstant.ORDER_ITEM, item);
                            Intent mIntent = new Intent(activity, RoborderActivity.class);
                            mIntent.putExtras(bundle);
                            mIntent.putExtra(IConstant.FROM_HOME, true);
                            activity.startActivity(mIntent);

//                    activity.startActivity(new Intent(activity,OrderDetaisActivity.class).putExtra(IConstant.ORDER_ID,String.valueOf(item.getId())));
                        }
                    });
                } else {
                    view(3).setVisibility(View.GONE);
                    view(9).setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
