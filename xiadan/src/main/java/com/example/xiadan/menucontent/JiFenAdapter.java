package com.example.xiadan.menucontent;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.example.xiadan.R;
import com.example.xiadan.bean.JiFenBean;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;


/**
 * Created by wlanjie on 14-6-18.
 */
public class JiFenAdapter extends SingleTypeAdapter<JiFenBean> {

    private final Activity activity;
    private Drawable drawable;

    /**
     * Create adapter
     *
     * @param activity
     */
    public JiFenAdapter(Activity activity) {
        super(activity, R.layout.layout_ji_fen_adapter);
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
                R.id.oder_number_text,//订单编号       0
                R.id.emp_name_text,// 快递员姓名   1
                R.id.jifen_text, //积分    2
                R.id.publish_time_text   //记录时间     3
        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final JiFenBean item) {
        if (item != null) {
            setText(0, "单号：" + item.getOrderNum());
            setText(1, "飞派员：" + item.getEmpName());
            setText(2, " 飞豆：" + item.getIntegral());
            setText(3, "时间：" + item.getPublishTime());
/*            setText(4,"流水帐号："+item.getSerial_number());
            setText(5,"申请时间："+item.getPublishTime());*/
        }
    }

}
