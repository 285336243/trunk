package com.example.xiadan.menucontent;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.example.xiadan.R;
import com.example.xiadan.bean.ConponBean;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;


/**
 * Created by wlanjie on 14-6-18.
 */
public class CouponListAdapter extends SingleTypeAdapter<ConponBean> {

    private final Activity activity;
    private Drawable drawable;

    /**
     * Create adapter
     *
     * @param activity
     */
    public CouponListAdapter(Activity activity) {
        super(activity, R.layout.layout_coupon_list_adapter);
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
                R.id.code_text,//优惠码       0
                R.id.stated_text,//状态   1
                R.id.price_text, //抵扣金额    2
                R.id.useprice_text,  //使用限额     3
                R.id.use_interval //使用时间   4
                //  R.id.application_time   //申请时间 5

        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final ConponBean item) {
        if (item != null) {
            setText(0, "  优惠码： " + item.getCode());
            setText(1, "    状态： " + item.getState());
            setText(2, "抵扣金额： " + item.getPrice());
            setText(3, "使用限额： " + item.getUsePrice());
            setText(4, "使用时间： " + item.getStartTime() + "--" + item.getEndTime());
        }
    }

}
