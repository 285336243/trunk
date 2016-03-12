package feipai.qiangdan.my;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import feipai.qiangdan.R;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.TransBean;

/**
 * Created by wlanjie on 14-6-18.
 */
public class HistoryTransAdapter extends SingleTypeAdapter<TransBean> {

    private final Activity activity;
    private Drawable drawable;

    /**
     * Create adapter
     *
     * @param activity
     */
    public HistoryTransAdapter(Activity activity) {
        super(activity, R.layout.layout_history_trans_adapter);
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
                R.id.name_text,//姓名       0
                R.id.account_text,//支付宝帐号   1
                R.id.amount_text, //转账金额    2
                R.id.state_text,  //转账状态     3
                R.id.water_number,//流水帐号   4
                R.id.application_time   //申请时间 5

        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final TransBean item) {
        if (item != null) {
               setText(0,"姓名："+item.getUserName_zfb());
            setText(1,"支付宝帐号："+item.getAccount_zfb());
            setText(2,"转账金额："+item.getMoney());
            setText(3,"状态："+item.getState());
            setText(4,"流水帐号："+item.getSerial_number());
            setText(5,"申请时间："+item.getPublishTime());
        }
    }

}
